package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.*;
import sim.app.horde.targets.*;

public class Detatch extends Behavior 
    {
    private static final long serialVersionUID = 1L;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public boolean shouldAddDefaultExample() { return false; }
        
    public Detatch()
        {
        name = "Detatch";
        }
    
    public void start(Agent agent, Macro parent, Horde horde) 
        {            
        ((SimAgent)agent).setManipulated(null);
        }
    }
