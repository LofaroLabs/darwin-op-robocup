package sim.app.horde.scenarios.forage;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.List;
import java.util.Scanner;

import sim.app.horde.Horde;
import sim.app.horde.SimHorde;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.objects.CircularBody;
import sim.app.horde.scenarios.forage.agent.Forager;
import sim.app.horde.scenarios.forage.agent.Supervisor;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class Box extends CircularBody
	{
	private static final long serialVersionUID = 1L;
	SimHorde horde;

	public Box(double scale, Continuous2D field, Double2D loc2)
		{
		super(scale, field, loc2);
		}

	public Box(SimHorde horde, String params)
		{
		this(5, ((ForageHorde) horde).obstacles, new Double2D(0, 0));
		this.horde = horde;
		initFromParamString(params);
		}

	protected void setup()
		{
		defaultPaint = paint = new java.awt.Color(64, 64, 128, 128);
		setMinimumAttachments(2);
		}

	public void newRandomLocation()
		{
		loc = new Double2D(horde.random.nextDouble() * field.getWidth(), horde.random.nextDouble()
				* field.getHeight());

		// set a random size, limited to the number of agents in the system
		// setMinimumAttachments(horde.random.nextInt(ForageHorde.totalNumAgents));

		field.setObjectLocation(this, loc);
		}

	public Double2D getTargetLocation(Agent agent, Horde horde)
		{
		return loc;
		}

	public void targetted(boolean isTargetted)
		{
		/*
		if (isTargetted)
			paint = Color.green;
		else
			paint = Color.black; */
		}

	public void decrementAttachment()
		{
		super.decrementAttachment();
		Double2D pos = field.getObjectLocation(this);
		double dist = pos.distance(((ForageHorde) horde).getMarker("Home Base").getTargetLocation(null,
				horde));
		if (dist <= 2 * Forager.HOME_RANGE)
			{ // move to new random location
			ForageHorde.collectedBoxes++;
			ForageHorde.collectedWeight += minimumAttachments;
			newRandomLocation();

			while (getAttachments() > 0)
				super.decrementAttachment();

			setTargetStatus(null, null, 0);

			// In this case the last agent released the box
			ForageHorde cHorde = (ForageHorde) horde;
			List<Agent> agentList = cHorde.allAgents.get("Forager");

			for (int i = 0; i < agentList.size(); i++)
				{
				SimAgent a = (SimAgent) agentList.get(i);
				if (a.manipulated == this)
					{
					a.manipulated = null;
					if (a instanceof Forager)
						{
						Forager f = (Forager) a;
						Supervisor ctrl = (Supervisor) a.getGroup().getController();

						if (ctrl != null) ctrl.resetBiggest(f);
						}
					Macro cb = (Macro) a.getBehavior();
					//cb.finished = true;
					cb.setFlag(Macro.FLAG_DONE, true, true);

					}
				}
			}
		}

	/**
	 * Initialize the box from a parameter string. The parameters are white
	 * space delimited with the following format: <min_attachments>
	 * 
	 */
	public void initFromParamString(String params)
		{
		Scanner vscan = new Scanner(params);
		setMinimumAttachments(vscan.nextInt());
		int fieldNum = vscan.nextInt();
		
		if (fieldNum == 0)
			field = ((SimHorde) horde).obstacles;
		else if (fieldNum == 1) field = ((SimHorde) horde).regions;

		newRandomLocation();
		}

	@Override
	public boolean collision(Double2D p)
		{
		// TODO Auto-generated method stub
		return false;
		}

	@Override
	public boolean collision(Area a)
		{
		// TODO Auto-generated method stub
		return false;
		}

	@Override
	public boolean collision(Shape sh)
		{
		// TODO Auto-generated method stub
		return false;
		}
	}
