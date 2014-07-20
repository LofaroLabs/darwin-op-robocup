package sim.app.horde.scenarios.foraging;
import sim.app.horde.*;
import sim.app.horde.targets.*;
import sim.util.*;

public class ClosestFood extends Target
    {
    private static final long serialVersionUID = 1;
        
    public static final String TARGET = "The Food Target";
    public Object getTarget(Agent agent, Horde horde)
        {
        return TARGET;
        }
                
    public Double2D getLocation(Agent agent, Horde horde)
        {
        return ((Forager)agent).closestFood((ForagingHorde)horde);
        }
                
    public double getOrientation(Agent agent, Horde horde)
        {
        return 0;
        }
    public int getStatus(Agent agent, Horde horde)
        {
        return 0;
        }
    public String toString() { return "Closest Food"; }
    }
