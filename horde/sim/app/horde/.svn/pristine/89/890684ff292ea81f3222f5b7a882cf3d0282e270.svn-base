package sim.app.horde.scenarios.foraging;

import sim.engine.*;
import sim.portrayal.continuous.*;
import java.awt.*;
import sim.portrayal.grid.*;
import sim.util.gui.*;
import sim.app.horde.*;


public class ForagingHordeWithUI extends HordeWithUI
    {
    public ForagingHordeWithUI()
        {
        this(new ForagingHorde(System.currentTimeMillis()));
        }

    public ForagingHordeWithUI(SimState state)
        {
        super(state);
        }
                
    ContinuousPortrayal2D closestFoodLocPortrayal = new ContinuousPortrayal2D();

    Color foragedColor = new Color(255,0,255);
    FastValueGridPortrayal2D foodPortrayal = new FastValueGridPortrayal2D();

    public void setupPortrayals()
        {
        final ForagingHorde horde = (ForagingHorde) state;
        foodPortrayal.setField(horde.food);
        Color[] c = new Color[] { new Color(255,0,255,0) };
        foodPortrayal.setMap(new SimpleColorMap( c, 0, 255, new Color(255,0,255,128), new Color(255,0,255)));
                
        closestFoodLocPortrayal.setField(horde.closestFoodLoc);

        super.setupPortrayals();
        }

    public void attachPortrayals()
        {
        display.attach(foodPortrayal, "Food!");
        //              display.revalidate();
        super.attachPortrayals();
        display.attach(closestFoodLocPortrayal, "Closest Food Location!");
        }

    public static void main(String[] args)
        {
        new ForagingHordeWithUI().createController();  // randomizes by currentTimeMillis
        }
    }
