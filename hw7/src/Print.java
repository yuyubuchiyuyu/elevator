import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.TimableOutput;

public class Print {
    public static void printIn(Elevator elevator, PersonRequest request) {
        char type = elevator.getType();
        int nowFloor = elevator.getNowFloor();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("IN-"
                    + request.getPersonId() + '-' + nowFloor + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("IN-"
                    + request.getPersonId() + '-' + nowFloor + "-" + id + "-B");
        } else {
            TimableOutput.println("IN-"
                    + request.getPersonId() + '-' + nowFloor + "-" + id);
        }
    }

    public static void printOut(Elevator elevator, PersonRequest request) {
        char type = elevator.getType();
        int nowFloor = elevator.getNowFloor();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("OUT-" +
                    request.getPersonId() + '-' + nowFloor + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("OUT-" +
                    request.getPersonId() + '-' + nowFloor + "-" + id + "-B");
        } else {
            TimableOutput.println("OUT-" +
                    request.getPersonId() + '-' + nowFloor + "-" + id);
        }
    }

    public static void printOpen(Elevator elevator) {
        char type = elevator.getType();
        int nowFloor = elevator.getNowFloor();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("OPEN-" + nowFloor + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("OPEN-" + nowFloor + "-" + id + "-B");
        } else {
            TimableOutput.println("OPEN-" + nowFloor + "-" + id);
        }
    }

    public static void printClose(Elevator elevator) {
        char type = elevator.getType();
        int nowFloor = elevator.getNowFloor();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("CLOSE-" + nowFloor + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("CLOSE-" + nowFloor + "-" + id + "-B");
        } else {
            TimableOutput.println("CLOSE-" + nowFloor + "-" + id);
        }
    }

    public static void printReceive(Elevator elevator, PersonRequest request) {
        char type = elevator.getType();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("RECEIVE-" + request.getPersonId() + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("RECEIVE-" + request.getPersonId() + "-" + id + "-B");
        } else {
            TimableOutput.println("RECEIVE-" + request.getPersonId() + "-" + id);
        }
    }

    public static void printArrive(Elevator elevator) {
        char type = elevator.getType();
        int nowFloor = elevator.getNowFloor();
        int id = elevator.getID();
        if (type == 'A') {
            TimableOutput.println("ARRIVE-" + nowFloor + "-" + id + "-A");
        } else if (type == 'B') {
            TimableOutput.println("ARRIVE-" + nowFloor + "-" + id + "-B");
        } else {
            TimableOutput.println("ARRIVE-" + nowFloor + "-" + id);
        }
    }
}
