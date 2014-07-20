package sim.app.horde.objects;
import java.awt.geom.*;
import sim.util.*;
import sim.field.continuous.*;

public class RectangularBody extends Body
    {
    private static final long serialVersionUID = 1;
    public RectangularBody(double width, double height, Continuous2D field, Double2D location)
        {
        super(new Rectangle2D.Double(0, 0, width, height), field, location);
        }
    public String toString() { return "Rectangular Body"; }
    }