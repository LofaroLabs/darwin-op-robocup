package sim.app.horde.behaviors;

/** A simple behavior which does nothing.  However Macros detect transition
    to this behavior, which causes them to signal their "done" flag. */
        
public class Failed extends Flag
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public Failed() { name = "Failed"; flag = Macro.FLAG_FAILED; }
    }