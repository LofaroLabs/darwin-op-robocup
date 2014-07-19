package sim.app.horde.features;
import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;

/*
  RANK: Every object has a unique rank.  You can get the rank of the nearest teammate etc.
  STATUS: Every object has a status, which can be set, incremented, decremented, or reset.
  ASSOCIATION: An FSA can associate with an object.
  GRABBING: An agent can grab an object.  
  FLAGGING: An agent can set a per-FSA flag.
  COUNTING: An agent can set, increment, decrement, or reset a counter.
*/


// Status really is categorical but because it has an infinite number of possible settings we make it continuous

public class Status extends Feature
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public Status()
        {
        super("Status");
        targets = new Target[1];
        targets[0] = new Me();  //  default
        targetNames = new String[]{ "X" };
        }

    public Status(Target t)
        {
        this();
        targets[0] = t;
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return targets[0].getStatus(agent, parent, horde); 
        }
    }
