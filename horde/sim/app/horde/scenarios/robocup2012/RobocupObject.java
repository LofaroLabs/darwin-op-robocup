package sim.app.horde.scenarios.robocup2012;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import sim.app.horde.scenarios.robocup2012.features.*;
import sim.portrayal.*;


public abstract class RobocupObject extends SimplePortrayal2D
    {
    private static final long serialVersionUID = 1L;

    double width;
    double height;
    Color objectColor = Color.white; 
    public String type = "Ball";
                
    public RobocupObject(Color c, String typ) {
        objectColor = c; 
        type = typ;
        }

    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        graphics.setPaint(objectColor); 
            
        double w = width * info.draw.width;
        double h = height * info.draw.height;
        double x = info.draw.x;
        double y = info.draw.y; 
            
        //           System.err.println(type + " From " + info.draw);
        //           System.err.println(type + " Printing " + new Rectangle2D.Double(x, y, w, h));
            
        graphics.fill(new Rectangle2D.Double(x, y, w, h)); 
        }

    public abstract void update(HumanoidHorde hHorde, HumanoidData data);
        
    }
