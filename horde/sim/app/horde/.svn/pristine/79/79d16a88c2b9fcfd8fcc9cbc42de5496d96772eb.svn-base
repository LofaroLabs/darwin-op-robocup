package sim.app.horde.scenarios.forage.features;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.forage.agent.Supervisor;
import sim.app.horde.targets.*; 

public class SomeoneDone extends Feature
    {
    private static final long serialVersionUID = 1L;
    
    public static final String getType() { return "supervisor"; }

    public SomeoneDone()
        {
        super("SomeoneDone"); 
        targets = new Target[0]; 
        targetNames = new String[0]; 
        }
        
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        Supervisor s = (Supervisor)agent; 
        if (s.doneAgent != null) 
        	{
        	return 1; 
        	}
        return 0; 
        }

    }
