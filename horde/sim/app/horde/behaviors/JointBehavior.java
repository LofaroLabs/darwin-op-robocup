package sim.app.horde.behaviors;

import java.util.*;
import javax.swing.*;
import java.io.*;
import ec.util.*;

import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.app.horde.targets.*;

public class JointBehavior extends Behavior
    {
    static final long serialVersionUID = 1L;

    public static final String P_NAME = "name";  // yes, I know this isn't a Parameter, it's a string
    public static final String P_DOES = "does";  // yes, I know this isn't a Parameter, it's a string
    public static final String P_HOMOGENEOUS_PREFIX = "-";  // so the Joint for "Done" is different than the basic for "Done"
    volatile int[] behaviorIndices = null;  // initially
    String[] behaviorNames;  // really only useful for debugging
        
    /** Creates a homogeneous behavior of the same name as the provided Behavior */
    public JointBehavior(Controller controller, Behavior behavior)
        {
        name = P_HOMOGENEOUS_PREFIX + behavior.getName();  // same name

        // load names
        behaviorNames = new String[] { behavior.getName() }; 
                
        // allow joint targets with care
        if (behavior.getTargets().length != 0)
            {
            System.err.println("WARNING: Joint Behavior created for behavior with a target: " + this);
            targets = new Target[behavior.getTargets().length];
            for(int i = 0; i < targets.length; i++)   // set defaults
                if (behavior.getTargets()[i] == null)  // it's a null target below, so make me null too
                	targets[i] = null;
                // else make me something legitimate
                else 
                	targets[i] = new Me();  
            targetNames = new String[behavior.getTargetNames().length];
            setTargetNames(behavior.getTargetNames());  // use same names as the subsidiary targets
            }
        }
                
    public JointBehavior(Controller controller, ec.util.ParameterDatabase db, ec.util.Parameter base)
        {
        // load name
        name = db.getString(base.push(P_NAME), null);
        if (name == null)
            throw new RuntimeException("Heterogeneous Joint Behavior created with null name at: " + base.push(P_NAME));
                
        // figure out joint sub-behavior names
        ArrayList<AgentGroup> subs = controller.getSubsidiaryAgents();
        if (subs.size() == 0)
            throw new RuntimeException("Zero subsidiary agent types for heterogenous Joint Behavior");

        // load names
        behaviorNames = new String[subs.size()];
        for(int i = 0; i < subs.size(); i++)
            behaviorNames[i] = db.getString(base.push(P_DOES).push("" + i), null);
                
        // can we allow joint targets?
        int numTargets = -1;
        for(int i = 0; i < subs.size(); i++)
            {
            Behavior behavior = subs.get(i).getAgent(0).getBehavior().behaviors[behaviorIndices[i]];
            if (numTargets != -1 && behavior.getNumTargets() != numTargets) // uh oh
                throw new RuntimeException("Heterogeneous joint behavior created with uneven targets");
            numTargets = behavior.getNumTargets();
            }
        
        // can we allow joint targets?  -- step two, verify all null or all non-null
        for(int j = 0; j < numTargets; j++)
        	{
        	boolean allNull = true;
        	boolean allNonNull = true;
        	String targetName = null;
	        for(int i = 0; i < subs.size(); i++)
    	        {
        	    Behavior behavior = subs.get(i).getAgent(0).getBehavior().behaviors[behaviorIndices[i]];
        	    if (behavior.getTarget(j) != null)
        	    	allNull = false;
        	    if (behavior.getTarget(j) == null)
        	    	allNonNull = false;
        	    if (i == 0) targetName = behavior.getTargetName(j);
        	    else if ((targetName == null && behavior.getTargetName(j) != null) || 
        	    			!targetName.equals(behavior.getTargetName(j)))  // hmmm, not matching target names
        	    	System.err.println("Warning: target name for target " + j + " does not match: (Behavior 0: " + targetName + " vs. Behavior " + i + ": " + behavior.getTargetName(j));
    	        }
        	if (!allNull && !allNonNull) 
                throw new RuntimeException("Heterogeneous joint behavior created with non-matching targets");
    	    }

        
        // okay, so they all have the same number of targets...
        // allow joint targets with care.  For the moment we're using the target names
        // of agent group 0.  Maybe we should allow the user to specify the target names?
        if (numTargets != 0)
            {
        	System.err.println("WARNING: Joint Behavior created for behavior with a target: " + this);
            targets = new Target[numTargets];
            for(int i = 0; i < targets.length; i++)   // set defaults
                {
                Behavior behavior = subs.get(i).getAgent(0).getBehavior().behaviors[behaviorIndices[i]];
            	if (behavior.getTargets()[i] == null)  // it's a null target below, so make me null too
                	targets[i] = null;
                // else make me something legitimate
                else
                	targets[i] = new Me();  
                } 
            targetNames = new String[numTargets];
            setTargetNames(subs.get(0).getAgent(0).getBehavior().behaviors[behaviorIndices[0]].getTargetNames());  // use same names as the subsidiary targets
            }
        }
        
    public void rebuildIndices(Controller controller)
        {
        ArrayList<AgentGroup> subs = controller.getSubsidiaryAgents();
        int size = subs.size();
        if (size != behaviorNames.length)
            throw new RuntimeException("Incorrect number of subsidiary agent types for Joint Behavior (" + size + ")");

        if (behaviorIndices == null) behaviorIndices = new int[size];
        for(int i = 0; i < size; i++)
            {
            if (behaviorNames[i] == null)
                throw new RuntimeException("Joint Behavior " + getName() + " has empty behavior name for subsidiary " + i);
            try 
                {
                behaviorIndices[i] = subs.get(i).getAgent(0).indexOfBehavior(behaviorNames[i]);
                }
            catch (Exception e)  // uh oh
                {
                throw new RuntimeException("No subsidiary behavior called " + behaviorNames[i] + " exists as behavior " + i + " for Joint Behavior " + getName());
                }
            }
        }
    
    // we call start here rather than go so that we can cut down
    // on constant demands of the subsidiaries.  Hope this works right -- Sean 
    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
        
        Controller controller = (Controller)agent;
        
        // dunno if this is worth the overhead
        if (behaviorIndices == null || horde.getShouldRebuildJointBehaviorIndices())
            rebuildIndices(controller);

        ArrayList<AgentGroup> sub = controller.getSubsidiaryAgents();

        for (int i = 0; i < behaviorIndices.length; i++)
            ((AgentGroup)(sub.get(i))).fireBehaviors(this, behaviorIndices[i], parent);
        }

    public String toString()
        {
        StringBuilder sb = new StringBuilder(name);
        sb.append("->[");
        for(int i = 0; i < behaviorNames.length; i++)
            sb.append(behaviorNames[i]);
        sb.append("]");
        return sb.toString();
        }
    }
