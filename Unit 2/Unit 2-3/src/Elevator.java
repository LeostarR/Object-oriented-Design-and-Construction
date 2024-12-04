import com.oocourse.elevator3.TimableOutput;
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
    private final ArrayList<Passenger> list = new ArrayList<>();
    private final StateMap stateMap;
    private final ArrayList<Integer> accessList;

    public Elevator(int elevatorId, int fromFloor, int capacity, double speed, PassengerQueue q,
                    PassengerQueue waitQueue, PassengerQueue table, StateMap stateMap) {
        this.elevatorId = elevatorId;
        this.nowFloor = fromFloor;
        this.targetFloor = fromFloor;
        this.capacity = capacity;
        this.speed = speed;
        this.queue = q;
        this.waitQueue = waitQueue;
        this.table = table;
        this.stateMap = stateMap;
        this.accessList = stateMap.getAccessList(this.elevatorId);
    }

    public boolean isFull() {
        return this.passengerNum == this.capacity;
    }

    public boolean isEmpty() {
        return this.passengerNum == 0;
    }

    public boolean canAccess(int floor) {
        return this.accessList.contains(floor);
    }

    private void setTarget(int floor) {
        this.targetFloor = floor;
        this.direction = Integer.signum(this.targetFloor - this.nowFloor);
    }

    @Override
    public void run() {
        while (true) {
            if (table.isEmpty() && waitQueue.getEndFlag() == 1) {
                waitQueue.setEnd(true);
            }
            if (this.queue.isEnd() && this.queue.isEmpty()
                    && this.targetFloor == this.nowFloor && this.isEmpty() && table.isEmpty()) {
                return;
            }
            if (this.targetFloor == this.nowFloor && this.isEmpty()
                    && this.queue.isMaintained() && this.queue.isEmpty()) {
                TimableOutput.println("MAINTAIN_ABLE-" + this.elevatorId); //电梯无人时维护
                return;
            }
            Passenger passenger = queue.getOnePassenger();
            if (passenger == null) {
                continue;
            }
            this.maintainFlag = 0;
            this.setTarget(passenger.getFromFloor());//最早到达设为主请求，接人
            forward(passenger);
            if (this.maintainFlag == 1) {
                continue;
            }
            this.setTarget(passenger.getDemand());//最早到达设为主请求，送人
            forward(passenger);
            if (this.maintainFlag == 1) {
                continue;
            }
            while (!this.list.isEmpty()) {
                this.setTarget(this.list.get(0).getDemand());
                Passenger p = this.list.get(0);
                forward(p);
                if (this.maintainFlag == 1) {
                    break;
                }
            }
        }
    }

    private void forward(Passenger p) {         //p为主请求
        onTheWay(p);//执行完毕后targetFloor == nowFloor
        if (this.maintainFlag == 1) {
            return;
        }
        int receiveFlag = 0;
        if (this.onlyReceive(p)) {
            this.stateMap.addOnly(nowFloor);
            receiveFlag = 1;
        }
        this.open();
        int flag = 0;
        if (p.getDemand() == this.targetFloor) {
            this.out(p);
            flag = 1;
        }
        ArrayList<Passenger> l = new ArrayList<>(list);
        for (Passenger passenger : l) {
            if (passenger.getDemand() == this.nowFloor) {
                this.out(passenger);
            }
        }
        if (p.getFromFloor() == this.targetFloor && !this.isFull() &&
                !list.contains(p) && canAccess(p.getDemand())
                && flag == 0 && table.getList().contains(p)) {
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
            if (receiveFlag == 1) {
                this.stateMap.removeOnly(nowFloor);
            }
            return;
        }
        this.close();
        if (receiveFlag == 1) {
            this.stateMap.removeOnly(nowFloor);
        }
    }

    private void onTheWay(Passenger p) {
        while (this.nowFloor != this.targetFloor) { //捎带：方向一致，目标小于主目标
            if (this.queue.isMaintained()) { //强制开门，无论是否可达
                if (this.isEmpty()) { //无人，无需开关门，将p重新加入waitQueue
                    p.setMaintain(1);
                    this.queue.setMain(1);
                    this.waitQueue.addPassenger(p);
                    this.waitQueue.addMaintainList(this.queue.getList());
                    this.queue.clear();
                    this.setTarget(this.nowFloor);
                    this.maintainFlag = 1;
                    return;
                }
                int receiveFlag = 0;
                if (this.onlyReceive(p)) {
                    this.stateMap.addOnly(nowFloor);
                    receiveFlag = 1;
                }
                this.open();//有人，需将这些人重新加入waitQueue中
                this.dealMaintain(p);
                this.close();
                if (receiveFlag == 1) {
                    this.stateMap.removeOnly(nowFloor);
                }
                return;
            }
            if (openOrNot()) {
                int receiveFlag = 0;
                if (this.onlyReceive(p)) {
                    this.stateMap.addOnly(nowFloor);
                    receiveFlag = 1;
                }
                this.open();
                this.stream(p);
                this.close();
                if (receiveFlag == 1) {
                    this.stateMap.removeOnly(nowFloor);
                }
            }
            this.move();
        }
    }

    private void dealMaintain(Passenger p) {
        ArrayList<Passenger> arr = new ArrayList<>(list);
        ArrayList<Passenger> maintain = new ArrayList<>();//仅删去到达目的地的乘客
        int flag = 0;
        if (!arr.contains(p) && table.getList().contains(p)) {
            flag = 1;
        }
        for (Passenger passenger : arr) {
            if (passenger.getDemand() == this.nowFloor) {
                passenger.setMaintain(1);
                this.out(passenger);//会修改table
                continue;
            }
            Passenger pa =
                    new Passenger(nowFloor, passenger.getToFloor(), passenger.getPersonId());
            table.addPassenger(pa);
            table.removePassenger(passenger);
            pa.setMaintain(1);
            maintain.add(pa);
            this.list.remove(passenger);//会替换table和maintain中乘客的属性
            this.passengerNum--;
            TimableOutput
                    .println("OUT-" + passenger.getPersonId() + "-" + nowFloor + "-" + elevatorId);
        }
        if (flag == 1) { //p没进电梯之前不在list和queue中，需单独考虑
            p.setMaintain(1);
            maintain.add(p);
        }
        this.queue.setMain(1);
        maintain.addAll(this.queue.getList());
        this.queue.clear();
        this.waitQueue.addMaintainList(maintain);
        this.setTarget(this.nowFloor);
        this.maintainFlag = 1;
    }

    private void dealPick(int num) { //处理捎带
        int cnt = 0;
        for (int i = 0;i < this.queue.getList().size();i++) {
            Passenger passenger = this.queue.getList().get(i);
            int dir = Integer.signum(passenger.getDemand() - passenger.getFromFloor());
            if (passenger.getFromFloor() == this.nowFloor && dir == this.direction
                    && this.canAccess(passenger.getDemand())) {
                cnt++;
                if (cnt > num) {
                    break;
                }
                this.in(passenger);
                this.queue.removePassenger(passenger);
            }
        }
    }

    private boolean openOrNot() {
        int flag1 = 0; //有人到达
        for (Passenger passenger : this.list) {
            if (passenger.getDemand() == this.nowFloor) {
                flag1 = 1;
                break;
            }
        }
        int flag2 = 0; //有人可捎带
        ArrayList<Passenger> l = new ArrayList<>(queue.getList());
        for (Passenger passenger : l) {
            int dir = Integer.signum(passenger.getDemand() - passenger.getFromFloor());
            if (dir == this.direction && passenger.getFromFloor() == nowFloor
                    && canAccess(passenger.getDemand()) && canAccess(nowFloor)) {
                flag2 = 1;
                break;
            }
        }
        boolean flag3 = this.canAccess(this.nowFloor);
        return (flag1 == 1 || flag2 == 1) && flag3;
    }

    private boolean onlyReceive(Passenger p) { //只接人
        int flag1 = 0;
        for (Passenger passenger : this.list) {
            if (passenger.getDemand() == this.nowFloor) {
                flag1 = 1;
                break;
            }
        }
        int flag2 = 0;
        if (p.getDemand() == nowFloor && this.list.contains(p)) {
            flag2 = 1;
        }
        return flag1 == 0 && flag2 == 0;
    }

    private void open() {
        this.stateMap.addService(this.nowFloor);
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
        this.stateMap.removeService(this.nowFloor);
    }

    private void stream(Passenger p) { //控制非主请求的人流进出
        ArrayList<Passenger> l = new ArrayList<>(list);
        for (Passenger passenger : l) {
            if (passenger.getDemand() == this.nowFloor) {
                this.out(passenger);
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

    private void in(Passenger passenger) {
        this.addPassenger(passenger);
        TimableOutput.println("IN-" + passenger.getPersonId() + "-" + nowFloor + "-" + elevatorId);
    }

    private void out(Passenger passenger) {
        this.removePassenger(passenger);
        TimableOutput.println("OUT-" + passenger.getPersonId() + "-" + nowFloor + "-" + elevatorId);
    }

    private void addPassenger(Passenger passenger) {
        this.list.add(passenger);
        this.passengerNum++;
    }

    private void removePassenger(Passenger passenger) {
        this.list.remove(passenger);
        int target = passenger.getDemand();
        passenger.setDemand();
        if (passenger.isEmptyDe() && target == passenger.getToFloor()) {
            this.table.removePassenger(passenger);
        } else {
            passenger.setFromFloor(nowFloor);
            this.waitQueue.addPassenger(passenger);
        }
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
