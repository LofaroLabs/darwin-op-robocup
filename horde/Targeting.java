package sim.app.horde;

import sim.app.horde.targets.*;
import sim.app.horde.agent.*;
import sim.app.horde.behaviors.*;

import java.io.*;
import java.util.*;

/**
   TARGETING
   
   <p>A TARGETING object is one which can have one or more TARGETS. 
   Targeting objects include FEATUREs and BEHAVIORs.
   
   <p>Targeting objects hold an array of targets and also an array 
   of associated PARAMETER NAMES for those targets. Targeting objects 
   specify the number of targets by defining these arrays themselves beforehand: you 
   cannnot set the number of targets using any methods below.
   
   <p>When Targeting objects have their start() or stop() methods called, 
   they typically call start() or stop() on their targets as well. Two 
   methods below make this convenient.
*/

public abstract class Targeting implements java.io.Serializable, Cloneable
    {
    private static final long serialVersionUID = 1;

    /*
     * A feature/behavior has a type/category. Agents subscribe to one or more
     * types which makes features/behaviors of that type accessible to the
     * agent. Each subclass should implement a no-args getType() method to
     * provide it's type if different than GLOBAL
     */
    public final static String getType(Class theClass)
        {
        // We don't want this -- Sean
        // if (theClass == null) return Agent.TYPE_GLOBAL;
        try
            {
            java.lang.reflect.Method m = theClass.getMethod("getType", (Class[]) null);
            return (String) (m.invoke(null, (Object[]) null));
            } 
        // We don't want this -- Sean
        //catch (NoSuchMethodException e)
        //{
        //return Agent.TYPE_GLOBAL;
        //} 
        catch (Throwable e)
            {
            e.printStackTrace();
            return "Error in retrieving targetting type";
            }
        }

    protected Target[] targets = new Target[0];
    protected String[] targetNames = new String[0];

    public int getNumTargets() { return targets.length; }

    public void setTarget(int i, Target t) { targets[i] = t; }
    public void setTargets(Target[] t)
        {
        if (t.length == getNumTargets()) for (int i = 0; i < t.length; i++) setTarget(i, t[i]);
        else throw new RuntimeException("Invalid target array");
        }
    public Target getTarget(int i) { return targets[i]; }
    public Target[] getTargets() { return targets; }

    public String getTargetName(int i) { return targetNames[i]; }
    public void setTargetName(int i, String name) { targetNames[i] = name; }
    public void setTargetNames(String[] n)
        {
        if (n.length == getNumTargets()) for (int i = 0; i < n.length; i++) setTargetName(i, n[i]);
        else throw new RuntimeException("Invalid target array");
        }

    public String[] getTargetNames() { return targetNames; }

    public void loadTargetCopiesFrom(Targeting other)
        {
        targets = new Target[other.targets.length];
        for (int i = 0; i < targets.length; i++)
            if (other.targets[i] == null)
                targets[i] = null;
            else
                targets[i] = (Target) (other.targets[i].clone());

        targetNames = new String[other.targetNames.length];

        for (int i = 0; i < targetNames.length; i++)
            targetNames[i] = other.targetNames[i];
        }

    /** Calls start() on all the targets. */
    public void startTargets(Agent agent, Macro parent, Horde horde)
        {
        for (int i = 0; i < targets.length; i++)
            if (targets[i] != null) targets[i].start(agent, parent, horde);
        }

    /** Calls stop() on all the targets. */
    protected void stopTargets(Agent agent, Macro parent, Horde horde)
        {
        for (int i = 0; i < targets.length; i++)
            if (targets[i] != null) targets[i].stop(agent, parent, horde);
        }

    public String allTargets()
        {
        StringBuilder sb = new StringBuilder();

		boolean firstDone = false;
        for (int i = 0; i < targets.length; i++)
            if (targets[i] != null) 
            	{
            	if (firstDone) sb.append(", ");
            	else firstDone = true;
            	sb.append(targetNames[i]).append(":").append(targets[i]);
            	if (targets[i] instanceof Wrapper)
            		{
					sb.append("{").append(((Wrapper)(targets[i])).getTopParameter(((Behavior)this).getParent())).append("}");
					//sb.append("\n   <").append(((Wrapper)(targets[i])).getParentList(((Behavior)this).getParent())).append(">");
					//sb.append("\n->").append(((Wrapper)(targets[i])).allParameters(((Behavior)this).getParent()));
					//sb.append("\n->").append(((Wrapper)(targets[i])).allParameterNames(((Behavior)this).getParent()));
					//sb.append("\n");
            		}
	            }

        return sb.toString();
        }
        
    public String writeAllTargetsHelp(HashSet targetsSoFar)
        {
        String s = "";
        for (int i = 0; i < targets.length; i++)
            {
            if (!targetsSoFar.contains(targetNames[i]))
                {
                s += (targetNames[i] + " ");
                targetsSoFar.add(targetNames[i]);
                }
            }
        return s;
        }

    public Object clone()
        {
        try
            {
            Targeting f = (Targeting) (super.clone());

            // clone targets
            f.targets = new Target[targets.length];
            for (int i = 0; i < f.targets.length; i++)
                f.targets[i] = (targets[i] == null ? null : (Target) (targets[i].clone()));

            // copy target names, don't clone, they're immutable
            f.targetNames = new String[targetNames.length];
            for (int i = 0; i < f.targetNames.length; i++)
                f.targetNames[i] = targetNames[i];
            return f;
            } 
        catch (CloneNotSupportedException e) { return null; /* never happens */ }
        }
    }
