package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class BigPivot extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public BigPivot() {
        name = "BigPivot";
        }

    public boolean getShouldAddDefaultExample()
        {
        return false;
        }

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);

        System.out.println("Big Pivot ");

        for (int i = 0; i < 2; i++)
            ((HumanoidAgent) agent).humanoid.doMotion(HumanoidAgent.RCB4_MOT_PIVOT_LEFT);
        }
    }
