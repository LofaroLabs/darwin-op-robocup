package sim.app.horde.features;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;

/** A simple binary categorical feature which returns 1 or 0 based on whether the
    macro has "Failed". **/
        
public class Failed extends CategoricalFeature implements NonDefaultFeature
    {   
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public Failed() { super("Failed", new String[] {"NotFailed", "Failed"}); }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return (parent.getFlag(Macro.FLAG_FAILED) ? 1 : 0); 
        }
    }