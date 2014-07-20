package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.targets.*;

public class ResetStatus extends SetStatus
    {
    private static final long serialVersionUID = 1;
        
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public ResetStatus()
        {
        super(0);
        name = "ResetStatus";
        setKeyStroke('0');
        } 
    }
