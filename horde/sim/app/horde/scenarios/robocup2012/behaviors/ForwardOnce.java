package sim.app.horde.scenarios.robocup2012.behaviors;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.robocup2012.HumanoidAgent;

public class ForwardOnce extends Behavior {
    private static final long serialVersionUID = 1L;

    public ForwardOnce() { name = "Forward-Once"; } 
    
    public boolean getShouldAddDefaultExample() { return false; }    

    
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).humanoid.doMotion(HumanoidAgent.RCB4_MOT_FORWARD_BUTTON);  
        }
    }
