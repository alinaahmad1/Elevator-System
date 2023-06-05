package elevator_Sim_Project;

public enum elevator_motor {
	up
    {
        public String toString()
        {
            return "up";
        }
    },
    stop
    {
        public String toString()
        {
            return "stop";
        }
    },
    down
    {
        public String toString()
        {
            return "down";
        }
    };
}