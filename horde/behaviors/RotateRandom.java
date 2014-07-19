package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;

public class RotateRandom extends Behavior
    {
    private static final long serialVersionUID = 1;

    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public RotateRandom()
        {
        name = "RotateRandom";
        setKeyStroke('?');
        } 

    public void start(Agent agent, Macro parent, Horde horde)
        {
        super.start(agent, parent, horde);
        SimAgent simagent = (SimAgent) agent;
        simagent.setOrientation(horde.random.nextDouble() * Math.PI * 2);
        }
    }
