package sim.app.horde.scenarios.pioneer.features;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.pioneer.PioneerAgent;
import sim.app.horde.features.*; 

public class ClosestRearSonar extends Feature
    {

    private static final long serialVersionUID = 1L;

    public ClosestRearSonar() { super("Closest Read Distance"); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((PioneerAgent)agent).closestRearDistance; 
        }
        
    }
