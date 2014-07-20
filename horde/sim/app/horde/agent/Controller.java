package sim.app.horde.agent;
import java.util.*;
import sim.app.horde.*;
import sim.app.horde.behaviors.*;


public interface Controller
    {
    /** Agent parameter for the number of subsidiary agents to this [controller] agent. */
    public static final ec.util.Parameter P_NUM_SUBS = new ec.util.Parameter("num-subs");
    /** Agent parameter for specific subsidiary agents to this [controller] agent, such as sub.0, sub.1, etc. */
    public static final ec.util.Parameter P_SUB = new ec.util.Parameter("sub");
    /** Agent parameter for the number of joint behaviors to this [controller] agent.
        If the agent is homogeneous AND the number of joint behavors is zero, then
        behaviors will be automatically generated.  */
    public static final ec.util.Parameter P_NUM_JOINTS = new ec.util.Parameter("num-joints");
    /** Agent parameter for specific joint behaviors to this [controller] agent, such as joint.0, joint.1, etc. */
    public static final ec.util.Parameter P_JOINT = new ec.util.Parameter("joint");
    /** Agent parameter which indicates whether all of the subsidiary's behaviors should be automatically
        generated (if automatic generation is occuring).  By default this is false. */  
    public static final ec.util.Parameter P_ALLOW_ALL_BEHAVIORS = new ec.util.Parameter("all-behaviors");

    /** Returns a list of all subsidiary agent groups. */
    public ArrayList<AgentGroup> getSubsidiaryAgents();

    /** Though this is public -- because interfaces must be public --
        do NOT call this method.  Instead, call reload() or Agent.provideAgent(...) */
    public void setup(ec.util.ParameterDatabase db);
    
    /** Provides all joint behaviors for the agent. */
    public Behavior[] provideJointBehaviors(ec.util.ParameterDatabase db);
    
    
    public void computeRequirements(List<String> basicTypes);
    public void dispenseForController(List<String> basicTypes, int [] basicAgentAlloc, HierarchyConstraints constraints);
    
    /** Basic agent requirements for a given basic agent type */
    public int getComputedMaxAgents(int basicTypeIndex);
    public int getComputedMinAgents(int basicTypeIndex);
    public int getComputedPreferredAgents(int basicTypeIndex);
    
    public int agentCount(String agentType);
    
    public void scheduleAgents(Horde horde);
    }
