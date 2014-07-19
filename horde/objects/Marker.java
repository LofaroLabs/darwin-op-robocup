package sim.app.horde.objects;

import java.awt.*;
import sim.util.*;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.portrayal.*;

/**
   MARKER
        
   <p>A Marker is a point in space which can be a Targetable.  Markers are presently
   displayed as little "X"s.  You can't collide with a marker.
*/

public class Marker extends SimplePortrayal2D implements Targetable
    {
    private static final long serialVersionUID = 1;
    String label;
    Paint defaultPaint = Color.black;
    Paint paint = null;
    Paint[] borders = new Paint[]{null, Color.black, Color.orange};
    Paint border = borders[0];
    int status = 0;
    
    public void setDefaultPaint(Paint p) 
    	{
    	if (p == null) p = Color.black;
    	defaultPaint = p; 
    	}
    
    public void setTargetRank(Agent agent, Horde horde, int rank) {} 
    public int getTargetRank(Agent agent, Horde horde) { return 0; }

    public void setParameterValue(int index) {
        if (index >= 0) paint = SimHorde.parameterObjectColor[index];
        else paint = null; 
        }

    public boolean getTargetIntersects(Agent agent, Horde horde, Double2D location, double slopSquared)
        {
        return getTargetLocation(agent, horde).distanceSq(location) <= slopSquared;
        }

    public Double2D getTargetLocation(Agent agent, Horde horde)
        {
        return ((SimHorde)horde).markers.getObjectLocation(this);
        }
                
    public void setTargetLocation(Agent agent, Horde horde, Double2D location)
        {
        ((SimHorde)horde).markers.setObjectLocation(this, location);
        }
                
    public Marker(String label)
        {
        this.label = label;
        }

    public int getTargetStatus(Agent agent, Horde horde)
        {
        return status;
        }

    public void setTargetStatus(Agent agent, Horde horde, int status)
        {
        this.status=status;
        if (status >= 0 && status < borders.length) 
            border = borders[status];
        else System.err.println("Status out of bounds for border in Marker.java: " + status);
        }
                
    public void setLabel(String label) { this.label = label; }
    
    public String toString() { return label; }
        
    /** Draws the Marker as an "X" */
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        graphics.setPaint(paint == null ? defaultPaint : paint);
        int x1 = (int)(info.draw.x - info.draw.width/2);
        int x2 = (int)(info.draw.x + info.draw.width/2);
        int y1 = (int)(info.draw.y - info.draw.height/2);
        int y2 = (int)(info.draw.y + info.draw.height/2);
                
        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x1, y2, x2, y1);  // big 'ol X
                
        if (border!=null)
            {
            graphics.setPaint(border);
            graphics.drawOval(x1-5,y1-5,x2-x1+10,y2-y1+10);
            }
        }

    /** Returns true if the Marker was hit by the given MASON rectangle. */
    public boolean hitObject(Object object, DrawInfo2D range)
        {
        final double width = range.draw.width;
        final double height = range.draw.height;
        return( range.clip.intersects( range.draw.x-width/2, range.draw.y-height/2, width, height ) );
        }

    }
