package sim.app.horde.scenarios.forage.agent;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.app.horde.Horde;
import sim.app.horde.SimHorde;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.objects.Body;
import sim.app.horde.scenarios.forage.behaviors.Grab;
import sim.app.horde.scenarios.forage.behaviors.Release;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;

public class Forager extends SimAgent
	{
	private static final long serialVersionUID = 1L;

	public final static Color HAS_FOOD_PAINT = Color.green;
	public final static Color HAS_NO_FOOD_PAINT = Color.blue;

	public double MINIMUM_DEPOSIT_DISTANCE = 5;

	public static final int NUM_STATUSES = 3;
	public static final int FREE_STATUS = 0;
	public static final int ATTACHED_STATUS = 4;
	public static final int SEE_BOX_STATUS = 2;

	// am i close enough to home? used by CodedAgent and Box
	public static final int HOME_RANGE = 5;
	public static final int RANGE = 10; // how far can the agent see a box?

	// The manipulated box with the highest attachment requirements known to
	// this agent or it's parents
	//private int biggestBox = -1;

	// The forager that has manipulated the biggest box
	//private Forager attachedToBiggestBox;

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
		{
		super.draw(object, graphics, info);

		/*
		 * if (biggestAttachedAgent != null) { Paint tmpPaint = paint; paint =
		 * Color.BLACK; Double2D l = biggestAttachedAgent.getLocation(); double
		 * dx = l.x - loc.x; double dy = l.y - loc.y; int x2 = (int)
		 * (info.draw.x + info.draw.width * dx); int y2 = (int) (info.draw.y +
		 * info.draw.height * dy);
		 * 
		 * graphics.drawLine((int) info.draw.x, (int) info.draw.y, x2, y2);
		 * paint = tmpPaint; }
		 */

		}

	/*
	public int getManipulatedStatus()
		{
		if (manipulated != null)
			return manipulated.getTargetStatus(this, horde);
		return -1;
		}

	public void setManiupatedStatus(int s)
		{
		if (manipulated != null && s > 0)
			manipulated.setTargetStatus(this, horde, s);
		}

	public boolean getAttachToBox()
		{
		return (manipulated != null);
		}
*/
	public void setAttachToBox(boolean val)
		{
		if (val)
			new Grab().go(this, (Macro) (this.getBehavior()), getHorde());
		else
			new Release().go(this, (Macro) (this.getBehavior()), getHorde());
		}

	public void step(SimState state)
		{
/*
		Supervisor ctrl = (Supervisor) getController();

		if (manipulated != null && manipulated instanceof Body)
			{
			biggestBox = ((Body) manipulated).getMinimumAttachments();
			attachedToBiggestBox = this;
			}

		if (ctrl != null)
			{
			Forager attachedAgent = ctrl.attachedAgent();

			// Is there an overall biggest attached agent?
			if (attachedAgent != null)
				{
				// the attached agent let go (presumably because it was
				// deposited at
				// the destination, so the parents have bad info about the
				// biggest
				if (attachedAgent.manipulated == null)
					ctrl.resetBiggest();

				// This agent is grabbing something, but it's not the biggest,
				// so release that one and focus on the biggest
				else if (manipulated != null
						&& ((Body) attachedAgent.manipulated).loc != ((Body) manipulated).loc)
					{
					setAttachToBox(false);
					attachedToBiggestBox = attachedAgent;
					biggestBox = attachedAgent.biggestBox;
					}
				}
			}
*/
		super.step(state);

		/*
		 * String s = getUnderlyingBehavior().getName(); if (s != "Start" && s
		 * != "Done") { System.out.println(getUnderlyingBehavior());
		 * System.exit(-1); }
		 */

		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.app.horde.scenarios.forage.Foraging#resetBiggest()
	public void resetBiggest()
		{
		Supervisor ctrl = (Supervisor) getController();

		// Reset the parent if: 1) There is a controller and it has a biggest
		// attached agent and 2) The biggest box of the controller is not
		// larger than my biggest box (meaning it got it's biggest box info
		// from another forager)
		if (ctrl != null && ctrl.attachedToBiggestBox() != null
				&& (biggestBox > 0 && biggestBox >= ctrl.biggestBox()))

		ctrl.resetBiggest();

		biggestBox = -1;
		attachedToBiggestBox = null;
		}
	 */

	public int biggestBox()
		{
		
		int biggest = -1;
		if (manipulated != null && manipulated instanceof Body)
			{
			biggest = ((Body) manipulated).getMinimumAttachments();
			}
		return biggest;
		}
/*
	public void setBiggestBox(int biggest)
		{
		biggestBox = biggest;
		}

	public Forager attachedToBiggestBox()
		{
		return attachedToBiggestBox;
		}

	public void setAttachedToBiggestBox(Forager f)
		{
		attachedToBiggestBox = f;
		} */
	}
