package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.targets.*;

public class SetCounter extends Behavior 
    {
    private static final long serialVersionUID = 1L;

    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public boolean shouldAddDefaultExample() { return false; }
        
    public SetCounter()
        {
        name = "SetCounter";
        setKeyStroke('2');
        }
        
    public void start(Agent agent, Macro parent, Horde horde) 
        {
        parent.setCounter(Macro.COUNTER_BASIC, 1); 
        }
    }
