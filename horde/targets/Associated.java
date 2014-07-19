package sim.app.horde.targets;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

public class Associated extends Target
    {
    private static final long serialVersionUID = 1;
    
    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = parent.getAssociatedObject(Macro.ASSOCIATED_OBJECT_BASIC);
        if (obj == null)
            {
            // this might get annoying
            System.err.println("WARNING: Associated Target is null, using 'Me' instead.");
            return (SimAgent)agent;
            }
        else return obj;
        }

    public String toString() { return "Associated"; }
    }
