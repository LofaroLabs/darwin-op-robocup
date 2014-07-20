package sim.app.horde.scenarios.forage.targets;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.targets.*;
import sim.app.horde.scenarios.forage.*;
import sim.app.horde.scenarios.forage.agent.Forager;
import sim.util.*;

public class ClosestAttachedAgent extends Target
    {
    private static final long serialVersionUID = 1L;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        Forager f = (Forager) agent;
        ForageHorde fHorde = (ForageHorde) horde;

        Double2D myLoc = f.getLocation();
        double bestDistance = Double.MAX_VALUE;
        Bag agents = fHorde.agents.allObjects;
        Targetable closest = null;

        for (int i = 0; i < agents.size(); i++)
            {
            if (agents.objs[i] instanceof Forager) { 
                Forager forage = (Forager) agents.objs[i];
                if (forage == agent || forage.getRank() != f.getRank()) continue;
                if (forage.manipulated != null)
                    {
                    double d = myLoc.distanceSq(forage.getTargetLocation(f, fHorde));
                    if (d < bestDistance)
                        {
                        closest = forage;
                        bestDistance = d;
                        }
                    }
                }

            }
        return closest;
        }

    public String toString()
        {
        return "Closest Attached Agent";
        }

    }
