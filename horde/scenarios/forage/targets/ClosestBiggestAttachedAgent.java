package sim.app.horde.scenarios.forage.targets;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.forage.agent.*;
import sim.app.horde.targets.Target;

public class ClosestBiggestAttachedAgent extends Target
    {
    private static final long serialVersionUID = 1L;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        Forager biggest = null;
        
        if (agent.getGroup().getController() != null)
        	biggest = ((Supervisor)agent.getGroup().getController()).attachedAgent();
        else
        biggest = ((Supervisor)agent).attachedToBiggestBox();
        
        return biggest;
        }
        
    public String toString()
        {
        return "Biggest Attached Agent in Swarm";
        }
    }
