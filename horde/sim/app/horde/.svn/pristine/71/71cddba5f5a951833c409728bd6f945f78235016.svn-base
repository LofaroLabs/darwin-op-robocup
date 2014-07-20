package sim.app.horde.scenarios.foraging;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.Double2D;

import sim.app.horde.*;
import sim.util.*;

public class ForagingHorde extends SimHorde
    {

    private static final long       serialVersionUID        = 3136448823048746533L;

        
        
    static
        {
        // change Horde's default locations for elements. Hopefully before the
        // other classes load
        BASIC_BEHAVIORS_LOCATION = "scenarios/foraging/basic.behaviors";
        BASIC_TARGETS_LOCATION = "scenarios/foraging/basic.targets";
        BASIC_FEATURES_LOCATION = "scenarios/foraging/basic.features";
        TRAINABLE_MACRO_DIRECTORY = Horde.getPathInDirectory("scenarios/foraging/trained/");
        }


    public Agent buildNewAgent()
        {
        Forager agent = new Forager(this);  // all the agents will use the training macro for now

        // build a random location
        Double2D d = null;
        do
            {
            d = new Double2D(random.nextDouble() * width, random.nextDouble() * height);
            } while (Utilities.collision(this, d, null));

        agent.setPose(d, random.nextDouble() * Math.PI * 2 - Math.PI);
        agent.stoppable = schedule.scheduleRepeating(agent);
        return agent;
        }




    public static String getPathInForagingDirectory(String s)
        {
        return ForagingHorde.class.getResource("").getPath()+"/" +s;
        }

    agent.setPose(d, random.nextDouble() * Math.PI * 2 - Math.PI);
    agent.stoppable = schedule.scheduleRepeating(agent);
    */
    return agent;
                
    }

public static String getPathInForagingDirectory(String s)
    {
    return ForagingHorde.class.getResource("").getPath() + "/" + s;
    }

public String           cfl                             = "closest food";
public Continuous2D     closestFoodLoc  = new Continuous2D(width, width, height);

public IntPBMGrid2D loadFood()
    {
    IntPBMGrid2D f = new IntPBMGrid2D(getPathInForagingDirectory("food.pbm"));
    for (int i = 0; i < f.field.length; i++)
        for (int j = 0; j < f.field[i].length; j++)
            f.field[i][j] *= 255;
    return f;
    }

public IntPBMGrid2D     food    = loadFood();

public void start()
    {
    super.start();
    loadFood();
        
    schedule.scheduleRepeating(new Steppable() {
        private static final long       serialVersionUID        = -7528696918741338119L;

        public void step(SimState state)
            {
            closestFoodLoc.setObjectLocation(cfl, ((Forager) (getTrainingAgent())).closestFood(ForagingHorde.this));
            }
        });
    }
}
