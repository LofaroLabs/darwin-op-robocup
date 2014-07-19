package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class StepRight extends Behavior {
    private static final long serialVersionUID = 1L;

    public StepRight() { name = "Step-Right"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(HumanoidAgent.RIGHT_STEP_BUTTON); 
        }
    }
