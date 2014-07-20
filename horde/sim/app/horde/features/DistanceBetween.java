package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;
import sim.util.*;

public class DistanceBetween extends Feature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public DistanceBetween()
        {
        super("DistanceBetween");
        targets = new Target[2];
        targets[0] = new Me();  //  default
        targets[1] = new Me();  //  default
        targetNames = new String[]{ "0", "1" };
        }
        
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;
        Double2D loc1 = targets[0].getLocation(agent, parent, horde);
        Double2D loc2 = targets[1].getLocation(agent, parent, horde);
        Double2D loc3 = simagent.getLocation();
        
        Double2D p = Utilities.closestPointOnLine(loc1.x, loc1.y, loc2.x, loc2.y, loc3.x, loc3.y);
        return loc3.distance(p);
        }
    }
