package sim.app.horde.objects;
import java.awt.geom.*;
import sim.util.*;
import sim.field.continuous.*;

public class CircularBody extends Body
    {
    private static final long serialVersionUID = 1;
    public CircularBody(double diameter, Continuous2D field, Double2D location)
        {
        super(new Ellipse2D.Double(0, 0, diameter, diameter ), field, location);
        }
        
    public String toString() { return "Circular Body"; }
    }