package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class StepRight extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public StepRight()
        {
        name = "Step-Right";
        }

    public boolean getShouldAddDefaultExample() { return false; }

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
        HumanoidAgent hAgent = (HumanoidAgent) agent;

        if (hAgent.isTraining())
            hAgent.humanoid.doMotion(HumanoidAgent.RCB4_MOT_RIGHT_STEP_BUTTON);
        else
            hAgent.humanoid.pushButton(HumanoidAgent.RIGHT_STEP_BUTTON);
        }
    }
