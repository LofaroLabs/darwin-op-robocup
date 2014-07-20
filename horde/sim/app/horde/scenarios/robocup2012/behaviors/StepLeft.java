package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class StepLeft extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public StepLeft() {
        name = "Step-Left";
        }

    public boolean getShouldAddDefaultExample() { return false; }

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);

        HumanoidAgent hAgent = (HumanoidAgent) agent;

        if (hAgent.isTraining())
            hAgent.humanoid.doMotion(HumanoidAgent.RCB4_MOT_LEFT_STEP_BUTTON);
        else
            hAgent.humanoid.pushButton(HumanoidAgent.LEFT_STEP_BUTTON);
        }
    }
