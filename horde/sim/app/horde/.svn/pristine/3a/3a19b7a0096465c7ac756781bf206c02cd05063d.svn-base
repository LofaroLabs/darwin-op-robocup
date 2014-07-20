/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.flockbots;


import static sim.app.horde.Horde.getPathInDirectory;
import static sim.app.horde.Horde.initialParameterObjectName;

import sim.app.horde.SimHorde;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.TrainableMacro;
import static sim.engine.SimState.doLoop;

public class FlockbotHorde extends SimHorde
	{
	

	public static boolean experiment = false;

	
	
	private static final long serialVersionUID = 1L;

	public FlockbotHorde(long seed)
	{
	super(seed);

	setBasicBehaviorLocation("scenarios/robot/flockbots/flockbots.behaviors");
	setBasicTargetLocation("scenarios/robot/flockbots/flockbots.targets");
	setBasicFeatureLocation("scenarios/robot/flockbots/flockbots.features");
	setTrainableMacroDirectory(getPathInDirectory("scenarios/robot/flockbots/trained/"));
	//setAgentMetaDirectory(getPathInDirectory("scenarios/robot/flockbots/meta/"));
        
	}

	public Behavior initBehavior(Agent a, Behavior b)
        {
            // Setting the behavior to a new untrained macro
            if (b == null)
                    a.setBehavior(new TrainableMacro(a.getName()).reset(this,
                                    buildNewParameters(), initialParameterObjectName,
                                    Behavior.provideAllBehaviors(a), currentFeatures()));
            else
                    a.setBehavior((Behavior) (b.clone()));
            return a.getBehavior();
        }

	public void startBypass()
        {
            super.start();
        }

        @Override
	public void start()
        {
            arena = "scenarios/robot/flockbots/arena/L3.arena.txt";

            schedule.reset();

            super.start();

        }

        @Override
	public void finish()
        {
            // properly clean up the running threads
            
            
        }

	public void buildEnvironment()
        {

        }

	public static void main(String[] args)
		{
		experiment = true;
                
		doLoop(FlockbotHorde.class, args);
		System.exit(0);
		}

	}

