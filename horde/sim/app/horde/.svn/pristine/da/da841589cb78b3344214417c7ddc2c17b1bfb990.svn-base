package sim.app.horde.targets;
import sim.app.horde.objects.*;
import sim.app.horde.*;
import sim.util.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

public class ClosestObstacle extends Target
    {
    private static final long serialVersionUID = 1;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        SimHorde simhorde = (SimHorde) horde;
        SimAgent simagent = (SimAgent)agent;
        Double2D loc = simagent.getLocation();
        Bag obstacles = simhorde.obstacles.getAllObjects();  // we'll just do a scan
        Body best = (Body) (obstacles.objs[0]);
        double bestDistance = loc.distanceSq(best.getTargetLocation(simagent, simhorde));
        for (int i = 0; i < obstacles.numObjs; i++)
            {
            Body o = (Body) (obstacles.objs[i]);
            double d = loc.distanceSq(o.getTargetLocation(simagent, simhorde));
            if (d < bestDistance)
                {
                best = o;
                bestDistance = d;
                }
            }
        return best;
        }
    public String toString() { return "Closest Obstacle"; }
    }
