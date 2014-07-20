package sim.app.horde.scenarios.forage.targets;

import sim.app.horde.Horde;
import sim.app.horde.Targetable;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.forage.Box;
import sim.app.horde.scenarios.forage.ForageHorde;
import sim.app.horde.targets.Target;
import sim.util.Bag;
import sim.util.Double2D;

public class ClosestBox extends Target
	{
	private static final long serialVersionUID = 1;

	public Targetable getTargetable(Agent agent, Macro parent, Horde horde)
		{
		ForageHorde simhorde = (ForageHorde) horde;
		SimAgent simagent = (SimAgent) agent;
		Double2D loc = simagent.getLocation();
		Bag boxes = simhorde.obstacles.getAllObjects(); // we'll just do a scan
		Box best = (Box) (boxes.objs[0]);
		best.targetted(false);
		double bestDistance = loc.distanceSq(best.getTargetLocation(simagent, simhorde));

		for (int i = 1; i < boxes.numObjs; i++)
			{
			Box o = (Box) (boxes.objs[i]);
			double d = loc.distanceSq(o.getTargetLocation(simagent, simhorde));

			if (d < bestDistance)
				{
				best = o;
				bestDistance = d;
				}
			o.targetted(false);
			}

		best.targetted(true);

		//if (bestDistance <= Forager.RANGE)
		return best;
		//return null;
		}

	public String toString()
		{
		return "Closest Box";
		}

	}
