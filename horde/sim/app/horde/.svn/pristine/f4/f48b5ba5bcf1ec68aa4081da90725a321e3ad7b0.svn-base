package sim.app.horde.scenarios.heteroboxpushing.features.create;

import sim.app.horde.*;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.heteroboxpushing.create.CreateAgent;

public class CenterBumper extends CategoricalFeature
    {
    private static final long       serialVersionUID        = 1L;

    public CenterBumper()
        {
        super("Center Bumper", new String[] { "Not Pressed", "Pressed" });
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        CreateAgent roomba = (CreateAgent) agent;
        if (roomba.bumpRight() && roomba.bumpLeft())
            return 1;
        else 
            return 0;
        }
    }
