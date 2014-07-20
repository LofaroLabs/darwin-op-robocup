package sim.app.horde.targets;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;

/**
   PARAMETER
        
   A parameter is one of a few special targets stored in the currently-being-trained TrainableMacro.  Wrappers
   located in behaviors and features in that TrainableMacro can point to the parameter to refer to it.  When
   the TrainableMacro is saved and then reloaded as a behavior in a higher-level TrainableMacro, one of the first
   things that is done is to replace its Parameters with Wrappers which refer (initially) to higher-level Parameters
   in the outer TrainableMacro.
        
   <p>Every Parameter has an index which points to a Targetable stored in Horde which currently is standing in for
   this Parameter.  You can get this from horde.getParameterObject(index).  Parameters also have names, initially
   thing like "A", "B", or "C", though in future versions we should be able to change those parameter names.
*/


public class Parameter extends Target
    {
    private static final long serialVersionUID = 1;
    int index;  // a pointer to the particular parameter in the Horde's Targets array.  Don't confuse with the index in the TrainableMacro, which is used by Wrapper.
    String name;

    public Parameter(int index, String name)
        {
        this.index = index;
        this.name = name;
        }

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        return ((SimHorde)horde).getParameterObject(index);
        }

    public void setName(String s)
        {
        name = s;
        }
                
    public String getName()
        {
        return name;
        }

    public String toString()
        {
        return getName();
        }

    public int getIndex()
        {
        return index;
        }
    }
