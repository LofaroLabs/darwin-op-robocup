package sim.app.horde.behaviors;
import sim.app.horde.*;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;

public class Rotate extends Behavior
    {

    private static final long serialVersionUID = 1;
    public double speed;

    // I am designed for sim agents
    public static String getType() { return sim.app.horde.agent.SimAgent.TYPE_SIMAGENT; }

    public Rotate()
        {
        this(2);
        } // good default

    public Rotate(double speed)
        {
        this.speed = speed;
        name = "Rotate[" + speed + "]";
        setKeyStroke(KS_RIGHT);
        }

    public void go(Agent agent, Macro parent, Horde horde)
        {
        super.go(agent, parent, horde);
                
        SimAgent simagent = (SimAgent) agent;
//        double a = simagent.getOrientation();
        simagent.setOrientation(simagent.getOrientation() + speed / 360.0 * Math.PI * 2);
//        System.err.println("New: " + simagent.getOrientation() + " vs Old " + a);
        }
    }
