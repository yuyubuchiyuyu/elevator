import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.max;

public class Elevator extends Thread {
    private final Integer id;
    private final char type; // I/A/B
    private Elevator friend;
    private Integer moveTime;
    private Integer capacity;
    private Integer nowFloor;
    private Integer nextFloor;
    private Integer direction; // 0停止，1上行，-1下行
    private final Integer transferFlag; // 是否有换乘楼层
    private final Integer transferFloor; // 换乘楼层
    private final Integer lowFloor;
    private final Integer highFloor;
    private int normalReset = 0; // 0表示不重置，1表示开启重置
    private int doubleReset = 0; // 0表示不重置，1表示开启重置
    private final RequestPool outRequest = new RequestPool(); // 电梯外面的请求
    private final RequestPool inRequest = new RequestPool();// 电梯里面的请求
    private final RequestPool normalRequestPool = new RequestPool();
    private final RequestPool requestPoolA = new RequestPool();
    private final RequestPool requestPoolB = new RequestPool();
    private Integer endSignal = 0; // 结束信号
    private Integer doorState = 0; //电梯门的状态，0关闭，1开着
    private long leavePoint;
    private long openPoint;
    private long closePoint;
    private int count = 0;
    private int newCapacity;
    private int newMoveTime;
    private int newTransferFloor;
    private final RequestPool globalRequest;
    private final ArrayList<Elevator> elevators;

    public Elevator(ArrayList<Elevator> elevators, Integer id,
                    char type, int moveTime, int capacity, int transferFlag, int transferFloor,
                    RequestPool requestPool, RequestPool globalRequest) {
        this.id = id;
        this.type = type;
        this.moveTime = moveTime;
        this.capacity = capacity;
        this.transferFlag = transferFlag;
        this.direction = 0;
        if (this.transferFlag == 0) { // 不是换乘电梯
            this.nowFloor = 1;
            this.lowFloor = 1;
            this.highFloor = 11;
            this.transferFloor = -1;
        } else { // 是换乘电梯
            this.transferFloor = transferFloor;
            if (this.type == 'A') {
                this.nowFloor = transferFloor - 1;
                this.lowFloor = 1;
                this.highFloor = transferFloor;
            } else {
                this.nowFloor = transferFloor + 1;
                this.lowFloor = transferFloor;
                this.highFloor = 11;
            }
        }
        synchronized (outRequest) {
            for (int i = 0; i < requestPool.getSize(); i++) {
                PersonRequest request = new PersonRequest(
                        requestPool.getRequest(i).getFromFloor(),
                        requestPool.getRequest(i).getToFloor(),
                        requestPool.getRequest(i).getPersonId());
                Print.printReceive(this, request);
                outRequest.addRequest(request);
            }
            outRequest.notifyAll();
        }
        this.globalRequest = globalRequest;
        this.elevators = elevators;
        this.leavePoint = System.currentTimeMillis();
    }

    public void setFriend(Elevator elevator) {
        this.friend = elevator;
    }

