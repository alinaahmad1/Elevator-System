package elevator_Sim_Project;
import java.net.*;
import java.io.*;

public class Elevator implements Runnable{
		// Sockets used to send and receive data from the Scheduler,
		// respectively,
		private DatagramSocket sendSocket, receiveSocket;
		// The packets used to send and receive data from the Scheduler,
		// respectively
		private DatagramPacket sendPacket;
		static DatagramPacket receivePacket;
		// The floor the Elevator is currently at.
		private int currentFloor;
		// Used to keep track of the Elevator's present state.
		ElevatorStates elevatorState;
		// Booleans used to keep track of errors and direct the program flow
		// in the required way.
		private boolean passengersPicked, hasElevatorFailed, doorsStuck;
		// Used to identify this elevator
		private int carID;
		// Used to hold the Elevator's state prior to a door fault, so that
		// the Elevator can be reverted to that state after handling the
		// door fault.
		private ElevatorStates previousState;
		// The time it takes for the doors to completely open/close. This cannot be a constant
		// because we have to modify it to simulate door faults.
		private int doorOpenCloseTime;
		// The floor number where the elevator fault will take place.
		private int faultFloorNumber;
		// Used to keep track of timings of different events.
		private long eventHandleStartTime, eventHandleEndTime, floorStartTime, floorEndTime;
		// Constants representing the time (in milliseconds) it will take
		// the Elevator to "do" certain actions - for the sake of simulation.
		// These timings are used with sleep() calls.
		private static final int DOOR_STAYS_OPEN_TIME = 6000;
		private static final int DOOR_FIXING_TIME = 1000;
		private static final int DOOR_OPEN_CLOSE_TIME = 2000;
		private static final int DOOR_OPEN_CLOSE_THRESHOLD = 2500;
		private static final int FLOOR_TO_FLOOR_TIME = 6790;
		private static final int FLOOR_TO_FLOOR_THRESHOLD = 7500;
	
