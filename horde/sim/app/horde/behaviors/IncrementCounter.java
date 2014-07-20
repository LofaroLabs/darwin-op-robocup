package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.app.horde.targets.*;

public class IncrementCounter extends Behavior
    {
    private static final long serialVersionUID = 1L;
        
    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public boolean shouldAddDefaultExample() { return false; }
        
    public IncrementCounter()
        {
        name = "IncrementCounter";
        setKeyStroke('3');
        }
        
    public void start(Agent agent, Macro parent, Horde horde) 
        {
        parent.incrementCounter(Macro.COUNTER_BASIC, 1); 
        }
    }
