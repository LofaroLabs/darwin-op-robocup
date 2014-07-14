package sim.app.horde.scenarios.foraging;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class Release extends Behavior
    {
    private static final long serialVersionUID = 1;
    public Release() { name = "Release"; setKeyStroke('r'); }
    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
        ((Forager)agent).release();
        }
    }
