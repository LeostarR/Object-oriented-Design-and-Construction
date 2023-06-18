import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private final int elevatorId;
    private final int capacity;
    private final double speed;
    private int passengerNum = 0;
    private int nowFloor;
    private int targetFloor;
    private int direction = 0;
    private int maintainFlag;
    private final PassengerQueue queue;
    private final PassengerQueue waitQueue;
    private final PassengerQueue table;
    private final ArrayList<PersonRequest> list = new ArrayList<>();

    public Elevator(int elevatorId, int fromFloor, int capacity, double speed,
                    PassengerQueue q, PassengerQueue waitQueue, PassengerQueue table) {
        this.elevatorId = elevatorId;
        this.nowFloor = fromFloor;
        this.targetFloor = fromFloor;
        this.capacity = capacity;
        this.speed = speed;
        this.queue = q;
        this.waitQueue = waitQueue;
        this.table = table;
    }

    public boolean isFull() {
        return this.passengerNum == this.capacity;
    }

    public boolean isEmpty() {
        return this.passengerNum == 0;
    }

    private void setTarget(int floor) {
        this.targetFloor = floor;
        this.direction = Integer.signum(this.targetFloor - this.nowFloor);
    }

    @Override
    public void run() {
        while (true) {
            if (this.queue.isEnd() && this.queue.isEmpty() && this.targetFloor == this.nowFloor
                    && this.isEmpty()) {
                return;
            }
            if (this.targetFloor == this.nowFloor && this.isEmpty() && this.queue.isMaintained()
                    && this.queue.isEmpty()) {
                TimableOutput.println("MAINTAIN_ABLE-" + this.elevatorId); //电梯无人时维护
                return;
            }
            //TimableOutput.println(elevatorId + "loop");
            PersonRequest person = queue.getOnePassenger();
            if (person == null) {
                continue;
            }
            this.maintainFlag = 0;
            this.setTarget(person.getFromFloor());//最早到达设为主请求，接人
            forward(person);
            if (this.maintainFlag == 1) {
                continue;
            }
            this.setTarget(person.getToFloor());//最早到达设为主请求，送人
            forward(person);
            if (this.maintainFlag == 1) {
                continue;
            }
            while (!this.list.isEmpty()) {
                this.setTarget(this.list.get(0).getToFloor());
                PersonRequest personRequest = this.list.get(0);
                //this.list.remove(personRequest);
                forward(personRequest);
                if (this.maintainFlag == 1) {
                    break;
                }
            }
            //TimableOutput.println(elevatorId + "loopend");
        }
    }

    private void forward(PersonRequest p) {         //p为主请求
        while (this.nowFloor != this.targetFloor) { //捎带：方向一致，目标小于主目标
            if (this.queue.isMaintained()) {
                if (this.isEmpty()) { //无人，无需开关门，将p重新加入waitQueue
                    this.waitQueue.addPassenger(p);
                    this.waitQueue.addMaintainList(this.queue.getList());
                    this.queue.clear();
                    this.setTarget(this.nowFloor);
                    this.maintainFlag = 1;
                    return;
                }
                this.open();//有人，需将这些人重新加入waitQueue中
                this.dealMaintain(p);
                this.close();
                return;
            }
            if (openOrNot()) {
                this.open();
                this.stream(p);
                this.close();
            }
            this.move();
        }
        this.open();
        if (p.getToFloor() == this.targetFloor) {
            this.out(p);
        }
        ArrayList<PersonRequest> l = new ArrayList<>(list);
        for (PersonRequest person : l) {
            if (person.getToFloor() == this.nowFloor) {
                this.out(person);
            }
        }
        if (p.getFromFloor() == this.targetFloor && !this.isFull() && !list.contains(p)) {
            this.in(p);
        }
        if (!this.isFull()) {
            int num = capacity - this.passengerNum;
            if (!this.list.contains(p)) {
                num--;
            }
            dealPick(num);
        }
        if (this.queue.isMaintained()) {
            this.dealMaintain(p);
            this.close();
            return;
        }
        this.close();
    }

    private void dealMaintain(PersonRequest p) {
        ArrayList<PersonRequest> arr = new ArrayList<>(list);
        ArrayList<PersonRequest> maintain = new ArrayList<>();//仅删去到达目的地的乘客
        int flag = 0;
        if (!arr.contains(p) && table.getList().contains(p)) {
            flag = 1;
        }
        for (PersonRequest person : arr) {
            if (person.getToFloor() == this.nowFloor) {
                this.out(person);//会修改table
                continue;
            }
            PersonRequest personRequest =
                    new PersonRequest(nowFloor, person.getToFloor(), person.getPersonId());
            table.addPassenger(personRequest);
            table.removePassenger(person);
            maintain.add(personRequest);
            this.list.remove(person);//会替换table和maintain中乘客的属性
            this.passengerNum--;
            TimableOutput
                    .println("OUT-" + person.getPersonId() + "-" + nowFloor + "-" + elevatorId);
        }
        if (flag == 1) { //p没进电梯之前不在list和queue中，需单独考虑
            maintain.add(p);
        }
        maintain.addAll(this.queue.getList());
        this.queue.clear();
        this.waitQueue.addMaintainList(maintain);
        this.setTarget(this.nowFloor);
        this.maintainFlag = 1;
    }

    private void dealPick(int num) { //处理捎带
        int cnt = 0;
        for (int i = 0;i < this.queue.getList().size();i++) {
            PersonRequest person = this.queue.getList().get(i);
            int dir = Integer.signum(person.getToFloor() - person.getFromFloor());
            if (person.getFromFloor() == this.nowFloor && dir == this.direction) {
                cnt++;
                if (cnt > num) {
                    break;
                }
                this.in(person);
                this.queue.removePassenger(person);
            }
        }
    }

    private boolean openOrNot() {
        int flag1 = 0; //有人到达
        for (PersonRequest person : this.list) {
            if (person.getToFloor() == this.nowFloor) {
                flag1 = 1;
                break;
            }
        }
        int flag2 = 0; //有人可捎带
        ArrayList<PersonRequest> l = new ArrayList<>(queue.getList());
        for (PersonRequest person : l) {
            int dir = Integer.signum(person.getToFloor() - person.getFromFloor());
            if (dir == this.direction && person.getFromFloor() == nowFloor) {
                flag2 = 1;
                break;
            }
        }
        return flag1 == 1 || flag2 == 1;
    }

    private void open() {
        TimableOutput.println("OPEN-" + this.nowFloor + "-" + this.elevatorId);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("CLOSE-" + this.nowFloor + "-" + this.elevatorId);
    }

    private void stream(PersonRequest p) { //控制非主请求的人流进出
        ArrayList<PersonRequest> l = new ArrayList<>(list);
        for (PersonRequest person : l) {
            if (person.getToFloor() == this.nowFloor) {
                this.out(person);
            }
        }
        if (!this.isFull()) {
            int num = capacity - this.passengerNum;
            if (!this.list.contains(p) && table.getList().contains(p)) {
                num--;
            }
            dealPick(num);
        }
    }

    private void in(PersonRequest person) {
        this.addPassenger(person);
        TimableOutput.println("IN-" + person.getPersonId() + "-" + nowFloor + "-" + elevatorId);
    }

    private void out(PersonRequest person) {
        this.removePassenger(person);
        TimableOutput.println("OUT-" + person.getPersonId() + "-" + nowFloor + "-" + elevatorId);
    }

    private void addPassenger(PersonRequest person) {
        this.list.add(person);
        this.passengerNum++;
    }

    private void removePassenger(PersonRequest person) {
        this.list.remove(person);
        this.table.removePassenger(person);
        this.passengerNum--;
    }

    public void move() {
        if (this.direction == 1 && this.nowFloor < this.targetFloor && this.nowFloor < 11) {
            this.nowFloor++;
            if (this.nowFloor == this.targetFloor) {
                this.direction = 0;
            }
            this.arrive();
            return;
        }
        if (this.direction == 1 && (this.nowFloor == 11 || this.nowFloor == this.targetFloor)) {
            this.direction = 0;
            this.arrive();
            return;
        }
        if (this.direction == -1 && this.nowFloor > this.targetFloor && this.nowFloor > 1) {
            this.nowFloor--;
            if (this.nowFloor == this.targetFloor) {
                this.direction = 0;
            }
            this.arrive();
            return;
        }
        if (this.direction == -1 && (this.nowFloor == 1 || this.nowFloor == this.targetFloor)) {
            this.direction = 0;
            this.arrive();
        }
    }

    private void arrive() {
        try {
            Thread.sleep((long) (1000 * speed));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("ARRIVE-" + this.nowFloor + "-" + this.elevatorId);
    }
}
