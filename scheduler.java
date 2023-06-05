package elevator_Sim_Project;


import java.io.IOException;
import java.net.*;

/**
 * All request made by the Floors or Elevator are managed by the Scheduler
 * Communicates with Floors and Elevators and transfers data in between them
 */
public class scheduler implements Runnable {
	// Sockets used to receive data from the SchedulerHelper and send/receive data
	// from the Elevators.
	static DatagramSocket receiveSocket;
	static DatagramSocket receiveElevatorSocket;
	static DatagramSocket sendElevatorSocket; //changed to static
	// Packets used to receive data from the SchedulerHelper and send/receive data
	// from the Elevators.
	private static DatagramPacket recieveFloorSystemPacket; //changed to static
	private DatagramPacket receiveElevatorPacket;
	static DatagramPacket sendElevatorPacket;
	// Used to resize the buffers to the correct size (packet size)
	private byte reduced_data[];
	static byte reduced_data2[];
	// A constant representing the # of Elevators to be used in the system.
	private final int numberOfElevators = 3;

	/**
	 * A constructor for the Scheduler class.
	 * 
	 */
	public scheduler() {
		try {
			receiveSocket = new DatagramSocket(5001);
			receiveElevatorSocket = new DatagramSocket(5002);
			sendElevatorSocket = new DatagramSocket();

			for (int i = 1; i <= numberOfElevators; i++) {
				Thread elevator = new Thread(new Elevator(i), "Elevator" + i);
				elevator.start();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Used to receive notification from an Elevator that it is idle and ready to
	 * service a request.
	 * 
	 * @return The Elevator number of the Elevator that is idle and notifying the
	 *         Scheduler as a String.
	 * 
	 */
	public String idleElevator() {
		byte data[] = new byte[100];
		receiveElevatorPacket = new DatagramPacket(data, data.length);

		String received = null;

		try {
			receiveElevatorSocket.receive(receiveElevatorPacket); // The socket receives the packet from the Elevator
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		int len = receiveElevatorPacket.getLength();
		reduced_data = new byte[len];
		System.arraycopy(data, 0, reduced_data, 0, len);

		received = new String(reduced_data, 0, len);
		statemachine(SchedulerStates.NoRequests);

		return received;
	}

	/**
	 * Used to receive a request from SchedulerHelper that needs to be scheduled.
	 * 
	 * @return The request (line from floorRequests.txt) that the Scheduler needs to
	 *         schedule.
	 * 
	 */
	public static String floorSystemRecieve() { //changed this to static

		byte data2[] = new byte[1000];
		recieveFloorSystemPacket = new DatagramPacket(data2, data2.length); //changed this to static

		try { //changed receiveSocket to static
			receiveSocket.receive(recieveFloorSystemPacket); // The socket receives the packet from the Floor
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		reduced_data2 = new byte[recieveFloorSystemPacket.getLength()]; //changed this to static
		System.arraycopy(data2, 0, reduced_data2, 0, recieveFloorSystemPacket.getLength());
		int len = recieveFloorSystemPacket.getLength();
		statemachine(SchedulerStates.ReceivedReq);
		return new String(reduced_data2, 0, len);
	}

	/**
	 * Sending the request to the Elevator specified by elevatorNum.
	 * 
	 * @param elevatorNum The elevator # of the Elevator that is idle and will be
	 *                    sent the request.
	 * 
	 */
	public static void sendToElevator(int elevatorNum) { //changed to static

		try {
			sendElevatorPacket = new DatagramPacket(reduced_data2, reduced_data2.length, InetAddress.getLocalHost(), 5015 + elevatorNum);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			sendElevatorSocket.send(sendElevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * {@inheritDoc} This run() method ensures the Scheduler receives a request from
	 * SchedulerHelper and then schedules that request to the first Elevator that
	 * tells it that it is idle. This continues for all requests in
	 * floorRequests.txt.
	 * 
	 */
	public void run() {
		while (true) {
			String elevNumToSendRequest = "";

			floorSystemRecieve();
			elevNumToSendRequest = idleElevator();
			sendToElevator(Integer.parseInt(elevNumToSendRequest));
		}
	}

	public enum SchedulerStates {
    	NoRequests,
    	ReceivedReq,
    }
	public static void statemachine(SchedulerStates state) { //changed this to static
		switch(state) {
			case NoRequests:
				System.out.println("SCHEDULER: No Requests\n");
				break;
			case ReceivedReq:
	            System.out.println("SCHEDULER: Contains Requests\n");
	            break;
		}
	}
}