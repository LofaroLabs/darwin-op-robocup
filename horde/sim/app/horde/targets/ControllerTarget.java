package sim.app.horde.targets;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.util.*;

/**

   CONTROLLER TARGET
   
   <p>ControllerTarget is used by AgentGroup to specify the proper targets for subsidiaries to
   use in joint behaviors.  When a controller agent wishes his subsidiaries to do some 
   behavior foo(tar) where tar is a target, we want the target's methods to get passed
   the subsidiary AGENT but need to provide the controller's MACRO.  This is because
   the target might be a WRAPPER and wrappers need to refer to the controller's macro
   to unwrap properly.  However the target values (like "closestAgentTo...") need to
   be in reference to the subsidiary AGENT.  The way we do this is by passing in
   a special target (the ControllerTarget) which holds the original target, but instead
   hands it a the controller's macro instead of the "parent" provided.
   
*/


public class ControllerTarget extends Target
    {
    private static final long serialVersionUID = 1;
        
    Target target;
    Macro macro;
    
    public ControllerTarget(Target target, Macro macro)
    	{ this.target = target; this.macro = macro; }
        
    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        return target.getTargetable(agent, macro, horde);
        }

    public int getStatus(Agent agent, Macro parent, Horde horde)
        {
        return target.getStatus(agent, macro, horde);
        }

    public int getRank(Agent agent, Macro parent, Horde horde)
        {
        return target.getRank(agent, macro, horde);
        }

    public Double2D getLocation(Agent agent, Macro parent, Horde horde)
        {
        return target.getLocation(agent, macro, horde);
        }
                
    public double getOrientation(Agent agent, Macro parent, Horde horde)
        {
        return target.getOrientation(agent, macro, horde);
        }
        
    public String toString() { return "Controller[" + target +"]"; }
    }
