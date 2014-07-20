package sim.app.horde.scenarios.forage;

import java.awt.Color;

import sim.app.horde.objects.RectangularBody;
import sim.util.Double2D;

public class Obstacle extends RectangularBody
    {
    private static final long serialVersionUID = 1L;

    ForageHorde horde;

    public Obstacle(ForageHorde horde, int w, int h)
        {
        super(w, h, horde.obstacles, new Double2D(0, 0));
        this.horde = horde;
        }

    public void newRandomLocation()
        {
        loc = new Double2D(horde.random.nextDouble() * field.getWidth(), horde.random.nextDouble() * field.getHeight());
        field.setObjectLocation(this, loc);
        }

    protected void setup()
        {
        defaultPaint = paint = Color.red;
        setMinimumAttachments(2);
        }

    public void decrementAttachment()
        {
        super.decrementAttachment();

        // everyone let go, so remove obstacle
        if (getAttachments() == 0) field.remove(this);
        }
        
    public String toString() { return "Forage Obstacle"; } 
    }
