package sim.app.horde.behaviors;

import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.app.horde.features.Feature;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** BEHAVIOR
   
    <p>The superclass of all behaviors of the agent, which manipulate the robot and
    also which may appear as states in a macro FSA behavior.
   
    <p>Each Behavior has some number of TARGETS which need to be specified for the
    Behavior to work. Behaviors also have names, names for the buttons on which
    they appear in Horde, and also optional associated keystrokes.
   
    <p>The default behavior does nothing at all.
*/

public abstract class Behavior extends Targeting // SEE NOTE BELOW
// in fact we only want TrainableMacros to be serializable. However since
// TrainableMacro is a subclass of Behavior, if Behavior isn't serializable,
// then Behavior's default constructor is called (see the documentation for
// java.io.Serializable). This results in unexpected behavior -- such as
// deserialized TrainableMacros having the name "Composed".
    {       
    private static final long serialVersionUID = 1;
        
        
        
    // RELATIONSHIP TO TRAINABLE MACRO
        
    protected Macro parent = null;

    /** Returns the Macro which presently owns and/or has called start() on this behavior. This value may be null
        if the Macro has never called start() on the behavior yet. getParent() is used by wrappers to determine the
        parent of a Macro or other behavior when computing their wrapped target value. */
    public Macro getParent() { return parent; }

    /** Sets the parent and also calls start() on all targets. When overriding, be sure to call super() */
    public void start(Agent agent, Macro parent, Horde horde)
        {
        this.parent = parent;
        super.startTargets(agent, parent, horde);
        }

    /** Sets the parent and also calls stop() on all targets. When overriding, be sure to call super() */
    public void stop(Agent agent, Macro parent, Horde horde)
        {
        this.parent = parent;
        super.stopTargets(agent, parent, horde);
        }

    /** Sets the parent. When overriding, be sure to call super() */
    public void go(Agent agent, Macro parent, Horde horde) { this.parent = parent; }


        
        
    // STRINGS
        
    protected String name = "Behavior";
        
    /** Returns a human name for the behavior.  May not be unique (though probably is). */
    public String getName() { return name; }
    public String toString() { return name; }

    // String uniqueName;
        
    /* Sets the unique name: else it uses a default value.  The purpose of this is to add
       arguments into the unique name for basic behaviors. */
    //protected void setUniqueName(String n) { uniqueName = n; }
                
    /* Returns the unique name. */
    //protected String getUniqueName() 
//              { 
//              if (uniqueName != null) return uniqueName;
//              else return "B_" + getType(this.getClass()) + "_" + getClass();
//              }

    /** Returns what to display on the button. May be different than toString() if you like. */
    public String getButtonName() { return name; } // what to display on the button

    /** Returns a stack of useful behavior information as a string. */
    public String getBehaviorBacktrace()
        {
        return getBehaviorBacktrace(new StringBuilder()).toString().trim();
        }

    /** Returns a stack of useful behavior information. */
    protected StringBuilder getBehaviorBacktrace(StringBuilder builder)
        {
        return builder.append(toString()).append("(").append(allTargets()).append(")\n");
        }
    
    





    // LOADING BEHAVIORS

    static Class[] basicBehaviorClasses; // stores all basic behavior classes
    static String[] basicBehaviorParams; // companion to basicBehaviorClasses; constructor parameters

    /** Loads all basic behavior classes from basic.behaviors */
    static
        {
        // load the basic behaviors
        ArrayList list = new ArrayList();
        List<String> plist = new ArrayList<String>();
        Scanner scanner = new Scanner(Horde.getStreamRelativeToClass(Horde.locationRelativeClass, Horde.BASIC_BEHAVIORS_LOCATION));
        while (scanner.hasNextLine())
            {
            String s = scanner.nextLine().trim();
            if (s.startsWith("#") || s.length() == 0) continue;
            try 
                { 
                Scanner vscan = new Scanner(s);
                String cn = vscan.next();
                list.add(Class.forName(cn, true, Thread.currentThread().getContextClassLoader())); 
                if (vscan.hasNext())
                    plist.add(vscan.nextLine().trim());
                else
                    plist.add(null);
                } 
            catch (ClassNotFoundException e) { System.err.println("Couldn't find Behavior: " + s); }
            }
        scanner.close();
        basicBehaviorClasses = new Class[list.size()];
        basicBehaviorParams = new String[plist.size()];
        System.arraycopy(list.toArray(), 0, basicBehaviorClasses, 0, list.size());
        System.arraycopy(plist.toArray(), 0, basicBehaviorParams, 0, plist.size());
        }


    /** Returns usable copies of all basic behaviors which can be used by the provided agent. 
        This may involve loading them from cache.  */
    public static Behavior[] provideAllBasicBehaviors(Agent agent)
        {
        // no caching for now -- Sean
                
        ArrayList basic = new ArrayList();
                
        // Filter appropriate classes
        for (int i = 0; i < basicBehaviorClasses.length; i++)
            try
                {
                Class behaviorClass = basicBehaviorClasses[i];
                if (agent.hasType(Targeting.getType(behaviorClass)))  // correct type, make one
                    {
                    Behavior b = null;

                    if (basicBehaviorParams[i] == null || basicBehaviorParams[i].length() == 0)
                        {
                        b = (Behavior) behaviorClass.newInstance();
                        }
                    else
                        {
                        String[] params = basicBehaviorParams[i].split("\\s+");
                        Constructor constructor = behaviorClass.getConstructor(new Class[] { String[].class });
                        b = (Behavior) constructor.newInstance(new Object[] {params});
                        //String s = b.getUniqueName();
                        //for(int j = 0; j < params.length; j++)
                        //      s = s + "_" + params[j];
                        //b.setUniqueName(s); 
                        }
                    basic.add(b);
                    }
                } 
            catch (Exception e)
                {
                e.printStackTrace();
                }

        boolean startFound = false;
        for(int i = 0 ; i < basic.size(); i++)
            if (basic.get(i) instanceof Start)
                { startFound = true; break; }

        if (!startFound)
            System.err.println("WARNING: No \"Start\" Behavior Loaded");

        Behavior[] b = new Behavior[basic.size()];
        basic.toArray(b);
        return b;
        }




    // OUTPUT

    public void write(PrintWriter writer, HashSet<String> behaviorsSoFar)
        {
        if (!behaviorsSoFar.contains(name))
            {
            writer.print(" ( behavior " + name + " " + getType(this.getClass()) + " )\n\n");
            behaviorsSoFar.add(name);
            }
        }






    //// USING THE BEHAVIOR
        
    /** By default returns true -- override this to return false if you want to declare that when transitioning TO this behavior,
        no DEFAULT example should be added to Horde's examples list. */
    public boolean shouldAddExamples() { return true; }

    /** By default returns true -- override this to return false if you want to declare that when transitioning TO this behavior,
        no DEFAULT example should be added to Horde's examples list. */
    public boolean getShouldAddDefaultExample() { return true; }





    // KEYSTROKES

    KeyStroke stroke = null; // likely immutable so we don't have to clone it

    // Various convenience constants for up/down/left/right.
    public static final KeyStroke KS_UP = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
    public static final KeyStroke KS_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
    public static final KeyStroke KS_LEFT = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false);
    public static final KeyStroke KS_RIGHT = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false);

    KeyStroke keyStroke(char val) { return KeyStroke.getKeyStroke(val); }

    /** Sets the keystroke. */
    public void setKeyStroke(KeyStroke val) { stroke = val; }

    /** Sets the keystroke. */
    public void setKeyStroke(char val) { stroke = keyStroke(val); }

    /** Returns the Behavior's keystroke, or null if there is no associated keystroke. */
    public KeyStroke getKeyStroke() { return stroke; }




    }