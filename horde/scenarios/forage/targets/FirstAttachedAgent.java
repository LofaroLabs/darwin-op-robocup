package sim.app.horde.scenarios.forage.targets;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.forage.agent.Forager;
import sim.app.horde.scenarios.forage.agent.Supervisor;
import sim.app.horde.targets.*; 

public class FirstAttachedAgent extends Target
    {
    private static final long serialVersionUID = 1L;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        Supervisor s = (Supervisor)agent; 
        return s.getAgentStatus(Forager.ATTACHED_STATUS); 
        }
        
    public String toString() { return "First Attached Agent"; }
    }
