package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.app.horde.agent.*;

public class ResetTimer extends Behavior
    {
    private static final long serialVersionUID = 1L;
                
    public static String getType() { return sim.app.horde.agent.Agent.TYPE_PHYSICAL; }

    public boolean shouldAddDefaultExample() { return false; }

	public ResetTimer()
        {
        name = "ResetTimer"; 
        setKeyStroke('-');
        }

    public void start(Agent agent, Macro parent, Horde horde) 
        {
        parent.resetTimer(Macro.TIMER_BASIC);
        }
    }
