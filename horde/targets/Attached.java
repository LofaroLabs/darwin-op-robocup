package sim.app.horde.targets;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

public class Attached extends Target
    {
    private static final long serialVersionUID = 1;
    
    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = ((SimAgent)agent).getManipulated();
        if (obj == null)
            {
            // this might get annoying
            System.err.println("WARNING: Attached Target is null, using 'Me' instead.");
            return (SimAgent)agent;
            }
        else return obj;
        }

    public String toString() { return "Attached"; }
    }
