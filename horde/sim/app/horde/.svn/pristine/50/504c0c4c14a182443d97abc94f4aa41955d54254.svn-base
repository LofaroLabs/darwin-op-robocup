package sim.app.horde.scenarios.pioneer.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.pioneer.PioneerAgent;
import sim.app.horde.behaviors.*;

public class LeftTurn extends Behavior 
    {
    private static final long serialVersionUID = 1L;

    public LeftTurn() 
        {
        name = "Turn Left"; 
        }
        
    public void go(Agent agent, Macro parent, Horde horde)
        { 
        super.go(agent, parent, horde); 
        PioneerAgent a = (PioneerAgent ) agent; 
        a.turn(PioneerAgent.LEFT); 
        }

    }
