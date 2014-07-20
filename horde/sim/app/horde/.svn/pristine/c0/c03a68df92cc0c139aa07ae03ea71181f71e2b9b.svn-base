package sim.app.horde.scenarios.pioneer.features;

import sim.app.horde.scenarios.pioneer.PioneerAgent;
import sim.app.horde.scenarios.pioneer.targets.AttackerColor;
import sim.app.horde.scenarios.pioneer.targets.HomeColor;
import sim.app.horde.scenarios.pioneer.targets.ScatterColor;
import sim.app.horde.scenarios.pioneer.targets.TeamColor;
import sim.app.horde.targets.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;
import sim.app.horde.targets.Target;
import sim.app.horde.targets.Wrapper;

public class BlobXCoord extends Feature
    {
    private static final long serialVersionUID = 1L;

    public BlobXCoord() {
        super("X-Coord");
        targets = new Target[1];
        targetNames = new String[1];
        targets[0] = new HomeColor();
        targetNames[0] = "Color";
        }

    public double getValue(Agent agent, Macro parent, Horde horde)
        {
        BlobData bd = ((PioneerAgent) agent).blobData;

        int idx = 0;

        Target t = targets[0];

        if (targets[0] instanceof Wrapper)
            {
            Wrapper w = (Wrapper) targets[0];
            t = w.getTopParameter(parent);
            }

        if (t instanceof Parameter)
            {
            Parameter p = (Parameter) t;
            idx = p.getIndex();
            }
        else if (t instanceof HomeColor)
            idx = 0;
        else if (t instanceof AttackerColor)
            idx = 1;
        else if (t instanceof TeamColor)
            idx = 2;
        else if (t instanceof ScatterColor)
            idx = 3;

        double coord = -1;
        if (bd.xMin[idx] >= 0)
            coord = bd.xMin[idx] + (bd.xMax[idx] - bd.xMin[idx]) / 2.0;
        return coord;
        }
    }