	public Elevator(int elevatorNum) {
	
		try {
			receiveSocket = new DatagramSocket(5015 + elevatorNum);
			sendSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		this.carID = elevatorNum;
		elevatorState = ElevatorStates.Idle;
		currentFloor = 0;
		hasElevatorFailed = false;
		passengersPicked = false;
		doorsStuck = false;
		doorOpenCloseTime = DOOR_OPEN_CLOSE_TIME;
	}
	public enum ElevatorStates {
        Idle,
        Operate,
        elev_Up,
        elev_Down,
        Stop,
		door_Fail,
		elev_Fail,
		Illegal,
		arrived,
		arrive_destination
    }

	/**
	 * A method that returns the current state of the Elevator
	 * @return The current state of the Elevator
	 */
	public ElevatorStates getElevatorState() {
		return elevatorState;
	}
	
	/**
	 * A method that returns the current floor number of the Elevator
	 * @return The current floor number of the Elevator
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * A method that returns the Elevator number
	 * @return The Elevator number
	 */
	public int getElevatorNum() {
		return carID;
	}	
	/**
	 * Using the current elevatorState, the request currently being served, and some
	 * control booleans, this method is used to simulate the operations and states
	 * of the Elevator (moving up, moving down, opening doors, closing doors, door
	 * failure, elevator failure, etc.). The logic for modifying elevatorState for
	 * proper functionality and so that errors can also be implemented is in
	 * handleRequest().
	 * 
	 * @param currentRequest The request (line from floorRequests.txt) that the
	 *                       Elevator is currently servicing.
	 */
	public void stateMachine(String currentRequest) {
		long doorTimingStart;
		long doorTimingEnd;
		requestHandler(currentRequest);

		floorRequest r = new floorRequest(currentRequest);

		switch (elevatorState) {
		case elev_Up:
			System.out.println("ELEVATOR: " + Thread.currentThread().getName() + " moving up...");
			floorStartTime = System.nanoTime() / 1000000;
			try {
				Thread.sleep(FLOOR_TO_FLOOR_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			floorEndTime = System.nanoTime() / 1000000;

			if ((floorEndTime - floorStartTime) > FLOOR_TO_FLOOR_THRESHOLD) {
				elevatorState = ElevatorStates.elev_Fail;
				break;
			}

			currentFloor++;
			System.out.println("ELEVATOR: Current Floor of " + Thread.currentThread().getName() + ": " + currentFloor);
			break;
		case elev_Down:
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " moving down...");
			floorStartTime = System.nanoTime() / 1000000;
			try {
				Thread.sleep(FLOOR_TO_FLOOR_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			floorEndTime = System.nanoTime() / 1000000;

			if ((floorEndTime - floorStartTime) > FLOOR_TO_FLOOR_THRESHOLD) {
				elevatorState = ElevatorStates.elev_Fail;
				break;
			}

			currentFloor--;
			System.out.println("ELEVATOR: Current Floor of " + Thread.currentThread().getName() + ": " + currentFloor);
			break;
		case arrived:
			System.out.println("................." + Thread.currentThread().getName() + " arrived at source"
					+ ".................");

			System.out.println("ELEVATOR: Floor " + r.getFloorNumber() + ": " + "Button lamp turned off.");

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors opening...");
			doorTimingStart = System.nanoTime() / 1000000;
			try {
				Thread.sleep(doorOpenCloseTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doorTimingEnd = System.nanoTime() / 1000000;

			if ((doorTimingEnd - doorTimingStart) > DOOR_OPEN_CLOSE_THRESHOLD) {
				elevatorState = ElevatorStates.door_Fail;
				System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s doors are stuck.");
				break;
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors opened...");
			try {
				Thread.sleep(DOOR_STAYS_OPEN_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors closing...");
			doorTimingStart = System.nanoTime() / 1000000;
			try {
				Thread.sleep(doorOpenCloseTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doorTimingEnd = System.nanoTime() / 1000000;

			if ((doorTimingEnd - doorTimingStart) > DOOR_OPEN_CLOSE_THRESHOLD) {
				elevatorState = ElevatorStates.door_Fail;
				System.out.println(Thread.currentThread().getName() + "'s doors are stuck.");
				break;
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors closed...");
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s" + " floor " + r.getDestinationFloor()
					+ " button lamp turned on.");
			passengersPicked = true;
			break;
		case arrive_destination:
			System.out.println("................." + Thread.currentThread().getName() + " arrived at destination"
					+ ".................");
			
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s" + " floor " + r.getDestinationFloor()
					+ " button lamp turned off.");
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors opening...");
			doorTimingStart = System.nanoTime() / 1000000;
			try {
				Thread.sleep(doorOpenCloseTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doorTimingEnd = System.nanoTime() / 1000000;

			if ((doorTimingEnd - doorTimingStart) > DOOR_OPEN_CLOSE_THRESHOLD) {
				elevatorState = ElevatorStates.door_Fail;
				System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s doors are stuck.");
				break;
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors opened...");
			try {
				Thread.sleep(DOOR_STAYS_OPEN_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors closing...");
			doorTimingStart = System.nanoTime() / 1000000;
			try {
				Thread.sleep(doorOpenCloseTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doorTimingEnd = System.nanoTime() / 1000000;

			if ((doorTimingEnd - doorTimingStart) > DOOR_OPEN_CLOSE_THRESHOLD) {
				elevatorState = ElevatorStates.door_Fail;
				System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s doors are stuck.");
				break;
			}

			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " doors closed...");

			passengersPicked = false;
			elevatorState = ElevatorStates.Idle;

			eventHandleEndTime = System.nanoTime(); // End event time

			// The time it takes an Elevator to complete an event.
			System.out.println("*** " + Thread.currentThread().getName() + " took "
					+ ((eventHandleEndTime - eventHandleStartTime) / 1000000000)
					+ " seconds to complete the following request: " + r.toString() + " ***"); 
			break;
		case door_Fail:
			try {
				Thread.sleep(DOOR_FIXING_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + "'s doors are now fixed.");
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " resumed back to service.");
			elevatorState = previousState;

			doorOpenCloseTime = DOOR_OPEN_CLOSE_TIME;
			doorsStuck = true;
			break;
		case elev_Fail:
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " is currently broken.");
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " shutting down...");
			System.out.println("ELEVATOR: " +Thread.currentThread().getName() + " has been shut down.");
			hasElevatorFailed = true;
			break;
		case Idle:
			break;
		default:
			elevatorState = ElevatorStates.Illegal;
			System.out.println("Warning: Elevator in ILLEGAL STATE");
		}
	}

	/**
	 * This method essentially does the pre-processing of the Elevator before it goes through its state machine. As the
	 * Elevator moves from floor-to-floor, this method makes the necessary changes to its state. This is also where we 
	 * look for injection of errors and modify the necessary fields and get the necessary information to simulate and 
	 * handle those errors. 
	 * 
	 * @param request The request (line from floorRequests.txt) that the Elevator is currently servicing.
	 */
	public void requestHandler(String request) {

		if (elevatorState == ElevatorStates.elev_Fail) {
			return;
		} else if (elevatorState == ElevatorStates.door_Fail) {
			return;
		}

		floorRequest r = new floorRequest(request);

		if (r.getError().equals("ElevatorFailure")) {
			faultFloorNumber = r.getFloorErrorNum();

			if (currentFloor == faultFloorNumber) {
				elevatorState = ElevatorStates.elev_Fail;
				return;
			}
		}

		if (!passengersPicked) {
			if (currentFloor < r.getFloorNumber())
				elevatorState = ElevatorStates.elev_Up;
			else if (currentFloor > r.getFloorNumber())
				elevatorState = ElevatorStates.elev_Down;
			else {
				elevatorState = ElevatorStates.arrived;
				if (!doorsStuck) {
					if (r.getError().equals("SourceDoorError")) {
						doorOpenCloseTime = DOOR_OPEN_CLOSE_THRESHOLD + 1;
					}
				}
			}
		} else {
			if (currentFloor < r.getDestinationFloor())
				elevatorState = ElevatorStates.elev_Up;
			else if (currentFloor > r.getDestinationFloor())
				elevatorState = ElevatorStates.elev_Down;
			else {
				elevatorState = ElevatorStates.arrive_destination;
				if (!doorsStuck) {
					if (r.getError().equals("DestinationDoorFailure")) {
						doorOpenCloseTime = DOOR_OPEN_CLOSE_THRESHOLD + 1;
					}
				}
			}
		}
		previousState = elevatorState;
	}

	/**
	 * Used to send a message to the Scheduler containing the Elevator's number. This serves
	 * as an indication to the Scheduler that this Elevator is idle and ready to receive a request.
	 * The Elevator's number is sent so that the Scheduler can use it to send a request to the correct
	 * port number.
	 */
	public void askSchedulerForRequest() {
		String toScheduler = String.valueOf(carID);

		byte tempData[] = toScheduler.getBytes();

		try {
			sendPacket = new DatagramPacket(tempData, tempData.length, InetAddress.getLocalHost(), 5002);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Receive a request from the Scheduler that
	 * needs to be serviced by this Elevator.
	 * 
	 * @return A string containing the request the Elevator has to service.
	 * 
	 */
	public String getSchedulerRequest() {

		byte data[] = new byte[1000];
		receivePacket = new DatagramPacket(data, data.length);
		String received = null;

		try {
			// The socket received data from the Scheduler (Send)
			receiveSocket.receive(receivePacket);

			byte reduced_data[] = new byte[receivePacket.getLength()];
			System.arraycopy(data, 0, reduced_data, 0, reduced_data.length);

			int len = receivePacket.getLength();
			received = new String(reduced_data, 0, len);
		} catch (IOException e) {
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		return received;
	}
	/**
	 * 
	 * This run() method makes the Elevator threads ask the Scheduler for a request and
	 * wait until a request is received, if they are idle. They then continuously
	 * loop and perform the necessary operations to service a request (and simulate
	 * errors) before becoming Idle again. Failed elevators cannot be brought back.
	 * 
	 */
	public void run() {
		String currentRequest = "";
		while (true) {
			if (elevatorState == ElevatorStates.Idle) {
				askSchedulerForRequest();
				doorsStuck = false;
				currentRequest = getSchedulerRequest();

				eventHandleStartTime = System.nanoTime(); // Start the event time
			}
			if (hasElevatorFailed) {
				return;
			}
			stateMachine(currentRequest);
		}
	}
}


  