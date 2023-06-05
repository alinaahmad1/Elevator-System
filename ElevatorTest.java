package elevator_Sim_Project;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import elevator_Sim_Project.Elevator.ElevatorStates;

public class ElevatorTest  {
	
	@Test
	public void testGetElevatorState() {
		// Arrange
        Elevator elevator = new Elevator(1);
        ElevatorStates expectedState = ElevatorStates.Idle;    
        // Act
        ElevatorStates currentState = elevator.getElevatorState();     
        // Assert
        assertEquals(expectedState, currentState);
	}

	@Test
	public void testGetCurrentFloor() {
		Elevator elevator = new Elevator(2); // create an instance of Elevator
		int currentFloor = elevator.getCurrentFloor(); // call getCurrentFloor() method
		assertEquals(0, currentFloor); // assert that the current floor is 1
	}

	@Test
	public void testGetElevatorNum() {
		Elevator elevator = new Elevator(4); // create an elevator with ID 4
        int expected = 4;
        int actual = elevator.getElevatorNum();
        assertEquals(expected, actual);
	}

	@Test
	public void testRequestHandler() {
		// Set up initial state
	    Elevator elevator = new Elevator(5);  
	    // Test for source floor request
	    String sourceFloorRequest = "14:05:15.0 1 Up 4";
	    elevator.requestHandler(sourceFloorRequest);
	    assertEquals(ElevatorStates.elev_Up, elevator.getElevatorState()); 
	}

	@Test
	public void testAskSchedulerForRequest() {
		Elevator elevator = new Elevator(3);
        try {
            DatagramSocket receiveSocket = new DatagramSocket(5002);

            // Start a new thread to receive the packet sent by the elevator
            Thread receiveThread = new Thread(() -> {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    receiveSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Verify that the received data is correct
                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                assertEquals("3", receivedData);
            });

            receiveThread.start();

            // Wait for the thread to start listening on the socket
            Thread.sleep(100);

            // Send the request from the elevator
            elevator.askSchedulerForRequest();

            // Wait for the receive thread to finish
            receiveThread.join();

            // Clean up
            receiveSocket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
	}
	
	
	@Test
	public void testGetSchedulerRequest() throws IOException, InterruptedException {
		
	}

	
}
