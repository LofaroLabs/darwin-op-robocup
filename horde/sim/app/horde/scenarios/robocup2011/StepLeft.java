package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class StepLeft extends Behavior {
    private static final long serialVersionUID = 1L;

    public StepLeft() { name = "Step-Left"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(HumanoidAgent.LEFT_STEP_BUTTON); 
        }
    }
