package sim.app.horde.behaviors;

/** A simple behavior which does nothing.  However Macros detect transition
    to this behavior, which causes them to signal their "done" flag. */
        
public class Done extends Flag
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.Agent.TYPE_GLOBAL; }

    public Done() { name = "Done"; flag = Macro.FLAG_DONE; }
    }