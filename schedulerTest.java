package elevator_Sim_Project;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;
public class schedulerTest {

	@Test
	public void testIdleElevator() throws IOException {
		scheduler scheduler = new scheduler();

		//create a DatagramSocket to simulate elevator sending data
		DatagramSocket elevatorSocket = new DatagramSocket();
		InetAddress elevatorAddress = InetAddress.getLocalHost();

		//create data to be sent by elevator
		String testData = "1";
		byte[] sendData = testData.getBytes();

		//create a DatagramPacket to be sent by elevator
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, elevatorAddress, 5001);
		elevatorSocket.send(sendPacket);

		//call the method idleElevator() and verify expected response is returned
		String actualResponse = scheduler.idleElevator();
		assertTrue(actualResponse.equals("1") || actualResponse.equals("2") || actualResponse.equals("3"));
		//close the socket
		elevatorSocket.close();
	}

	@Test
	public void testFloorSystemRecieve() throws IOException {
		DatagramSocket receiveSocket = new DatagramSocket(1234);
		receiveSocket.close();
	    String expectedRequest = "1";
	    byte[] data = expectedRequest.getBytes();
	    DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 1234);
	    DatagramSocket socket = new DatagramSocket();
	    socket.send(packet);

	    String actualRequest;
		actualRequest = scheduler.floorSystemRecieve();
		assertEquals(expectedRequest, actualRequest); 
	}

	@Test
	public void testSendToElevator() throws SocketException, UnknownHostException, InterruptedException {
		// Call the sendToElevator method with some test data
        scheduler.reduced_data2 = new byte[]{0, 1, 2, 3, 4};
        scheduler.sendToElevator(1);
        Elevator elevator = new Elevator(0);
		elevator.getSchedulerRequest();
        // Wait for some time to allow the elevator to receive the data
        Thread.sleep(1000);
        
        // Verify that the elevator received the expected data
        byte[] receivedData = elevator.getSchedulerRequest().getBytes();
        assertArrayEquals(scheduler.reduced_data2, receivedData);
        
        // Clean up by closing the sockets
        scheduler.receiveSocket.close();
        scheduler.receiveElevatorSocket.close();
        scheduler.sendElevatorSocket.close();

	}
}
