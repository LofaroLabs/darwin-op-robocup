package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class TurnLeft extends Behavior {
    private static final long serialVersionUID = 1L;

    public TurnLeft() { name = "Turn-Left"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        System.err.println("Turning Left?");
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(HumanoidAgent.LEFT_TURN_BUTTON); 
        }
    }
