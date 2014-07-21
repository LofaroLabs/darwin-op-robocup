package sim.app.horde.scenarios.boxpushing.behaviors.pioneer; 

import sim.app.horde.*;
import sim.app.horde.agent.*; 
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.boxpushing.*;

public class Forward extends Behavior
    {
    private static final long serialVersionUID = 1L;

    public Forward() { name = "PioneerForward"; } 
                
    public void go(Agent agent, Macro parent, Horde horde)
        { 
        super.go(agent, parent, horde); 
        
        PioneerAgent pa = (PioneerAgent) agent; 
        
        byte speed = (byte) Pioneer.MOVE_SPEED;
		if (pa.isTraining())
			speed = (byte) Pioneer.TRAINING_MOVE_SPEED;

        
        pa.bot.move(Pioneer.FORWARD, speed); 
        }       
    }
