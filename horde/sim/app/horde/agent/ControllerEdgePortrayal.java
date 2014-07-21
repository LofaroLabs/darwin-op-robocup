package sim.app.horde.agent;

import sim.portrayal.*;
import java.awt.*;
import sim.field.network.*;
import java.awt.geom.*;
import sim.util.*;
import sim.portrayal.network.*;

import sim.app.horde.*;

public class ControllerEdgePortrayal extends SimpleEdgePortrayal2D
	{
	Horde horde;
	
	public ControllerEdgePortrayal(Horde horde) { this.horde = horde; }
	
	static final Color PINK = new Color(255, 180, 180);
	static final Color LIGHT_BLUE = new Color(180, 180, 255);
	static final Color LIGHT_GRAY = new Color(200, 200, 200);
	
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
		{
		// Right now we're "drawing" every single edge (calling this method), but only actually
		// *drawing* the edges if appropriate.  If we wanted to make this faster, we'd remove
		// all the edges from the network except for the relevant ones.  For now, this is
		// an acceptable approach speedwise (I think).
		
		Edge edge = (Edge) object;
		Object from = edge.getFrom();
		Object to = edge.getTo();
		
		if (from == horde.getTrainingAgent() || 
			to == horde.getTrainingAgent())
			{
			if (from == horde.getTrainingAgent())
				{
				// an edge to my superior
				fromPaint = Color.BLACK;
				toPaint = Color.RED;
				}
			else
				{
				// an edge to my subordinate
				fromPaint = Color.BLUE;
				toPaint = Color.BLACK;
				}
			super.draw(object, graphics, info);
			return;
			}
		
		// how about selected agents that are not being trained or are not trainable?
		// There are two ways we could go about doing this.
		// 1. Add a 'selected' boolean to AgentPortrayal2D, which gets selected or deselected
		//    when setSelected() is called.  This adds more bloat.
		// 2. Look up the agent in the selected wrappers.  This is slower as it involves building
		//    a small array every single check.
		//
		// I have gone with #2 for the time being
		LocationWrapper[] wrappers = ((SimHordeWithUI) (info.gui)).display.getSelectedWrappers();
		for(int i = 0; i < wrappers.length; i++)
			if (wrappers[i] == from || wrappers[i] == to)
				{
				if (wrappers[i] == from)
					{
					// an edge to my superior
					fromPaint = LIGHT_GRAY;
					toPaint = PINK;
					}
				else
					{
					// an edge to my subordinate
					fromPaint = LIGHT_BLUE;
					toPaint = LIGHT_GRAY;
					}
				super.draw(object, graphics, info);
				return;
				}
		}
		
	public boolean hitObject(Object object, DrawInfo2D range)
		{
		Edge edge = (Edge) object;
		if (edge.getFrom() == horde.getTrainingAgent() || 
			edge.getTo() == horde.getTrainingAgent())
			return super.hitObject(object, range);
		else return false;
		}
	}