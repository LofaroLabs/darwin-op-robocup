package sim.app.horde.scenarios.robocup2012.features;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.robocup2012.*;

public class BallBearing extends Feature
    {
    private static final long serialVersionUID = 1L;

    public BallBearing() 
        {
        super("BallBearing");
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        HumanoidData hd = ((HumanoidAgent) agent).humanoid.getCurrentData();
        return hd.ballBearing;
        }
    }