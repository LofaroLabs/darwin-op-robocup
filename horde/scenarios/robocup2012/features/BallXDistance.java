package sim.app.horde.scenarios.robocup2012.features;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.robocup2012.*;

public class BallXDistance extends Feature
    {
    private static final long serialVersionUID = 1L;

    public BallXDistance() 
        {
        super("BallXDistance");
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        HumanoidData hd = ((HumanoidAgent) agent).humanoid.getCurrentData();
        return hd.ballXDistance;
        }
    }
