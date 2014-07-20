package sim.app.horde.scenarios.forage;

import java.awt.Color;

import sim.app.horde.*;
import sim.engine.SimState;
import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;

public class ForageHordeWithUI extends SimHordeWithUI
	{
	//ContinuousPortrayal2D blocksPortrayal = new ContinuousPortrayal2D();
	//ContinuousPortrayal2D homePortrayal = new ContinuousPortrayal2D();

	public ForageHordeWithUI()
	{
	this(new ForageHorde(System.currentTimeMillis()));
	}

	public ForageHordeWithUI(SimState state)
	{
	super(state);
	}

	public void setupPortrayals()
		{
		super.setupPortrayals();
		//setupAgentsAndPlacesPortrayals();
		}

	public void attachPortrayals()
		{
		display.attach(obstaclesPortrayal, "Boxes");
		display.attach(controllerPortrayal, "Relationships");
		display.attach(agentsPortrayal, "Foragers");
		display.attach(placesPortrayal, "Targets");
		}

	public static void main(String[] args)
		{
		new ForageHordeWithUI().createController();
		}
	}
