package sim.app.horde;
import sim.app.horde.agent.Agent;
import sim.util.*;

/**
   TARGETABLE
        
   <p>A targetable object is one which can be queried by a TARGET of a FEATURE
   to help the target return relevant feature information.  Presently targetable
   objects are required to be able to get and set a location, a "status" (an 
   integer), and a "rank" (another integer).

*/


public interface Targetable
    {
    /** Targetable objects can represent parameters in Horde (the red "A", blue "B", and green "C" objects).
        This object informs them that they've been set to such a parameter and (typically) must change their
        displayed color appropriately.  If they're set to a negative value, they are relieved of
        representing a target and should display themselves with a default color. */
    public void setParameterValue(int index);
        
    /** The location of a target is where its origin is placed in 2D space in the model. */
    public Double2D getTargetLocation(Agent agent, Horde horde);
    public void setTargetLocation(Agent agent, Horde horde, Double2D location);

    public static final double INTERSECTION_SLOP_SQUARED = 1.0;
    /** Does the target intersect with the given location? */
    public boolean getTargetIntersects(Agent agent, Horde horde, Double2D location, double slopSquared);

    /** The status of a targetable is a value assigned to it by the user, for any purpose he sees fit.  There exist
        features which extract this feature. */
    public int getTargetStatus(Agent agent, Horde horde);
    public void setTargetStatus(Agent agent, Horde horde, int status);
        
    /** The rank of a targetable is a unique number assigned to it to distinguish it from other targetable objects. 
        There exist features which compare the rank of various targets with one another. */
    public int getTargetRank(Agent agent, Horde horde);
    public void setTargetRank(Agent agent, Horde horde, int rank);
    }
