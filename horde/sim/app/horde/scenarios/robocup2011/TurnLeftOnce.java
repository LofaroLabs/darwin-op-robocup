package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class TurnLeftOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public TurnLeftOnce() { name = "Turn-Left-Once"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_LEFT_TURN_BUTTON, 1);   // 1 is ignored anyway
        }
    }
