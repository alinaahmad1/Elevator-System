package elevator_Sim_Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.net.*;

public class floorSystem implements Runnable{
	boolean lampOn = false; // checks if floor is ready to receive an elevator
	ArrayList<String> floorArray = new ArrayList<String>();
	private DatagramPacket sendPacket;//data which get sent to scheduler
	DatagramSocket sendSocket;//socket used to send packets
	byte data[];
	int sentRequests;

	public floorSystem() {
		try {
			sendSocket = new DatagramSocket();
		} catch(SocketException se){
			se.printStackTrace();
			System.exit(1);
		}
		sentRequests = 0;
	}

	/*
	 * 
	 */
	public ArrayList<String> getfloorArray(){
		return floorArray;
	}
	
	/*
	 * 
	 */
	public String getFloorSystem(int i) {
		return floorArray.get(i);
	}
		
	/*
	 * removes a floorRequest to the floorArray arraylist using clear method
	 */
	public void removeOut() {
		floorArray.clear();
	}
	
	/*
	 * 
	 */
	public void deleteIndex(int i) {
		floorArray.remove(i);
	}
	
	/*
	 * 
	 */
	public boolean getLamp(){
	    return lampOn;
	}
	  
	/*
	 * 
	 */
	public void setLamp(boolean lampOn){
	    this.lampOn = false;   
	}
	
	/*
	 * A method used to send a request to the SchedulerHelper and output the request
	 * #, along with the request that was sent.
	 * 
	 */
	public void sendRequestToScheduler() {
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5001);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			sentRequests += 1;
			int len = sendPacket.getLength();
			String sent = new String(data, 0, len);
			System.out.println();
			System.out.println("FLOORSYSTEM: (Request" + sentRequests + ") " + "Floor sent: " + sent);
			System.out.println();

			// The socket sends the packet to the Scheduler (Send)
			sendSocket.send(sendPacket);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/*
	 * A method used to create a message request
	 * 
	 * @param currentRequest The request that the
	 *                       Floor needs to send.
	 * 
	 */
	public void createMessageRequest(String currentRequest) {
		data = currentRequest.getBytes();
	}
	
	/*
	 * A method used to parse a request String and return the time as an integer.
	 * 
	 * @param floorRequest The request that the Floor
	 *                     must send.
	 * @return An integer representing the time value for the request.
	 * 
	 */
	public long getTimeFromRequest(String floorRequest) {
		String[] l = floorRequest.split(" ");
		String requestTime = l[0];
		String[] requestTimeInfo = requestTime.split(":");

		return (Integer.parseInt(requestTimeInfo[0]) * 1000 * 60 * 60)
				+ (Integer.parseInt(requestTimeInfo[1]) * 1000 * 60)
				+ (int) (Double.parseDouble(requestTimeInfo[2]) * 1000);

	}		
	/*
	 * A method to read data from the text file and store it in an ArrayList of
	 * Strings.
	 * 
	 * Code inspired from: https://www.w3schools.com/java/java_files_read.asp
	 * 
	 */
	public void readFile(String filename) {
		try {
			File myObj = new File(filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				floorArray.add(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	  
	public void run() {
		JFileChooser filechooser = new JFileChooser();
		int response = filechooser.showOpenDialog(null);
		if(response == JFileChooser.APPROVE_OPTION) {
			readFile("inputRequests");
		}
		for (int i = 0; i < floorArray.size(); i++) {
			String currentRequest = floorArray.get(i);
			createMessageRequest(currentRequest);
			floorRequest r = new floorRequest(currentRequest);

			// First request should be sent immediately, other requests should be sent at
			// the appropriate time.
			if (i == 0) {
				sendRequestToScheduler();

			} else {
				// Sleep for specified amount of time before sending request.
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				sendRequestToScheduler();
			}

			System.out.println("Floor " + r.getFloorNumber() + ": " + r.getRequestedDirection()
					+ " button pressed and lamp turned on.");
		}
	}
}