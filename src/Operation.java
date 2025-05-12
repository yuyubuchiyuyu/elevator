import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.abs;

public class Operation {

    public static Integer judgeOpen(char type, int capacity, int direction,
                                    int transferFlag, int transferFloor, int nowFloor,
                                    RequestPool inRequest, RequestPool outRequest) {
        if (transferFlag == 1 && Objects.equals(nowFloor, transferFloor)) { // 是换乘电梯且到达换乘层
            if (type == 'A') {
                for (int i = 0; i < inRequest.getSize(); i++) {
                    if (inRequest.getRequest(i).getToFloor() >= transferFloor) { // 下区有人要到上区或换乘层
                        return 1;
                    }
                }
                for (int i = 0; i < outRequest.getSize(); i++) {
                    if (outRequest.getRequest(i).getFromFloor() == nowFloor
                            && outRequest.getRequest(i).getToFloor() < nowFloor) { // 换乘层有人要到下区
                        return 1;
                    }
                }
            } else if (type == 'B') {
                for (int i = 0; i < inRequest.getSize(); i++) {
                    if (inRequest.getRequest(i).getToFloor() <= transferFloor) { // 上区有人要到下区或换乘层
                        return 1;
                    }
                }
                for (int i = 0; i < outRequest.getSize(); i++) {
                    if (outRequest.getRequest(i).getFromFloor() == nowFloor
                            && outRequest.getRequest(i).getToFloor() > nowFloor) { // 换乘层有人要到上区
                        return 1;
                    }
                }
            }
        }

        for (int i = 0; i < inRequest.getSize(); i++) { // 电梯中有人出去，开
            PersonRequest request = inRequest.getRequest(i);
            if (request.getToFloor() == nowFloor) {
                return 1;
            }
        }

        for (int i = 0; i < outRequest.getSize(); i++) { // 电梯未满员，外有人进且同向，开
            PersonRequest request = outRequest.getRequest(i);
            int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
            if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                    && directionFlag == direction) {
                return 1;
            }
        }
        if (inRequest.getSize() == 0 && outRequest.getSize() > 0 &&
                outRequest.getRequest(0).getFromFloor() == nowFloor) { // 空电梯来接人
            return 1;
        }
        return 0;
    }

    public static RequestPool open_A(Elevator elevator, char type, int nowFloor, int capacity,
                                     int transferFlag, int transferFloor, RequestPool inRequest,
                                     RequestPool outRequest) {
        RequestPool requestTemp = new RequestPool();
        if (transferFlag == 1 && Objects.equals(nowFloor, transferFloor)
                && type == 'A') { // 是换乘电梯且到达换乘层
            for (int i = 0; i < inRequest.getSize(); i++) {
                PersonRequest request = inRequest.getRequest(i);
                if (request.getToFloor() > transferFloor) { // 下区有人要到上区
                    Print.printOut(elevator, request);
                    requestTemp.addRequest(new PersonRequest(
                            transferFloor, request.getToFloor(), request.getPersonId()));
                    inRequest.removeRequest(request);
                    i--;
                } else if (request.getToFloor() == transferFloor) { // 下区有人要到换乘层
                    Print.printOut(elevator, request);
                    Input.addCount();
                    inRequest.removeRequest(request);
                    i--;
                }
            }
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest request = outRequest.getRequest(i);
                if (request.getFromFloor() == nowFloor
                        && request.getToFloor() < nowFloor
                        && inRequest.getSize() < capacity) { // 换乘层有人要到下区
                    Print.printIn(elevator, request);
                    outRequest.removeRequest(request);
                    inRequest.addRequest(request);
                    i--;
                }
            }
        }
        return requestTemp;
    }

    public static RequestPool open_B(Elevator elevator, char type, int nowFloor, int capacity,
                                     int transferFlag, int transferFloor, RequestPool inRequest,
                                     RequestPool outRequest) {
        RequestPool requestTemp = new RequestPool();
        if (transferFlag == 1 && Objects.equals(nowFloor, transferFloor)
                && type == 'B') { // 是换乘电梯且到达换乘层
            for (int i = 0; i < inRequest.getSize(); i++) {
                PersonRequest request = inRequest.getRequest(i);
                if (request.getToFloor() < transferFloor) { // 上区有人要到下区
                    Print.printOut(elevator, request);
                    requestTemp.addRequest(new PersonRequest(
                            transferFloor, request.getToFloor(), request.getPersonId()));
                    inRequest.removeRequest(request);
                    i--;
                } else if (request.getToFloor() == transferFloor) { // 上区有人要到换乘层
                    Print.printOut(elevator, request);
                    Input.addCount();
                    inRequest.removeRequest(request);
                    i--;
                }
            }
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest request = outRequest.getRequest(i);
                if (request.getFromFloor() == nowFloor
                        && request.getToFloor() < nowFloor
                        && inRequest.getSize() < capacity) { // 换乘层有人要到上区
                    Print.printIn(elevator, request);
                    outRequest.removeRequest(request);
                    inRequest.addRequest(request);
                    i--;
                }
            }
        }
        return requestTemp;
    }

    public static void open_1(Elevator elevator,
                              int nowFloor, RequestPool inRequest) {
        // 电梯中有人出去
        for (int i = 0; i < inRequest.getSize(); i++) {
            PersonRequest request = inRequest.getRequest(i);
            if (request.getToFloor() == nowFloor) {
                Print.printOut(elevator, request);
                Input.addCount();
                inRequest.removeRequest(request);
                i--;
            }
        }
    }

    public static void open_2(Elevator elevator, int capacity, int direction, int nowFloor,
                              RequestPool inRequest, RequestPool outRequest) {
        // 电梯里未满员，电梯外有人进且同向 或者 电梯空，电梯外有人进且同向
        for (int i = 0; i < outRequest.getSize(); i++) {
            PersonRequest request = outRequest.getRequest(i);
            int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
            if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                    && directionFlag == direction) {
                Print.printIn(elevator, request);
                outRequest.removeRequest(request);
                inRequest.addRequest(request);
                i--;
            }
        }
    }

    public static void open_25(Elevator elevator, int nowFloor,
                               RequestPool inRequest, RequestPool outRequest) {
        if (inRequest.getSize() == 0 && outRequest.getSize() > 0 &&
                outRequest.getRequest(0).getFromFloor() == nowFloor) { // 空电梯来接人
            PersonRequest request = outRequest.getRequest(0);
            Print.printIn(elevator, request);
            outRequest.removeRequest(request);
            inRequest.addRequest(request);
        }
    }

    public static void open_3(Elevator elevator, int capacity, int direction, int nowFloor,
                              RequestPool inRequest, RequestPool outRequest) {
        // 电梯空，找同向的请求
        if (inRequest.getSize() == 0 && outRequest.getSize() != 0) {
            int flag = -1;
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest requestTemp = outRequest.getRequest(i);
                int directionFlag = Integer.compare(requestTemp.getFromFloor() - nowFloor, 0);
                if (directionFlag == direction &&
                        abs(requestTemp.getFromFloor() - nowFloor) > 0) {
                    flag = i;
                    break;
                }
            }
            if (flag == -1) {
                open_4(elevator, capacity,
                        direction, nowFloor, inRequest, outRequest);
            }
        }
    }

    public static void open_4(Elevator elevator, int capacity, int direction, int nowFloor,
                              RequestPool inRequest, RequestPool outRequest) {
        // 电梯空，找同层出发的反向请求
        int newDirection = direction;
        if (inRequest.getSize() == 0 && outRequest.getSize() != 0) {
            int flag = -1;
            int distance = 0;
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest requestTemp = outRequest.getRequest(i);
                if (requestTemp.getFromFloor() == nowFloor
                        && abs(requestTemp.getToFloor() - nowFloor) > distance) {
                    distance = abs(requestTemp.getToFloor() - nowFloor);
                    flag = i;
                }
            }
            if (flag != -1) {
                newDirection = -direction; // 确实有反向请求
                PersonRequest request = outRequest.getRequest(flag);
                Print.printIn(elevator, request);
                outRequest.removeRequest(request);
                inRequest.addRequest(request);
            }
        }
        if (inRequest.getSize() != 0) {
            // 电梯里有人且未满员，电梯外有人进且同向
            newDirection = Integer.compare(
                    inRequest.getRequest(0).getToFloor()
                            - nowFloor, 0);
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest request = outRequest.getRequest(i);
                int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
                if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                        && directionFlag == newDirection) {
                    Print.printIn(elevator, request);
                    outRequest.removeRequest(request);
                    inRequest.addRequest(request);
                    i--;
                }
            }
        }
    }

    public static void searchOutMain(int direction, int nowFloor, RequestPool outRequest) {
        int distance = 0;
        int flag = -1;
        for (int i = 0; i < outRequest.getSize(); i++) { // 优先接同向状态最远的客人
            PersonRequest requestTemp = outRequest.getRequest(i);
            int directionTemp = Integer.compare(
                    requestTemp.getFromFloor() - nowFloor, 0);
            if (directionTemp == direction &&
                    abs(requestTemp.getFromFloor() - nowFloor) >= distance) {
                distance = abs(requestTemp.getFromFloor() - nowFloor);
                flag = i;
            }
        }
        if (flag != -1) {
            outRequest.changeResult(flag);
            return;
        }
        for (int i = 0; i < outRequest.getSize(); i++) { // 接最远的客人
            PersonRequest requestTemp = outRequest.getRequest(i);
            if (abs(requestTemp.getFromFloor() - nowFloor) > distance) {
                distance = abs(requestTemp.getFromFloor() - nowFloor);
                flag = i;
            }
        }
        if (flag != -1) {
            outRequest.changeResult(flag);
        }
    }

    public static void startDoubleElevators(Elevator old, Elevator newA,
                                            Elevator newB, ArrayList<Elevator> elevators) {
        newA.setFriend(newB);
        newB.setFriend(newA);
        elevators.add(newA);
        elevators.add(newB);
        newA.start();
        newB.start();
        elevators.remove(old);
    }
}
