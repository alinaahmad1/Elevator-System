package elevator_Sim_Project;

import javax.swing.JFileChooser;

public class main {
	public static void main(String[] args) {				
		//Thread sendNewRequest = new Thread(new newRequest(), "Scheduler");
		Thread floor = new Thread(new floorSystem(), "Floor"); 
		Thread scheduler = new Thread(new scheduler(), "Elevator Subsystem");
		 
		floor.start();
		scheduler.start();
	}
}