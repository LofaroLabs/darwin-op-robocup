package sim.app.horde.scenarios.robocup2011;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class GetUpBack extends Behavior {
    private static final long serialVersionUID = 1L;

    public GetUpBack() { name = "Get-Up-Back"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).doMotion(HumanoidAgent.RCB4_MOT_GETUPBACK, 1); 
        }
    }
