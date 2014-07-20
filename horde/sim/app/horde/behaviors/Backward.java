package sim.app.horde.behaviors;


public class Backward extends Forward
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public Backward() { super(-0.1);  name = "Backward[" + -speed + "]"; setKeyStroke(null); } // good default
    }
