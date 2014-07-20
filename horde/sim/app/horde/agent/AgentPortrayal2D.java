/*
 * Copyright 2013 by Sean Luke and George Mason University Licensed under the Academic Free License version 3.0 See the
 * file "LICENSE" for more information
 */

package sim.app.horde.agent;
import sim.portrayal.*;
import java.awt.*;
import sim.display.*;
import java.awt.event.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import sim.app.horde.behaviors.*;

/**
   This superclass of all agent classes handles
   basic drawing for simple agents.
        
   <p>AgentPortrayal2D is, in MASON parlance, a "wrapper portrayal",
   meaning that it does its work largely by calling on a chain of other
   portrayals to draw and handle hit-testing and movement for it.  The top
   of the chain is "child", and the bottom of the chain is "bottom", with the
   second-to-bottom element being "op2d" -- we need the second-to-bottom element
   because the bottom element frequently is replaced.
        
*/

public class AgentPortrayal2D extends SimplePortrayal2D
    {
    /** The top portrayal in the AgentPortrayal2D wrapper chain. */
    SimplePortrayal2D child;
    /** The bottom portrayal in the AgentPortayal2D wrapper chain, often replaced. */
    AbstractShapePortrayal2D bottom;
    /** The second to bottom portrayal in the AgentPortrayal2D wrapper chain.  To replace "bottom", we modify its pointer here. */
    OrientedPortrayal2D op2d;
    
    public static final int SHAPE_CIRCLE = 0;
    public static final int SHAPE_SQUARE = 1;
    public static final int SHAPE_HEXAGON = 2;
    public static final int SHAPE_TRIANGLE_UP = 3;
    public static final int SHAPE_TRIANGLE_DOWN = 4;
    public static final int SHAPE_TRIANGLE_LEFT = 5;
    public static final int SHAPE_TRIANGLE_RIGHT = 6;
    public static final int SHAPE_DIAMOND = 7;
    public static final int SHAPE_BOWTIE = 8;
    public static final int SHAPE_HOURGLASS = 9;
    public static final int SHAPE_OCTAGON = 10;
    public static final int SHAPE_HEXAGON_ROTATED = 11;
    
    /** Parameter value names corresponding to the varous SHAPE_... constants. */
    public static final String[] shapeNames = 
        new String[] { "circle", "square", "hexagon", "triangle-up", "triangle-down", "triangle-left",
                       "triangle-right", "diamond", "bowtie", "hourglass", "octagon", "hexagon-rotated" };
        
    /** Available predefined colors. */
    public static final Color[] predefinedColors = new Color[] { Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
                                                                 Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW };
    
    /** Parameter value names corresponding to various avaialble colors in predefinedColors[]. */
    public static final String[] colorNames = new String[] { "black", "blue", "cyan", "dark-gray", "gray", "green", "light-gray",
                                                             "magenta", "orange", "pink", "red", "white", "yellow" };
    
    /** Sets the shape drawn for the agent as specified by the provided name.  Must be one of shapeNames[] */ 
    public void setShape(String shapeName)
        {
        for(int i = 0 ; i < shapeNames.length; i++)
            if (shapeNames[i].equalsIgnoreCase(shapeName.trim()))
                { setShape(i); return; }
        setShape(SHAPE_CIRCLE);
        System.err.println("WARNING: Invalid shape name \"" + shapeName + "\", using \"circle\"");
        }
        
    /** Sets the default color drawn for the agent as specified by the provided name.  Must be one of predefinedColors[] */ 
    public void setDefaultColor(String colorName)
        {
        for(int i = 0; i < colorNames.length; i++)
            if (colorNames[i].equalsIgnoreCase(colorName.trim()))
                { setDefaultPaint(predefinedColors[i]); return; }
        setDefaultPaint(Color.GRAY);
        System.err.println("WARNING: Invalid color name \"" + colorName + "\", using \"black\"");
        setPaint(getDefaultPaint());
        }
    
    /** Sets the color drawn for the agent as specified by the provided SHAPE_... constant. */ 
    public void setShape(int shape)
        {
        boolean filled = bottom.filled;
        double scale = bottom.scale;
        Paint paint = bottom.paint;
        
        switch(shape)
            {
            case SHAPE_CIRCLE:
                bottom = new OvalPortrayal2D();
                break;
            case SHAPE_SQUARE:
                bottom = new RectanglePortrayal2D();
                break;
            case SHAPE_HEXAGON:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_HEXAGON, ShapePortrayal2D.Y_POINTS_HEXAGON);
                break;
            case SHAPE_TRIANGLE_UP:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_TRIANGLE_UP, ShapePortrayal2D.Y_POINTS_TRIANGLE_UP);
                break;
            case SHAPE_TRIANGLE_DOWN:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_TRIANGLE_DOWN, ShapePortrayal2D.Y_POINTS_TRIANGLE_DOWN);
                break;
            case SHAPE_TRIANGLE_LEFT:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_TRIANGLE_LEFT, ShapePortrayal2D.Y_POINTS_TRIANGLE_LEFT);
                break;
            case SHAPE_TRIANGLE_RIGHT:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_TRIANGLE_RIGHT, ShapePortrayal2D.Y_POINTS_TRIANGLE_RIGHT);
                break;
            case SHAPE_DIAMOND:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_DIAMOND, ShapePortrayal2D.Y_POINTS_DIAMOND);
                break;
            case SHAPE_BOWTIE:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_BOWTIE, ShapePortrayal2D.Y_POINTS_BOWTIE);
                break;
            case SHAPE_HOURGLASS:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_HOURGLASS, ShapePortrayal2D.Y_POINTS_HOURGLASS);
                break;
            case SHAPE_OCTAGON:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_OCTAGON, ShapePortrayal2D.Y_POINTS_OCTAGON);
                break;
            case SHAPE_HEXAGON_ROTATED:
                bottom = new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_HEXAGON_ROTATED, ShapePortrayal2D.Y_POINTS_HEXAGON_ROTATED);
                break;
            default:
                throw new RuntimeException("Invalid Shape: " + shape);
            }
        bottom.filled = filled;
        bottom.paint = paint;
        bottom.scale = scale;
        
        op2d.child = bottom;  // reset it
        }
        
    /** Sets the color drawn for the agent as specified by the provided SHAPE_... constant. */ 
    protected Paint defaultPaint = Color.GRAY;
    
    /** Sets the default paint which should be used to draw the agent.
        Also sets the agent's paint, though this can be overridden with setPaint() */
    public void setDefaultPaint(Paint paint)
        {
        defaultPaint = paint;
        setPaint(defaultPaint);
        }
    
    /** Returns the default paint which should be used to draw the agent. */
    public Paint getDefaultPaint()
        {
        return defaultPaint;
        }
        
    /** Sets the paint the agent uses to draw. */
    public void setPaint(Paint paint)
        {
        bottom.paint = paint;
        }
    
    /** Sets the scale at which the agent is drawn. Default: 1.0. Must be a value > 0.0. */
    public void setScale(double scale) 
        {
        bottom.scale = scale;
        }
        
    /** Sets whether the agent is drawn filled or not. */
    public void setFilled(boolean filled)
        {
        bottom.filled = filled;
        }
    
    /** Returns the label to be drawn with a selected agent.  Override this
        method to return a different label. */
    public String getLabel(Agent agent)
        {
        return agent.getBehaviorBacktrace();
        }

    /** Creates an AgentPortrayal2D, including the wrapper portrayal chain. */
    public AgentPortrayal2D()
        {
        bottom = new OvalPortrayal2D();
        op2d = new OrientedPortrayal2D(bottom, 0, 1.0);
            
        LabelledPortrayal2D lp2d = new LabelledPortrayal2D(op2d, null)
            {
            private static final long serialVersionUID = 1L;
            public String getLabel(Object object, DrawInfo2D info) { return AgentPortrayal2D.this.getLabel((Agent) object); }
            };
                    
        lp2d.setOnlyLabelWhenSelected(true);
        child = new AdjustablePortrayal2D(lp2d);
        }
    
    // This text is largely cribbed from wrapper portrayals in MASON
    
    /** Draws the AgentPortrayal2D */                         
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        child.draw(object,graphics,info);
        }
        
    /** Hit-Tests the AgentPortrayal2D */                         
    public boolean hitObject(Object object, DrawInfo2D range)
        {
        return child.hitObject(object,range);
        }

    /** Selects or deselects the AgentPortrayal2D */                         
    public boolean setSelected(LocationWrapper wrapper, boolean selected)
        {
        return child.setSelected(wrapper, selected);
        }

    /** Inspects the AgentPortrayal2D */                         
    public Inspector getInspector(LocationWrapper wrapper, GUIState state)
        {
        return child.getInspector(wrapper,state);
        }
    
    /** Returns the AgentPortrayal2D's name for purposes of inspection. */                         
    public String getName(LocationWrapper wrapper)
        {
        return child.getName(wrapper);
        }

    /** Handles mouse events for the AgentPortrayal2D. */                         
    public boolean handleMouseEvent(GUIState guistate, Manipulating2D manipulating, LocationWrapper wrapper,
        MouseEvent event, DrawInfo2D fieldPortrayalDrawInfo, int type)
        {
        return child.handleMouseEvent(guistate, manipulating, wrapper, event, fieldPortrayalDrawInfo, type);  // let someone else have it
        }
    }