    public void Up() {
        int lastFloor = nowFloor;
        int flag = 0; // 是否上行
        if (transferFlag == 0) { // 不是换乘电梯
            if (nowFloor + 1 <= 11) {
                flag = 1;
            }
        } else { // 是换乘电梯
            if (type == 'A') {
                if (nowFloor + 1 == transferFloor) { // 将要行至换乘层
                    if (!Objects.equals(friend.nowFloor, transferFloor)
                            && !Objects.equals(friend.nextFloor, transferFloor)) { // 未被占用
                        flag = 1;
                    } else { // 已被占用
                        synchronized (transferFloor) {
                            try {
                                transferFloor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else if (nowFloor + 1 < transferFloor) { // 下区上行
                    flag = 1;
                }
            } else if (type == 'B') {
                if (nowFloor + 1 <= 11) { // 上区上行
                    flag = 1;
                }
            }
        }
        if (flag == 1) {
            nextFloor = nowFloor + 1;
            direction = 1;
            Arrive("up");
        }
        if (transferFlag == 1 && lastFloor == transferFloor) {
            synchronized (friend.transferFloor) {
                friend.transferFloor.notifyAll();
            }
        }
    }

    public void Down() {
        int lastFloor = nowFloor;
        int flag = 0;
        if (transferFlag == 0) { // 不是换乘电梯
            if (nowFloor - 1 >= 1) {
                flag = 1;
            }
        } else { // 是换乘电梯
            if (type == 'A') {
                if (nowFloor - 1 >= 1) {
                    flag = 1;
                }
            } else if (type == 'B') {
                if (nowFloor - 1 == transferFloor) { // 将要行至换乘层
                    if (!Objects.equals(friend.nowFloor, transferFloor)
                            && !Objects.equals(friend.nextFloor, transferFloor)) { // 未被占用
                        flag = 1;
                    } else { // 已被占用
                        synchronized (transferFloor) {
                            try {
                                transferFloor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else if (nowFloor - 1 > transferFloor) { // 上区下行
                    flag = 1;
                }
            }
        }
        if (flag == 1) {
            nextFloor = nowFloor - 1;
            direction = -1;
            Arrive("down");
        }
        if (transferFlag == 1 && lastFloor == transferFloor) {
            synchronized (friend.transferFloor) {
                friend.transferFloor.notifyAll();
            }
        }
    }

    public void Arrive(String str) {
        long arrivePoint = System.currentTimeMillis();
        try {
            sleep(max(moveTime - (arrivePoint - leavePoint), 0));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (str.equals("up")) {
            nowFloor++;
        } else {
            nowFloor--;
        }
        Print.printArrive(this);
        leavePoint = System.currentTimeMillis();
        if (normalReset == 1 || doubleReset == 1) {
            count++;
            if (count == 2) {
                resetBegin();
            }
        }
    }

    public void Open() {
        if (Operation.judgeOpen(type, capacity, direction,
                transferFlag, transferFloor, nowFloor, inRequest, outRequest) == 1) { // 开门
            Print.printOpen(this);
            openPoint = System.currentTimeMillis();
            doorState = 1;
            RequestPool requestTemp = Operation.open_A(this, type, nowFloor, capacity, transferFlag,
                    transferFloor, inRequest, outRequest);
            synchronized (globalRequest) {
                for (int i = 0; i < requestTemp.getSize(); i++) {
                    globalRequest.addRequest(requestTemp.getRequest(i));
                }
                globalRequest.notifyAll();
            }
            requestTemp = Operation.open_B(this, type, nowFloor, capacity, transferFlag,
                    transferFloor, inRequest, outRequest);
            synchronized (globalRequest) {
                for (int i = 0; i < requestTemp.getSize(); i++) {
                    globalRequest.addRequest(requestTemp.getRequest(i));
                }
                globalRequest.notifyAll();
            }
            Operation.open_1(this, nowFloor, inRequest);
            Operation.open_2(this, capacity, direction, nowFloor, inRequest, outRequest);
            Operation.open_25(this, nowFloor, inRequest, outRequest);
            Operation.open_3(this, capacity, direction,
                    nowFloor, inRequest, outRequest);
            synchronized (globalRequest) {
                globalRequest.notifyAll();
            }
        }
    }

    public void Close() {
        if (doorState == 1) { // 关门
            closePoint = System.currentTimeMillis();
            try {
                sleep(max(400 - (closePoint - openPoint), 0));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Print.printClose(this);
            leavePoint = System.currentTimeMillis();
            doorState = 0;
        }
    }

    public void run() {
        while (true) {
            if (inRequest.getSize() == 0 && (normalReset == 1 || doubleReset == 1)) {
                resetBegin();
            }
            if (transferFlag == 1 && Objects.equals(nowFloor, transferFloor)
                    && inRequest.getSize() == 0 && outRequest.getSize() == 0 && endSignal != 1) {
                // 未完全结束时空电梯不许停在换乘层
                if (type == 'A') {
                    Down();
                } else if (type == 'B') {
                    Up();
                }
            }
            if (inRequest.getSize() != 0) { // 电梯里有人
                sendPerson();
            } else { // 电梯里没人
                if (outRequest.getSize() != 0) { // 电梯外有人
                    Operation.searchOutMain(direction, nowFloor, outRequest);
                    pickPerson();
                } else { // 电梯外没人
                    if (endSignal == 1) {
                        return;
                    } else {
                        synchronized (outRequest) {
                            try {
                                outRequest.wait();
                                leavePoint = System.currentTimeMillis();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public void sendPerson() {
        if (inRequest.getRequest(0).getToFloor() > nowFloor) { // 上行送人
            Up();
        } else if (inRequest.getRequest(0).getToFloor() < nowFloor) { // 下行送人
            Down();
        }
        Open();
        Close();
    }

    public void pickPerson() {
        if (outRequest.getRequest(0).getFromFloor() > nowFloor) { // 上行接人
            Up();
        } else if (outRequest.getRequest(0).getFromFloor() < nowFloor) { // 下行接人
            Down();
        }
        Open();
        Close();
    }

    public void addRequest(PersonRequest request) {
        if (normalReset == 1) {
            normalRequestPool.addRequest(request);
        } else if (doubleReset == 1) {
            if (request.getFromFloor() >= 1 && request.getFromFloor() < newTransferFloor) {
                requestPoolA.addRequest(request);
            } else if (request.getFromFloor() > newTransferFloor && request.getFromFloor() <= 11) {
                requestPoolB.addRequest(request);
            } else if (request.getFromFloor() == newTransferFloor) {
                if (request.getToFloor() > newTransferFloor) {
                    requestPoolB.addRequest(request);
                } else {
                    requestPoolA.addRequest(request);
                }
            }
        } else {
            Print.printReceive(this, request);
            synchronized (outRequest) {
                outRequest.addRequest(request);
                outRequest.notifyAll();
            }
        }
    }

    public void setReset(String str, int capacity, int moveTime, int transferFloor) {
        if (str.equals("normalReset")) {
            normalReset = 1;
        } else if (str.equals("doubleReset")) {
            doubleReset = 1;
        }
        newCapacity = capacity;
        newMoveTime = moveTime;
        newTransferFloor = transferFloor;
        synchronized (outRequest) {
            outRequest.notifyAll();
        }
    }

    public void cleanIn() {
        if (inRequest.getSize() > 0) { // 清空电梯中的人
            Print.printOpen(this);
            openPoint = System.currentTimeMillis();
            while (inRequest.getSize() > 0) {
                PersonRequest request = inRequest.getRequest(0);
                Print.printOut(this, request);
                if (request.getToFloor() == nowFloor) {
                    Input.addCount();
                    synchronized (globalRequest) {
                        globalRequest.notifyAll();
                    }
                } else {
                    outRequest.addRequest(
                            new PersonRequest(nowFloor,
                                    request.getToFloor(), request.getPersonId()));
                }
                inRequest.removeRequest(request);
            }
            closePoint = System.currentTimeMillis();
            try {
                sleep(max(400 - (closePoint - openPoint), 0));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Print.printClose(this);
        }
    }

    public void cleanOut() {
        synchronized (globalRequest) {
            while (outRequest.getSize() > 0) {
                PersonRequest request = outRequest.getRequest(0);
                globalRequest.addRequest(request);
                outRequest.removeRequest(request);
            }
            globalRequest.notifyAll();
        }
    }

    public void resetBegin() {
        cleanIn();
        TimableOutput.println("RESET_BEGIN-" + id);
        cleanOut();
        try {
            sleep(max(1200, 0));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("RESET_END-" + id);
        if (normalReset == 1) {
            capacity = newCapacity;
            moveTime = newMoveTime;
            count = 0;
            normalReset = 0;
            while (normalRequestPool.getSize() > 0) {
                Print.printReceive(this, normalRequestPool.getRequest(0));
                outRequest.addRequest(normalRequestPool.getRequest(0));
                normalRequestPool.removeRequest(normalRequestPool.getRequest(0));
            }
        } else if (doubleReset == 1) {
            endSignal = 1; // 不允许再输入请求
            Elevator elevatorA = new Elevator(elevators, id, 'A', newMoveTime, newCapacity,
                    1, newTransferFloor, requestPoolA, globalRequest);
            Elevator elevatorB = new Elevator(elevators, id, 'B', newMoveTime, newCapacity,
                    1, newTransferFloor, requestPoolB, globalRequest);
            synchronized (elevators) {
                Operation.startDoubleElevators(this, elevatorA, elevatorB, elevators);
                elevators.notifyAll();
            }
            doubleReset = 0;
        }
        synchronized (outRequest) {
            outRequest.notifyAll();
        }
        synchronized (globalRequest) {
            globalRequest.notifyAll();
        }
        leavePoint = System.currentTimeMillis();
    }

    public int getNormalReset() {
        return normalReset;
    }

    public int getDoubleReset() {
        return doubleReset;
    }

    public int getReset() {
        if (normalReset == 1 || doubleReset == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getSpeed() {
        if (normalReset == 1 || doubleReset == 1) {
            return newMoveTime;
        } else {
            return moveTime;
        }
    }

    public Integer getCapacity() {
        if (normalReset == 1) {
            return newCapacity;
        } else {
            return capacity;
        }
    }

    public int getDirection() {
        return direction;
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public int getRequestSize() {
        return inRequest.getSize() + outRequest.getSize() +
                normalRequestPool.getSize() + requestPoolA.getSize() + requestPoolB.getSize();
    }

    public void changSignal() {
        endSignal = 1;
    }

    public RequestPool getOutRequest() {
        return outRequest;
    }

    public char getType() {
        return type;
    }

    public int getID() {
        return id;
    }

    public int getEnd() {
        return endSignal;
    }

    public int getLowFloor() {
        return lowFloor;
    }

    public int getHighFloor() {
        return highFloor;
    }

    public int getNewTransferFloor() {
        return newTransferFloor;
    }
}
