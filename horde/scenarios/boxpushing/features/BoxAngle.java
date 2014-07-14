package sim.app.horde.scenarios.boxpushing.features;

import sim.app.horde.*;
import sim.app.horde.agent.*;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.*;
import sim.app.horde.scenarios.boxpushing.*;

public class BoxAngle extends Feature
{

	private static final long serialVersionUID = 1L;

	public BoxAngle()
	{
		super("BoxAngle");		
	}
	

	public double getValue(Agent agent, Macro parent, Horde horde) 
	{
		PioneerAgent p = (PioneerAgent) agent; 
		return p.getBoxAngle(); 
	}
}