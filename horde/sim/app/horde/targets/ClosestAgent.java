package sim.app.horde.targets;

import sim.app.horde.*;
import sim.util.Bag;
import sim.util.Double2D;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

/**
 *
 * @author josephzelibor
 */
public class ClosestAgent extends Target
    {
    private static final long serialVersionUID = 1;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        SimHorde simhorde = (SimHorde) horde;
        SimAgent simagent = (SimAgent)agent;
        Double2D myLoc = simagent.getLocation();
        Bag agents = simhorde.agents.getAllObjects();
        //default
        SimAgent closest = (SimAgent) (agents.objs[0]);

        //need at least 2 agents, just return myself
        if(agents.numObjs < 2)
            {
            return simagent;
            }

        //more than 1 agent, don't check vs yourself
        if (closest == agent)
            {
            closest = (SimAgent) (agents.objs[1]);
            }

        double bestDistance = myLoc.distanceSq(closest.getTargetLocation(simagent, simhorde));

        //scan all agents
        for (int i = 0; i < agents.numObjs; i++)
            {
            SimAgent a = (SimAgent) (agents.objs[i]);

            //don't check vs yourself
            if (a == simagent)
                {
                continue;
                }

            double d = myLoc.distanceSq(a.getTargetLocation(simagent, simhorde));

            if (d < bestDistance)
                {
                closest = a;
                bestDistance = d;
                }

            }

        return closest;
        }

    public String toString()
        {
        return "Closest Agent";
        }
    }
