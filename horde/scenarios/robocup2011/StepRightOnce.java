package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class StepRightOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public StepRightOnce() { name = "Step-Right-Once"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_RIGHT_STEP_BUTTON, 1);   // 1 is ignored anyway
        }
    }
