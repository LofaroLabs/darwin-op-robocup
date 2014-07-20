/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.foraging;

import sim.app.horde.Agent;
import sim.app.horde.Horde;
import sim.app.horde.targets.*;
import sim.app.horde.features.*;

public class FoodBelowMe extends Feature
    {
    private static final long serialVersionUID = 1;

    public FoodBelowMe()
        {
        super("Food_Below_Me");
        targets = new Target[0];  // no targets
        targetNames = new String[0];  // no targets
        }
                
    public double getValue(Agent agent, Horde horde)
        {
        return (((Forager)agent).getScroungeableFood((ForagingHorde)horde));
        }
    }
