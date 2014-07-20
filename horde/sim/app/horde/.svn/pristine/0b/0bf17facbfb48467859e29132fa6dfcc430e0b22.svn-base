package sim.app.horde.scenarios.boxpushing.behaviors.pioneer;

import sim.app.horde.agent.*;

import sim.app.horde.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.boxpushing.*;

public class Stop extends Behavior
{
	private static final long serialVersionUID = 1L;

	public Stop()
	{
		name = "PioneerStop";
	}

	public boolean getShouldAddDefaultExample()
	{
		return false;
	}

	public void go(Agent agent, Macro parent, Horde horde)
	{
		super.go(agent, parent, horde);
		((PioneerAgent) agent).bot.stopRobot();
	}
}
