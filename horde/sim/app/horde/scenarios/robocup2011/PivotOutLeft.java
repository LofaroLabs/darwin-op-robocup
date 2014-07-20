package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class PivotOutLeft extends Behavior {
    private static final long serialVersionUID = 1L;

    public PivotOutLeft() { name = "Pivot-Out-Left"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_PIVOT_OUT_LEFT, 1); 
        }
    }
