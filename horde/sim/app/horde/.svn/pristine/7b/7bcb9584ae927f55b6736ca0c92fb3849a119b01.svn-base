package sim.app.horde.scenarios.foraging;


import sim.engine.*;
import sim.util.*;
import sim.portrayal.*;
import sim.app.horde.*;
import java.awt.*;
import sim.app.horde.objects.*;
import sim.util.gui.*;

public class Forager extends SimAgent
    {
    private static final long serialVersionUID = -7291862425858981630L;
    public final static Color HAS_ALL_FOOD_PAINT = Color.red;
    public final static Color HAS_FOOD_PAINT = Color.green;
    public final static Color HAS_NO_FOOD_PAINT = new Color(0,0,255,0);
    public final static int TOTAL_FOOD = 71;  // something weird
    public SimpleColorMap cmap = new SimpleColorMap(0, TOTAL_FOOD, HAS_NO_FOOD_PAINT, HAS_FOOD_PAINT);
        

    public Forager(Horde horde)
        {
        super(horde);
        }


    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        // a hack to draw both filled and a border!
        super.draw(object, graphics, info);
        filled = false;
        Paint oldPaint = paint;
        if (totalFood == TOTAL_FOOD) paint = HAS_ALL_FOOD_PAINT;
        else
            paint = cmap.getColor(totalFood);
        super.draw(object, graphics, info);
        filled = true;
        paint = oldPaint;
        }

    int totalFood = 0;
    public void setTotalFood(int val) 
        { 
        totalFood = val;
        }

    public int getTotalFood() { return totalFood; }

    public int getScroungeableFood(ForagingHorde horde)
        {
        int width = horde.food.getWidth();
        int height = horde.food.getHeight();
        int locx = (int)(loc.x *  (width / horde.agents.getWidth()));
        int locy = (int)(loc.y *  (height / horde.agents.getHeight()));
        if (locx < 0 || locy < 0 | locx >= width || locy >= height)
            return 0;
        return horde.food.field[locx][locy];
        }

    Targetable load = null;
    public double MINIMUM_GRAB_DISTANCE = 5;
    public void grab(Targetable targetable, Horde horde)
        {
        if (load != null) return;  // we've already got something, sorry
        Double2D tloc = targetable.getTargetLocation(this, horde);
        if (tloc.distance(loc) > MINIMUM_GRAB_DISTANCE) return; // sorry, too far away
        load = targetable;
        // location will be set by me at the end of the step
        }

    public void release()
        {
        load = null;
        }

    public void step(SimState state)
        {
        super.step(state);
        
        System.out.println("FORAGER: "); 
        
        if (load != null)
            load.setTargetLocation(this, (Horde)state, loc);
        }

    public void scrounge(ForagingHorde horde)
        {
        int width = horde.food.getWidth();
        int height = horde.food.getHeight();
        int locx = (int)(loc.x *  (width / horde.agents.getWidth()));
        int locy = (int)(loc.y *  (height / horde.agents.getHeight()));
        if (locx < 0 || locy < 0 | locx >= width || locy >= height)
            return;  // failed

        if (horde.food.field[locx][locy] > 0 && totalFood < TOTAL_FOOD)
            {
            horde.food.field[locx][locy]--;
            totalFood++;
            }
        }

    public double MINIMUM_DEPOSIT_DISTANCE = 5;
    public void unload(Targetable targetable, ForagingHorde horde)
        {
        Double2D tloc = targetable.getTargetLocation(this, horde);
        if (tloc.distance(loc) <= MINIMUM_DEPOSIT_DISTANCE)
            {
            totalFood = 0;
            }
        }

    Double2D buildDouble2D(int x, int y, ForagingHorde horde)
        {
        return new Double2D( (x + 0.5) * horde.agents.getWidth() / horde.food.getWidth(),
            (y + 0.5) * horde.agents.getHeight() / horde.food.getHeight() );
        }

    public Double2D closestFood(ForagingHorde horde)
        {
        Double2D best = null;
        double bestd = 0;
        for(int i = 0; i < horde.food.field.length; i++)
            {
            int[] fieldx = horde.food.field[i];
            for(int j = 0; j < fieldx.length; j++)
                {
                if (fieldx[j] > 0)
                    if (best == null || 
                        buildDouble2D(i,j, horde).distanceSq(loc) < bestd)
                        {
                        best = buildDouble2D(i,j,horde);
                        bestd = best.distanceSq(loc);
                        }       
                }
            }
        if (best == null) best = new Double2D(0,0);
        return best;
        }
    public boolean collision(Double2D location)
        {
        return  this.collision((SimHorde)horde,location,this);
        }
    public boolean collision(SimHorde horde, Double2D location, Agent agent)
        {
        //double x = location.x;
        //double y = location.y;

        // check for borders
        /*
          if (x > horde.width || x < 0 || y > horde.height || y < 0)
          return true;
        */

        // check for obstacles
        //        Bag b = horde.obstacles.getObjectsWithinDistance(location, 0);
        //System.out.println("Checking for new collisions!");
        Bag b = new Bag(horde.obstacles.getAllObjects());
        b.remove(load);
        for (int i = 0; i < b.numObjs; i++)
            {
            if (((Body) (b.objs[i])).collision(location))
                return true;
            }

        // check for other agents.  Right now we're requiring a little slack (0.05, less than the 0.1 distance for various operators)
        // -- otherwise stuff like wall following has the agents wandering around one another.
        b = horde.agents.getObjectsExactlyWithinDistance(location, 0.05);
        if (b.size() == 0)
            return false;
        if (b.size() > 1)
            return true;
        if (b.objs[0] == agent)
            return false;

        return false;
        }


    }
