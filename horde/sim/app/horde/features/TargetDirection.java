package sim.app.horde.features;

import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

/**
 *
 * @author vittorio
 */
public class TargetDirection extends ToroidalFeature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public TargetDirection(Target t)
        {
        this();
        targets[0] = t;
        }

    public TargetDirection()
        {
        super("DirectionTo");
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };
        }
                
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;

        if (agent == horde.getTrainingAgent())
            targets[0].getTargetable(agent, parent, horde);
        
        double a =  Utilities.relativeAngle(simagent.getLocation(),
            simagent.orientation2D(), targets[0].getLocation(simagent, parent, horde));

        return a;         
        }
    }
