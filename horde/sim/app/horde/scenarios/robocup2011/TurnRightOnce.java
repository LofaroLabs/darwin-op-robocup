package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class TurnRightOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public TurnRightOnce() { name = "Turn-Right-Once"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_RIGHT_TURN_BUTTON, 1);  // 1 is ignored anyway
        }
    }
