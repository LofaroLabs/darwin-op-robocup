package sim.app.horde.scenarios.pioneer.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;

public class StopDone extends sim.app.horde.scenarios.pioneer.behaviors.Stop
    {
    private static final long serialVersionUID = 1L;

    public StopDone()
        {
        name = "Stop Done"; 
        }
        
    public void go(Agent agent, Macro parent, Horde horde)
        { 
        super.go(agent, parent, horde);
        parent.fireDone(agent, parent.getParent(), horde); 
        }
        
    }
