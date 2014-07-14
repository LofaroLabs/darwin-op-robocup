package sim.app.horde.scenarios.humanoid;

import edu.gmu.robocup.Motions;
import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class Stop extends Behavior {
    private static final long serialVersionUID = 1L;

    public Stop() { name = "Stop"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);  
        ((HumanoidAgent)agent).pushButton(Motions.STOP_BUTTON); 
        ((HumanoidAgent)agent).playMotion(Motions.RCB4_MOT_BOW); 
        }
        
        
    // don't add examples when transitioning TO me
    public boolean shouldAddExamples() { return false; }
    }
