package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class BackwardOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public BackwardOnce() { name = "Backward-Once"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_BACKWARD_BUTTON, 1);   // 1 is ignored anyway
        }
    }
