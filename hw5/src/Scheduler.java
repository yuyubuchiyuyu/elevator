import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;
import java.util.ArrayList;

public class Scheduler extends Thread {
    private final ArrayList<Elevator> elevators;

    public Scheduler(ArrayList<Elevator> elevators) {
        this.elevators = elevators;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                for (int i = 0; i < 6; i++) {
                    elevators.get(i).changSignal();
                    synchronized (elevators.get(i).getOutRequest()) {
                        elevators.get(i).getOutRequest().notifyAll();
                    }
                }
                break;
            } else {
                Elevator elevator = elevators.get(request.getElevatorId() - 1);
                synchronized (elevator.getOutRequest()) {
                    elevator.addRequest(request);
                    elevator.getOutRequest().notifyAll();
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
