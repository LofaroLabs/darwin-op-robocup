package sim.app.horde.scenarios.robocup2012;

import java.io.*;
import sim.app.horde.*;
import sim.engine.*;
import sim.field.continuous.*;
import sim.app.horde.behaviors.*;
import sim.field.grid.*;

public class HumanoidHorde extends Horde implements MacroObserver
    {

    private static final long serialVersionUID = 1L;

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

    public Continuous2D ballField = new Continuous2D(10, 640, 480);
    public Continuous2D ballLocation = new Continuous2D(2000, 2000, 2000);
    public DoubleGrid2D ballGrid = new DoubleGrid2D(200, 200);  // dummy grid, don't touch -- Sean
    public Continuous2D yellowField = new Continuous2D(10, 640, 480);
    public Continuous2D yellowGoalLocation = new Continuous2D(2000, 2000, 2000);
    public Continuous2D blueField = new Continuous2D(10, 640, 480);
    public Continuous2D blueGoalLocation = new Continuous2D(2000, 2000, 2000);

    public HumanoidHorde() {
        this(System.currentTimeMillis());
        }

    public HumanoidHorde(long millis) 
        {
        super(millis);

        setBasicBehaviorLocation("scenarios/robocup2012/humanoid.behaviors");
        setBasicTargetLocation("scenarios/robocup2012/humanoid.targets");
        setBasicFeatureLocation("scenarios/robocup2012/humanoid.features");
        setTrainableMacroDirectory(getPathInDirectory("/trained/"));

        trainingAgent = new HumanoidAgent(this);                
        }

    public void transitioned(Macro macro, int from, int to)
        {
        }

    public static String getPathInDirectory(String s)
        {
        File f = new File(HumanoidHorde.class.getResource("").getPath() + "/" + s + "/");
        return f.getPath() + File.separatorChar;
        }

    boolean humanoidRunning = false; 
    public void start()
        {
        humanoidRunning = true; 
                
        schedule.scheduleRepeating(new Steppable()
            {
            private static final long serialVersionUID = 1L;

            public void step(SimState state)
                {
                trainingAgent.go();
                }
            });
        }
        
    public void finish() 
        {
        if (humanoidRunning) 
            ((HumanoidAgent)trainingAgent).stop(); 
        }
    }
