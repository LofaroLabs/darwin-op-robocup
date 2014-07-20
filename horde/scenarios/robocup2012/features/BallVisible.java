package sim.app.horde.scenarios.robocup2012.features;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.robocup2012.*;

public class BallVisible extends CategoricalFeature
    {
    private static final long serialVersionUID = 1L;

    public BallVisible()
        {
        super("BallVisible", new String[]{"False", "True"}); 
        }
        
    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        HumanoidData hd = ((HumanoidAgent) agent).humanoid.getCurrentData();
        return hd.ballVisible; 
        }
    }
