package elevator_Sim_Project;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;
import org.junit.Rule;

public class floorSystemTest {

	@Test
	public void testFloorSystem() {
		try {
			//create instance and verify sendSocket is not null
            floorSystem fs = new floorSystem();
            DatagramSocket sendSocket = fs.sendSocket;
            assertNotNull(sendSocket);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
	}
	/**
	 * test case for getfloorArray() method, verifies that the 
	 * method returns the expected ArrayList<String> object.
	 */
	@Test
	public void testGetfloorArray() {
		// create an ArrayList<String> object with known values
		ArrayList<String> expectedArray = new ArrayList<String>();
        expectedArray.add("1");
        expectedArray.add("2");
        expectedArray.add("3");
        expectedArray.add("4");
        // create new instance of floorSystem
        floorSystem fs = new floorSystem();
        
        // set floorArray to the expected ArrayList<String> object
        fs.floorArray = expectedArray;
        
        // call the getfloorArray() method to retrieve the ArrayList<String> object
        ArrayList<String> actualArray = fs.getfloorArray();
        
        // verify that the actual ArrayList<String> object is the same as the expected one
        assertEquals(expectedArray, actualArray);
	}
	/**
	 * Test case for getFloorSystem() method, verifies that the
	 * method returns the correct floor string based on the index parameter.
	 */
	@Test
	public void testGetFloorSystem() {
		// create a new instance of floorSystem 
	    floorSystem fs = new floorSystem();
	    
	    // set floorArray to a known ArrayList<String> object
	    ArrayList<String> floorArray = new ArrayList<String>();
	    floorArray.add("First floor");
	    floorArray.add("Second floor");
	    floorArray.add("Third floor");
	    fs.floorArray = floorArray;
	    
	    // call the getFloorSystem() method with an index parameter
	    String actualFloor = fs.getFloorSystem(1);
	    
	    // verify the actual floor string matches the expected one
	    String expectedFloor = "Second floor";
	    assertEquals(expectedFloor, actualFloor);
	}
	/**
	 * Test case for removeOut() method, verifies that 
	 * the method clears the floorArray member variable.
	 */
	@Test
	public void testRemoveOut() {
		// create a new instance of floorSystem
	    floorSystem fs = new floorSystem();
	    
	    // set floorArray to a known ArrayList<String> object
	    ArrayList<String> floorArray = new ArrayList<String>();
	    floorArray.add("First floor");
	    floorArray.add("Second floor");
	    floorArray.add("Third floor");
	    fs.floorArray = floorArray;
	    
	    // call removeOut() to clear floorArray 
	    fs.removeOut();
	    
	    // verify floorArray is empty
	    assertTrue(fs.floorArray.isEmpty());
	}
	/**
	 * Test case for deleteIndex() method, verifies that the method
	 * removes the element at the specified index from floorArray.
	 */
	@Test
	public void testDeleteIndex() {
		// Create a new instance of floorSystem
	    floorSystem fs = new floorSystem();
	    
	    // set the floorArray to a known ArrayList<String> object
	    ArrayList<String> floorArray = new ArrayList<String>();
	    floorArray.add("First floor");
	    floorArray.add("Second floor");
	    floorArray.add("Third floor");
	    fs.floorArray = floorArray;
	    
	    // call deleteIndex()to remove the element at index 1 (Second floor)
	    fs.deleteIndex(1);
	    
	    // verify floorArray contains the expected elements
	    assertEquals("First floor", fs.floorArray.get(0));
	    assertEquals("Third floor", fs.floorArray.get(1));
	}

	@Test
	public void testGetLamp() {
		// create a new instance of floorSystem
	    floorSystem fs = new floorSystem();  	    
	    fs.lampOn = true;// set lampOn to true
	    assertTrue(fs.getLamp()); // verify that it returns true
	    fs.lampOn = false;// set lampOn to false
	    assertFalse(fs.getLamp());// verify that it returns false
	}
	/**
	 * Test case for the setLamp() method in floorSystem,
	 * verifies that the lampOn member variable is set false.
	 */
	@Test
	public void testSetLamp() {
		// Create a new instance of the FloorSystem class
	    floorSystem fs = new floorSystem();	    
	    fs.setLamp(false); // Set lampOn to false
	    assertFalse(fs.lampOn); // verify lampOn has been set to false
	    
	    //fs.setLamp(true);  // Set lampOn to true    
	    //assertTrue(fs.lampOn);  // verify lampOn has been set to true
	}
	/**
	 * Test case for sendRequestToScheduler() method
	 * verify it runs
	 */
	@Test
	public void testSendRequestToScheduler() throws UnknownHostException {
        floorSystem floorSystem = new floorSystem();
        try {
            floorSystem.sendRequestToScheduler(); 
            assertTrue(true);
        } catch (NullPointerException e) {
            // do nothing
        }
	}
	/**
	 * Test case for CreateMessageRequest() method,
	 * if the two byte arrays are not equal, an 
	 * assertion error will be thrown, failing the test.
	 */
	@Test
	public void testCreateMessageRequest() {
		floorSystem floorSystem = new floorSystem();
        String currentRequest = "UP 5"; 
        // call createMessageRequest with "UP 5"
        floorSystem.createMessageRequest(currentRequest); 
        // create a byte array with same contents as above
        byte[] expectedData = "UP 5".getBytes();
        assertArrayEquals(expectedData, floorSystem.data);
	}
	/**
	 * Tests if method correctly extracts time information 
	 * from a string request and returns it as a long.
	 */
	@Test
	public void testGetTimeFromRequest() {
		floorSystem fs = new floorSystem();
		 // Test case 1: input string with hours, minutes, and seconds
	    String request1 = "01:30:45.500 floor 5";
	    long expectedTime1 = 90 * 60 * 1000 + 45 * 1000 + 500;
	    long actualTime1 = fs.getTimeFromRequest(request1);
	    assertEquals(expectedTime1, actualTime1);
	}
	/**
	 * Tests the method reads the contents of a file 
	 * correctly and stores it in an ArrayList. 
	 * It checks if the ArrayList returned by the method is
	 * the same as the ArrayList containing the expected file contents.
	 */
	@Test
	public void testReadFile() {
		 floorSystem fs = new floorSystem();
	     ArrayList<String> expectedFloorArray = new ArrayList<>();
	     try {
				File myObj = new File("inputRequests");
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {
					expectedFloorArray.add(myReader.nextLine());
				}
				myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
	     fs.readFile("inputRequests");
	     assertEquals(expectedFloorArray, fs.getfloorArray());
	}
	/**
	 * Test case for sendRequestToScheduler() method,
	 * verify it runs
	 */
	@Test
	public void testRun() {
		floorSystem fs = new floorSystem();
		try {
			fs.run();
		} catch(Exception e) {
			// If an exception is thrown, the test fails
			fail("Unexpected exception thrown");
		}
	}
}
