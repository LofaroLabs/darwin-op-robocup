package sim.app.horde.scenarios.forage.features;

import java.util.List;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.AgentGroup;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.agent.SimControllerAgent;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.features.Feature;
import sim.app.horde.scenarios.forage.agent.Supervisor;
import sim.app.horde.targets.Target;

public class SomeoneNeedsHelp extends Feature
	{
	private static final long serialVersionUID = 1L;

	public static final String getType()
		{
		return "supervisor-l2";
		}

	public SomeoneNeedsHelp()
		{
		super("NeedsHelp");
		targets = new Target[0];
		targetNames = new String[0];
		}

	public double getValue(Agent agent, Macro parent, Horde horde)
		{
		Supervisor s = (Supervisor) agent;
		List<AgentGroup> minions = s.getSubsidiaryAgents();

		for (AgentGroup sub : minions)
			for (Agent sa : sub.getAgents())
				{

				SimAgent a = (SimAgent) sa;

				if (a instanceof SimControllerAgent)
					{
					Supervisor sup = (Supervisor) a;
					if (sup.biggestBox() > sup.agentCount("Forager")) return 1;
					}
				}
		return 0;
		}
	}
