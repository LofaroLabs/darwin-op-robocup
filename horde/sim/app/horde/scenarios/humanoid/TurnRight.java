package sim.app.horde.scenarios.humanoid;

import edu.gmu.robocup.Motions;
import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class TurnRight extends Behavior {
    private static final long serialVersionUID = 1L;

    public TurnRight() { name = "Turn Right"; } 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde); 
        ((HumanoidAgent)agent).pushButton(Motions.RIGHT_TURN_BUTTON); 
        }       
    }