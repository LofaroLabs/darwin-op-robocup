package sim.app.horde.scenarios.forage.behaviors;

import sim.app.horde.*; 
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;

public class StopDone extends Stop
    {

    private static final long serialVersionUID = 1L;
    
    public static final String getType() { return "forager"; }

    public StopDone() { name = "Stop Done"; }
        
    public void go(Agent agent, Macro parent, Horde horde) 
        {
        super.go(agent, parent, horde); 
        parent.fireFlag(Macro.FLAG_DONE, agent, parent, horde);
        }
        
    }
