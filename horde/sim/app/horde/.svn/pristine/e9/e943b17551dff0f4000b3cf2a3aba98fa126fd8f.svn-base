package sim.app.horde.scenarios.forage.targets;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.objects.Marker;
import sim.app.horde.scenarios.forage.ForageHorde;
import sim.app.horde.targets.*;
import sim.util.Bag;

public class HomeBase extends Target
    {
    private static final long serialVersionUID = 1;

    public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
        {
        ForageHorde simhorde = (ForageHorde) horde;
        Bag markers = simhorde.markers.getAllObjects(); 
                
        for (int i=0; i < markers.size(); i++) { 
            Marker m = (Marker) markers.objs[i]; 
            if (m.toString().equalsIgnoreCase("Home Base"))
                return m; 
            }
                
        return null; 
        }
        
    public String toString() { return "Home Base"; } 

    }
