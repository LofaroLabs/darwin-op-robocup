package sim.app.horde.scenarios.pioneer.features;

import sim.app.horde.agent.Agent;
import sim.app.horde.Horde;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.pioneer.PioneerAgent;
import sim.app.horde.features.*; 

public class ClosestFrontSonar extends Feature
    {

    private static final long serialVersionUID = 1L;

    public ClosestFrontSonar() { super("Closest Front Distance"); } 
        
    public double getValue(Agent agent, Macro parent, Horde horde) { 
        return ((PioneerAgent)agent).closestFrontDistance; 
        }
        
    }
