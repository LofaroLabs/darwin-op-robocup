package sim.app.horde.scenarios.boxpushing;

import sim.app.horde.*;

public class BoxpushingHitab extends SimHorde
{
	private static final long serialVersionUID = 1L;

	public BoxpushingHitab(long seed)
	{
		super(seed);

		setBasicBehaviorLocation("scenarios/boxpushing/boxpushing.behaviors");
		setBasicTargetLocation("scenarios/boxpushing/boxpushing.targets");
		setBasicFeatureLocation("scenarios/boxpushing/boxpushing.features");
		setTrainableMacroDirectory(getPathInDirectory("scenarios/boxpushing/trained/"));
		setAgentMetaDirectory("scenarios/boxpushing/meta/");
	}

	@Override
	public void start()
	{
		// may need something like this
		//arena = "scenarios/boxpushing/arena/L3.arena.txt";
		//arena = "scenarios/boxpushing/arena/pioneer.arena.txt";
		//arena = "scenarios/boxpushing/arena/flockbot.controller.arena.txt";
		arena = "scenarios/boxpushing/arena/pioneer.controller.arena.txt";  

		
		schedule.reset();

		super.start();

		resetBehavior();

	}

	public static void main(String args[])
	{
		doLoop(BoxpushingHitab.class, args);
		System.exit(0);
	}

}