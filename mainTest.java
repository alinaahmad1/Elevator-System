package elevator_Sim_Project;

import static org.junit.Assert.*;

import org.junit.Test;

public class mainTest { 
//verify floor and scheduler threads have started and are alive

	@Test
    public void testMain() {
        Thread floor = new Thread(new floorSystem(), "Floor");
        Thread scheduler = new Thread(new scheduler(), "Elevator Subsystem");

        floor.start();
        scheduler.start();

        assertTrue(floor.isAlive());
        assertTrue(scheduler.isAlive());
    }
}
