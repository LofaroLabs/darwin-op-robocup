package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class TurnRight extends Behavior {
    private static final long serialVersionUID = 1L;

    public TurnRight() { name = "Turn-Right"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        System.err.println("Turning Right?");
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(HumanoidAgent.RIGHT_TURN_BUTTON); 
        }
    }
