package sim.app.horde.scenarios.robocup2012.features;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.robocup2012.*;

public class YellowGoalZDistance extends Feature
    {
    private static final long serialVersionUID = 1L;

    public YellowGoalZDistance() 
        {
        super("YellowGoalZDistance");
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        return -1; 
        //HumanoidData hd = ((HumanoidAgent) agent).humanoid.getCurrentData();
        //return hd.yellowGoalZDistance;
        }
    }