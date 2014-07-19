package sim.app.horde.scenarios.boxpushing.features;

import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.boxpushing.*;

public class BoxStraight extends CategoricalFeature
{

	private static final long serialVersionUID = 1L;

	public BoxStraight()
	{
		super("DistToBox", new String[] {"False", "True"});		
	}

	public double getValue(Agent agent, Macro parent, Horde horde) 
	{
		PioneerAgent p = (PioneerAgent) agent; 
		double angle = p.getBoxAngle(); 
		if (Math.abs(angle) < 5)
			return 1; 
		else 
			return 0; 		
	}
}