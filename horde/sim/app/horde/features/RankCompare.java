package sim.app.horde.features;
import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;

public class RankCompare extends CategoricalFeature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public RankCompare()
        {
        super("RankCompare", new String[] {"higher","lower"}); 
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "Ag" };
        }

    public RankCompare(Target t)
        {
        this();
        targets[0] = t;
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        SimAgent simagent = (SimAgent) agent;
        return (targets[0].getRank(simagent, parent, horde) > simagent.getRank() ? 1 : 0);
        }
    }
