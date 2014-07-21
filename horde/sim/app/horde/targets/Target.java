package sim.app.horde.targets;
import sim.app.horde.*;
import sim.util.*;
import sim.engine.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import java.util.*;
import java.io.*;
import sim.portrayal.*;

/**

   TARGET
        
   A target is an object on which a feature is declared.  For example, a target might be "closest obstacle to me",
   and a feature on that target might be "color of(target)".  In Horde, targets presently have (1) locations,
   (2) orientations (3) statuses -- arbitrary states usually set by the user and (4) ranks -- every target has
   a unique number to enable an ordering among them.  Features use these items to compute feature values.
        
   <p>Many targets compute these items -- such as locations -- based on some TARGETABLE object.  For example,
   the location of the "closest obstacle to me" is determined by identifying what the closest obstacle *is*, then
   using that obstacle as the TARGETABLE to query location and orientation information.
        
   <p>The default versions of many of the methods below first extract the targetable using getTargetable(...),
   then query it for location, orientation, etc.  Many Target subclasses simply override getTargetable and
   leave it at that -- but you are welcome to override the specific other methods for more fine tuning.

   <p>All targets have a START method and a STOP method, in addition to various query methods.  The START
   method will always be called prior to a sequence of queries, and the STOP method will always be called after a sequence
   of queries.

*/

public abstract class Target implements Serializable, Cloneable
    {
    private static final long serialVersionUID = 1;
        
    /** Called to prepare the Target for possible upcoming queries.  "parent" is the macro in which the Target resides. */
    public void start(Agent agent, Macro parent, Horde horde) { }

    /** Called to shut down the Target after a series of queries.  "parent" is the macro in which the Target resides. */
    public void stop(Agent agent, Macro parent, Horde horde) { }
        
    /** Returns the targetable object.  The default version returns null. */
    public Targetable getTargetable(Agent agent, Macro parent, Horde horde) { return null; }

    /** Returns the location of the targetable object, or (0,0) if there is no such object. */
    public Double2D getLocation(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = getTargetable(agent, parent, horde);
        if (obj != null) 
            return obj.getTargetLocation(agent, horde);
        return new Double2D(0,0);
        }
                
    /** Returns the orientation of the targetable object, or 0 if there is no such object. */
    public double getOrientation(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = getTargetable(agent, parent, horde);
        if (obj != null && obj instanceof Oriented2D)
            return ((Oriented2D) obj).orientation2D();
        else return 0;
        }

    /** Returns the status of the targetable object, or 0 if there is no such object. */
    public int getStatus(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = getTargetable(agent, parent, horde);
        if (obj != null)
            return obj.getTargetStatus(agent, horde);
        else return 0;
        }
                
    /** Returns the location of the targetable object, or Integer.MIN_VALUE if there is no such object. */
    public int getRank(Agent agent, Macro parent, Horde horde)
        {
        Targetable obj = getTargetable(agent, parent, horde);
        if (obj != null)
            return obj.getTargetRank(agent, horde);
        else return Integer.MIN_VALUE;
        }

    /** Returns the previous location of the targetable object, or (0,0) if there is no such object.
        This can be used to determine speed, for example.  */
    public Double2D getLastLocation(Agent agent, Macro parent, Horde horde)
        {
        update(agent, parent, horde);
        return lastLoc;
        }
                
    /** Returns the previous orientation of the targetable object, or 0 if there is no such object.
        This can be used to determine rotational velocity, for example.  */
    public double getLastOrientation(Agent agent, Macro parent, Horde horde)
        {
        update(agent, parent, horde);
        return lastOrientation;
        }
                
                
    // we presume we're called every timestep if at all!
    double lastTime = Schedule.BEFORE_SIMULATION;
    double currentTime = Schedule.BEFORE_SIMULATION;
    Double2D currentLoc = null;
    Double2D lastLoc = null;
    double lastOrientation;
    double currentOrientation;
        
    // private method, updates the above variables to determine our last location and orientation
    void update(Agent agent, Macro parent, Horde horde)
        {
        double time = horde.schedule.getTime();
        if (time > currentTime)  // we have a new time, update
            {
            lastTime = currentTime;
            currentTime = time;
            lastLoc = currentLoc;
            lastOrientation = currentOrientation;
            currentLoc = getLocation(agent, parent, horde);
            currentOrientation = getOrientation(agent, parent, horde);
            }
        else if (time == currentTime)  // I'm at the current time, do nothing
            {
            }
        else    // maybe we got reset
            {
            currentTime = lastTime = time;
            currentLoc = lastLoc = getLocation(agent, parent, horde);
            currentOrientation = lastOrientation = getOrientation(agent, parent, horde);
            }
        }

    /** All basic targets */
    public static Class[] basicTargetClasses;

    /** Loads all the targets from Horde.BASIC_TARGETS_LOCATION, which can change
        depending on the scenario defined.  These are "ground targets"
        (non-Wrapper, non-Parameter targets like "Closest obstacle"). */
    static
        {
        // load the basic (ground) targets
        ArrayList list = new ArrayList();
        Scanner scanner = new Scanner(Horde.getStreamRelativeToClass(Horde.locationRelativeClass, Horde.BASIC_TARGETS_LOCATION));
        
        while(scanner.hasNextLine())
            {
            String s = scanner.nextLine().trim();
            if (s.startsWith("#") || s.length() == 0) continue;
            try { list.add(Class.forName(s, true, Thread.currentThread().getContextClassLoader())); }
            catch (ClassNotFoundException e) { System.err.println("Couldn't find Target: " + s); }
            }
        scanner.close();
        basicTargetClasses = new Class[list.size()];
        System.arraycopy(list.toArray(), 0, basicTargetClasses, 0, list.size());
        
        }


    /** Returns all basic targets, plus Wrappers for the Parameters in the current trainable macro.
        This is largely used to build a list of targets to attach to Features. 
    */
    public static Target[] provideAllTargets(TrainableMacro macro)
        {
        int tlen = macro.getNumTargets();

        Target[] targets = new Target[tlen + basicTargetClasses.length];
                        
        // get the parameters from the macros and make wrappers
        for(int i = 0; i < tlen; i++)
            targets[i] = macro.getTarget(i);   // REPLACED BY SEAN: I THINK THIS IS RIGHT // new Wrapper(macro, i);
                
        // get the basic targets
        for(int i = 0; i < basicTargetClasses.length; i++)
            try { targets[i+tlen] = (Target)(basicTargetClasses[i].newInstance()); }
            catch (Exception e) { e.printStackTrace(); }

        return targets;
        }

    public abstract String toString();
        
    public Object clone()
        {
        try 
            {
            return super.clone();
            }
        catch (CloneNotSupportedException e) { return null; /* never happens */ }
        }

    }
