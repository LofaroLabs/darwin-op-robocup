package sim.app.horde.scenarios.foraging;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;

public class Scrounge extends Behavior
    {
    private static final long serialVersionUID = 1;
    public Scrounge() { name = "Scrounge"; setKeyStroke('s'); }
    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
        ((Forager)agent).scrounge((ForagingHorde) horde);
        }
    }
