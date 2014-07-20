package sim.app.horde.scenarios.forage.hardcoded;

import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.features.Feature;
import sim.app.horde.transitions.Transition;

public class CodedBehavior extends Macro
    {
    private static final long serialVersionUID = 1L;

    public CodedBehavior(Feature[] features, Behavior[] b)
        {
        super();
        name = "Hard Coded Behavior";
        setKeyStroke('c');
        behaviors = b;
        transitions = new Transition[behaviors.length];

        for (int i = 0; i < behaviors.length; i++)
            transitions[i] = new CodedTransition(features);
        }

    public boolean shouldAddDefaultExample()
        {
        return false;
        }

    public boolean shouldAddExamples()
        {
        return false;
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        return currentBehavior;
        }
    }
