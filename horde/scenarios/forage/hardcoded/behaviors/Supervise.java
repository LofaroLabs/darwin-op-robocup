package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import java.util.List;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Done;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.behaviors.Start;
import sim.app.horde.scenarios.forage.agent.Forager;
import sim.app.horde.scenarios.forage.hardcoded.CodedAgent;
import sim.app.horde.scenarios.forage.hardcoded.CodedBehavior;
import sim.app.horde.scenarios.forage.hardcoded.CodedHorde;
import sim.app.horde.scenarios.forage.hardcoded.CodedSupervisor;
import sim.app.horde.scenarios.forage.hardcoded.InterestingFeature;

public class Supervise extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    int SOMEONE_ATTACHED = 1;
    int SOMEONE_DONE = 2;
    int SOMEONE_FINISHED = 0; 
    int SOMEONE_NEEDS_HELP = 3;

    static InterestingFeature[] interestedFeatures = {
        new InterestingFeature(sim.app.horde.scenarios.forage.hardcoded.features.SomeoneFinished.class, null),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.SomeoneAttached.class, null),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.SomeoneDone.class, null),
        new InterestingFeature(sim.app.horde.scenarios.forage.features.SomeoneNeedsHelp.class, null) };

    public Supervise()
        {
        super(CodedHorde.initFeatures(interestedFeatures), new Behavior[] { new Start(), new Done() });
        name = "Supervise";
        }

    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        if (currentFeatureVector[SOMEONE_FINISHED] > 0) {
            updateSubsidiaryAgents(agent, parent, horde, CodedAgent.RELEASE_NO_FLAG);
            reset(agent);
            finished = true;
            }
        else if (currentFeatureVector[SOMEONE_ATTACHED] > 0 || currentFeatureVector[SOMEONE_NEEDS_HELP] > 0)
            updateSubsidiaryAgents(agent, parent, horde, CodedAgent.GRAB_BOX);
        else
            updateSubsidiaryAgents(agent, parent, horde, CodedAgent.FORAGE);

        return 0;
        }

    void reset(Agent agent)
        {
        CodedSupervisor cAgent = (CodedSupervisor) agent;

        List<Agent> foragers = cAgent.getSubsidiaryAgents("Forager");
        for (int i = 0; i < foragers.size(); i++) {
            Forager a = (Forager) (foragers.get(i));
            a.resetBiggest(); 
            }
        }

    void updateSubsidiaryAgents(Agent agent, Macro parent, Horde horde, int behaviorIndex)
        {
        CodedSupervisor cAgent = (CodedSupervisor) agent;
        List<Agent> foragers = cAgent.getSubsidiaryAgents("Forager");

       for (int i = 0; i < foragers.size(); i++) {
            SimAgent a = (SimAgent) (foragers.get(i));
            CodedBehavior b = (CodedBehavior) (a.getBehavior());
            b.performTransition(behaviorIndex, a, horde);
            b.finished = false;
            }
        }

    }
