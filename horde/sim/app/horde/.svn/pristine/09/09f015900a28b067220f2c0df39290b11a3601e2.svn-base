package sim.app.horde.scenarios.robot.darwin.targets;
import sim.app.horde.targets.*;
import sim.app.horde.*;
import sim.util.*;
import sim.engine.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import java.util.*;
import java.io.*;
import sim.portrayal.*;
import sim.app.horde.scenarios.robot.darwin.comm.*;
import sim.app.horde.scenarios.robot.darwin.agent.*;

public class RobotOne extends Target implements Serializable, Cloneable
    {
    private static final long serialVersionUID = 1;
     
    public String toString() { return "Robot One"; }
    
    /** Returns the REAL location of Robot one */
    public Double2D getLocation(Agent agent, Macro parent, Horde horde)
        {
        DarwinParser dp = ((DarwinAgent) horde.getTrainingAgent()).getCurrentData();
	double x = dp.getOtherRobotX(1) * 10;
	double y = dp.getOtherRobotY(1) * 10;
        return new Double2D(x,y);
        }
                
    /** Returns the orientation of the target-able object, or 0 if there is no such object. */
    public double getOrientation(Agent agent, Macro parent, Horde horde)
        {
        DarwinParser dp = ((DarwinAgent) horde.getTrainingAgent()).getCurrentData();
        return dp.getOtherRobotA(1);
	}
    }
