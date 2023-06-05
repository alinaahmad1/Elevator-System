package elevator_Sim_Project;

public class floorRequest {
	private long time;
	private int currentFloor;
	private int direction; // 1 = Up and 0= Down
	private int destinationFloor;
	private String error;
	private int errorFloorNum; //the floor which is experiencing error state
	private String inputRequest;
	
	public floorRequest(String requestedFloor) {
		addFloorRequest(requestedFloor);
		this.inputRequest = requestedFloor;
		
	}
	
	/*
	 * method used to look through the request from the provided input text file
	 */
	public void addFloorRequest(String requestedFloor) throws NumberFormatException, ArrayIndexOutOfBoundsException {
	    try {
	        // Split the request by spaces and store each piece of info into an array.
	        String[] l = requestedFloor.split(" ");
	        String requestTime = l[0];
	        currentFloor = Integer.parseInt(l[1]);
	        //determine the requested direction
	        if(l[2].equals("Up")) {
	            direction = 1;
	        } else {
	            direction = 0;
	        }
	        //check the requested destination floor at index 4
	        destinationFloor = Integer.parseInt(l[3]);

	     // Split the request time string into hours, minutes, seconds and store in an array.
			//String[] requestedTimeDetail = requestTime.split(":");
			
			// Convert the request time into milliseconds.
			if(l.length >= 5) {
				error = l[4];
				if(l[4].equals("Elevator Failure") || l[4].equals("Door Failure")) {
					errorFloorNum = Integer.parseInt(l[5]);
				}
			} else {
				error = "";
			}
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
	        e.printStackTrace();
	    }
	}

	/*
	 * get the error in a string format
	 */
	public String getError() {
		return error;
	}

	/*
	 * floor which has the error
	 */
	public int getFloorErrorNum() {
		return errorFloorNum;
	}
	
	/*
	* Getter method for floorNumber
	*/
	public int getFloorNumber() {
		return this.currentFloor;
	}
	
	/*
	 * Get the time the request is made
	 */
	public long getTime(){
    return this.time;
  }
	/*
	 * get direction of the request
	 * returns an int 1= Up and 0= Down
	 */
	public int getDirection() {
		return this.direction;
		
	}
	
	/*
	 * Get the string for direction 
	 */
	public String getRequestedDirection() {
		switch (direction) {
	    case 0:
	        return "Down";
	    default:
	        return "Up";
		}
	}
	
	/*
	 * returns the destination floor 
	 */
	public int getDestinationFloor(){
		return this.destinationFloor;
	}
	
  
	/*
     * @return String summary - the data of the request in a readable format.
     */
    public String toString() {
        return inputRequest;
    }
}
