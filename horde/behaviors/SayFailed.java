package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.util.*;
import sim.app.horde.objects.*; 

/** A version of the Failed flag which does NOT go back to start.
	Thus it's just an ordinary behavior which happens to fire the FLAG_FAILED
	flag on the parent.  */
        
public class SayFailed extends Behavior
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public SayFailed() { name = "SayFailed"; }

    public void start(Agent agent, Macro parent, Horde horde) { parent.fireFlag(Macro.FLAG_FAILED, agent, parent, horde); }
    }