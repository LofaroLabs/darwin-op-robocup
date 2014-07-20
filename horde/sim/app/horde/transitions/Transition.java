package sim.app.horde.transitions;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.*;
import java.io.*;

/** 
    TRANSITION

    Abstract basic class for transitions.  Transition by itself never transitions anywhere.
    If you're creating a basic MACRO, you can just create your own Transition functions
    as you see fit.  But most likely you're using a TRAINABLE MACRO, in which case you're
    using a LEARNED TRANSITION.
        
    <p>Transitions have start() and stop() methods, and also a change() method which indicates
    the new behavior to transition to.
*/

public class Transition implements Serializable, Cloneable  // SEE NOTE
                        // in fact we only want LearnedTransitions to be serializable.  However since 
                        // LearnedTransitions is a subclass of Transition, if Transition isn't serializable,
                        // then Transition's default constructor is called (see the documentation for
                        // java.io.Serializable).  This could result in unexpected behavior.
    {
    private static final long serialVersionUID = 1;

    public void start(Agent agent, Macro parent, Horde horde) { }
    public void stop(Agent agent, Macro parent, Horde horde) { }
    public int change(Agent agent, Macro parent, Horde horde) { return parent.currentBehavior; } // do nothing

    public Object clone()
        {
        try 
            {
            return super.clone();
            }
        catch (CloneNotSupportedException e) { return null; /* never happens */ }
        }
        
    public void write(PrintWriter writer, boolean writeDomain) { }
        
    }
