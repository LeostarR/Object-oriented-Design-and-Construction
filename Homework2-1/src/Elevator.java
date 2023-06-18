import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private final int building;

    private int passengerNum = 0;

    private int nowFloor = 1;

    private int targetFloor = 1;

    private int direction = 0;

    private final PassengerQueue queue;

    private final ArrayList<Passenger> list = new ArrayList<>();

    public Elevator(int building, PassengerQueue passengerQueue) {
        this.building = building;
        this.queue = passengerQueue;
    }

    @Override
    public void run() {
        while (true) {
            if (this.queue.isEnd() && this.queue.isEmpty()
                    && this.targetFloor == this.nowFloor && this.isEmpty()) {
                return;
            }
            Passenger passenger = queue.getOnePassenger();
            if (passenger == null) {
                continue;
            }
            this.setTarget(passenger.getFromFloor());//最早到达设为主请求，接人
            forward(passenger);
            this.setTarget(passenger.getToFloor());//最早到达设为主请求，送人
            forward(passenger);
            while (!this.list.isEmpty()) {
                this.setTarget(this.list.get(0).getToFloor());
                Passenger p = this.list.get(0);
                this.list.remove(p);
                forward(p);
            }
        }
    }

    private void forward(Passenger p) {
        while (this.nowFloor != this.targetFloor) { //捎带：方向一致，目标小于主目标
            if (openOrNot()) {
                this.open();
                this.stream();
                this.close();
            }
            this.move();
        }
        //到达指定层
        this.open();
        if (p.getToFloor() == this.targetFloor) {
            this.out(p);
        }
        ArrayList<Passenger> l = new ArrayList<>(list);
        for (Passenger passenger : l) {
            if (passenger.getToFloor() == this.nowFloor) {
                this.out(passenger);
            }
        }
        if (p.getFromFloor() == this.targetFloor) {
            this.in(p);
        }
        if (!this.isFull()) {
            int num = 6 - this.passengerNum;
            int cnt = 0;
            for (int i = 0;i < queue.getList().size();i++) {
                Passenger pa = queue.getList().get(i);
                int dir = Integer.signum(pa.getToFloor() - pa.getFromFloor());
                if (pa.getFromFloor() == this.nowFloor && direction ==  dir) {
                    cnt++;
                    if (cnt > num) {
                        break;
                    }
                    this.in(pa);
                    queue.removePassenger(pa);
                }
            }
        }
        this.close();
    }

    private void open() {
        TimableOutput.println("OPEN-" + nowFloor + "-" + building);
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
        TimableOutput.println("CLOSE-" + nowFloor + "-" + building);
    }

    private void arrive() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("ARRIVE-" + nowFloor + "-" + building);
    }

    private void stream() { //管理in 和 out
        ArrayList<Passenger> l = new ArrayList<>(list);
        for (Passenger p : l) {
            if (p.getToFloor() == this.nowFloor) {
                this.out(p);
            }
        }
        if (!this.isFull()) {
            int num = 6 - this.passengerNum;
            int cnt = 0;
            for (int i = 0;i < queue.getList().size();i++) {
                Passenger p = queue.getList().get(i);
                int dir = Integer.signum(p.getToFloor() - p.getFromFloor());
                if (p.getFromFloor() == this.nowFloor && direction ==  dir) {
                    cnt++;
                    if (cnt > num) {
                        break;
                    }
                    this.in(p);
                    queue.removePassenger(p);
                }
            }
        }
    }

    private void in(Passenger p) {
        this.addPassenger(p);
        TimableOutput.println("IN-" + p.getId() + "-" + nowFloor + "-" + building);
    }

    private void out(Passenger p) {
        this.removePassenger(p);
        TimableOutput.println("OUT-" + p.getId() + "-" + nowFloor + "-" + building);
    }

    private void addPassenger(Passenger p) {
        while (this.isFull()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.list.add(p);
        this.passengerNum++;
    }

    private void removePassenger(Passenger p) {
        this.list.remove(p);
        this.passengerNum--;
    }

    private void setTarget(int floor) {
        this.targetFloor = floor;
        this.direction = Integer.signum(this.targetFloor - this.nowFloor);
    }

    public boolean isFull() {
        return this.passengerNum == 6;
    }

    public boolean isEmpty() {
        return this.passengerNum == 0;
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
            return;
        }
    }

    private boolean openOrNot() {
        int flag1 = 0; //有人到达
        for (Passenger p : this.list) {
            if (p.getToFloor() == this.nowFloor) {
                flag1 = 1;
                break;
            }
        }
        int flag2 = 0; //可捎带
        ArrayList<Passenger> l = this.queue.getList();
        for (Passenger p : l) {
            int dir = Integer.signum(p.getToFloor() - p.getFromFloor());
            if (dir == this.direction) {
                flag2 = 1;
                break;
            }
        }
        return flag1 == 1 || flag2 == 1;
    }

}
