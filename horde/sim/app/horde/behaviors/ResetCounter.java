package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.*;

public class ResetCounter extends Behavior
    {
    private static final long serialVersionUID = 1L;
                
    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public boolean shouldAddDefaultExample() { return false; }

    public ResetCounter()
        {
        name = "ResetCounter"; 
        setKeyStroke('9');
        }

    public void start(Agent agent, Macro parent, Horde horde) 
        {
        parent.setCounter(Macro.COUNTER_BASIC, 0); 
        }
    }
