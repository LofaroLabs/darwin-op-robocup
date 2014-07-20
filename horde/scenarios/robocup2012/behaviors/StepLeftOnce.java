package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class StepLeftOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public StepLeftOnce() { name = "Step-Left-Once"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).humanoid.doMotion(HumanoidAgent.RCB4_MOT_LEFT_STEP_BUTTON);   
        }
    }
