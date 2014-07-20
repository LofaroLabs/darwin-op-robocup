package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class Forward extends Behavior {
    private static final long serialVersionUID = 1L;

    public Forward() { name = "Forward"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        System.err.println("Going Forward?");
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(HumanoidAgent.FORWARD_BUTTON); 
        }
    }
