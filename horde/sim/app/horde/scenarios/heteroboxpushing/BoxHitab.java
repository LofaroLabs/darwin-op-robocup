package sim.app.horde.scenarios.heteroboxpushing;

import sim.app.horde.*;
import sim.app.horde.scenarios.heteroboxpushing.create.CreateAgent;
import sim.app.horde.scenarios.heteroboxpushing.pioneer.PioneerAgent;

import java.util.*;

public class BoxHitab extends Horde
    {
    private static final long       serialVersionUID        = 1L;

    String[]                                        pioneerIpAddress        = {  };                                         //{ "10.0.0.132" };
    String[]                                        createIpAddress         = { "10.0.0.123" };

    int                                                     CREATE_PORT                     = 7000;
    int                                                     PIONEER_PORT            = 6000;

    public ArrayList<Robot>         agents                          = new ArrayList<Robot>();

    public void setBasicBehaviorLocation(String s)
        {
        BASIC_BEHAVIORS_LOCATION = s;
        }

    public void setBasicTargetLocation(String s)
        {
        BASIC_TARGETS_LOCATION = s;
        }

    public void setBasicFeatureLocation(String s)
        {
        BASIC_FEATURES_LOCATION = s;
        }

    public void setTrainableMacroDirectory(String s)
        {
        TRAINABLE_MACRO_DIRECTORY = s;
        }

    public BoxHitab()
        {
        this(System.currentTimeMillis());
        }

    public BoxHitab(long seed)
        {
        super(seed);

        setBasicBehaviorLocation("scenarios/heteroboxpushing/heteroboxpushing.behaviors");
        setBasicTargetLocation("scenarios/heteroboxpushing/heteroboxpushing.targets");
        setBasicFeatureLocation("scenarios/heteroboxpushing/heteroboxpushing.features");
        setTrainableMacroDirectory(getPathInDirectory("scenarios/heteroboxpushing/trained/"));

        agents = new ArrayList<Robot>();

        for (int i = 0; i < pioneerIpAddress.length; i++)
            {
            PioneerAgent p = new PioneerAgent(this, pioneerIpAddress[i]);
            agents.add(p);
            if (i==0) 
                trainingAgent = p; 
            }

        for (int i = 0; i < createIpAddress.length; i++)
            {
            CreateAgent c = new CreateAgent(this, createIpAddress[i], CREATE_PORT);
            agents.add(c);
            if (i ==0 )
                trainingAgent = c; 
            }

        resetBehavior();

        }

    public void finish()
        {
        for (int i = 0; i < agents.size(); i++)
            agents.get(i).stop();
        }

    /*      public void distributeAndRestartBehaviors()
            {
            for (int i = 0; i < agents.size(); i++)
            {
            Robot agent = agents.get(i);

            // if I'm not the training agent, steal from him
            if (agent != trainingAgent &&  trainingAgent != null && trainingAgent.behavior != null && agent.getClass() == trainingAgent.getClass())
            agent.behavior = (TrainableMacro) (trainingAgent.behavior.clone());

            // now restart
            agent.restart(this);
            }
            }*/
    }
