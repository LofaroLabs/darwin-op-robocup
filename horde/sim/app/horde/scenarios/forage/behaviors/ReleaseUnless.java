package sim.app.horde.scenarios.forage.behaviors;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Macro;

public class ReleaseUnless extends Release
    {
    private static final long serialVersionUID = 1L;

    public ReleaseUnless() { name = "Release Unless"; }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        SimAgent a = (SimAgent) agent; 
        //if (a.hasBiggestBox()) return; // i have the biggest box, so don't release it 
        super.go(agent, parent, horde); 
        }
    }
