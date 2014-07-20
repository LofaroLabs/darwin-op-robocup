package sim.app.horde.scenarios.boxpushing;

import sim.engine.*;
import sim.app.horde.agent.*;
import sim.app.horde.*;
import sim.app.horde.agent.SimControllerAgent;
import sim.app.horde.behaviors.Macro;

public class PioneerController extends SimControllerAgent
{
	private static final long serialVersionUID = 1L;

	private static final String[] types = { "Global", "Controller", "PioneerController" };

	public int doneCount = 0;

	public int getDoneCount()
	{
		return doneCount;
	}

	public PioneerController(Horde horde)
	{
		super(horde);

		// TODO: There has to be a better way than this, something in the metadata maybe
		addTypes(types); // define behaviors and features for a Forager

	}

	public void step(SimState state)
	{
		super.step(state);

		doneCount = 0;

		for (AgentGroup ag : getSubsidiaryAgents())
		{
			// Get the biggest box for all children
			for (int i = 0; i < ag.getSize(); i++)
			{
				SimAgent a = (SimAgent) ag.getAgent(i);

				if (((Macro) (a.getBehavior())).getFlag(Macro.FLAG_DONE))
				{
					doneCount++;
				}

			}
		}

	}
}