package sim.app.horde.scenarios.robocup2012;

import java.util.Scanner;
import sim.app.horde.scenarios.robocup2012.features.*;
import sim.app.horde.scenarios.robocup2012.wiimote.*;

public class HumanoidAgent extends sim.app.horde.Agent implements RCB4Constants
    {
    private static final long serialVersionUID = 1L;
    // Behaviors
    public static final byte BEHAVIOR_NOTHING = 0;
    public static final byte BEHAVIOR_SUCCEED = 1;
    public static final byte BEHAVIOR_FAIL = 2;

    public boolean doingEpsilonButtonActions = false;

    public static final int WII_MOTE_PORT = 8050;
    public static final String WII_MOTE_HOST = "localhost";

    public static final int HUMANOID_PORT = 8050;
    public static final String HUMANOID_HOST = "10.0.0.22";

    // game states
    public static final short GAME_INITIAL = 0;
    public static final short GAME_READY = 1;
    public static final short GAME_SET = 2;
    public static final short GAME_PLAYING = 3;
    public static final short GAME_FINISHED = 4;

    // game halves
    public static final short FIRST_HALF = 0;
    public static final short SECOND_HALF = 1;

    // camera byte settings
    public static final byte MASTER_CAMERA_C = 0;
    public static final byte SLAVE_CAMERA_C = 1;
    public static final byte UNSET_CAMERA_C = 2;

    // ACTIONS
    public static final byte BASIC_BEHAVIOR = -3;
    public static final byte BASIC_BEHAVIOR_BLOCKING = -2;
    public static final byte BUTTON_ACTION_EPSILON = -1;
    public static final byte BUTTON_ACTION = 0;

    // to synchronize the variables below
    Object[] lock = new Object[0];

    Scanner scanner;
    public HumanoidData humanoidData = new HumanoidData();
    HumanoidData tmpHumanoidData = new HumanoidData();

    public RobocupObject ball, blueGoal, yellowGoal;

    WiiMote wiiMote;
    public Humanoid humanoid;

    int STOP, TURN_LEFT, TURN_RIGHT, STEP_LEFT, STEP_RIGHT, FORWARD, STEP_FORWARD;
    int KICK_LEFT, KICK_RIGHT, CALIBRATE;

    public HumanoidAgent(HumanoidHorde horde) {
        super(horde);

        //wiiMote = new WiiMote(WII_MOTE_HOST, WII_MOTE_PORT);

        humanoid = new Humanoid(HUMANOID_HOST, HUMANOID_PORT);
        ball = new Ball();
        blueGoal = new BlueGoal();
        yellowGoal = new YellowGoal();

        }

    boolean setup = false;

    public void setupVariables()
        {
        if (!setup)
            {
            STOP = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.Stop.class);

            // these are the continuous motions 
            FORWARD = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.Forward.class);
            TURN_LEFT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.TurnLeft.class);
            TURN_RIGHT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.TurnRight.class);
            STEP_LEFT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.StepLeft.class);
            STEP_RIGHT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.StepRight.class);

            // these are one and done motions 
            STEP_FORWARD = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.ForwardOnce.class);
            KICK_LEFT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.KickLeft.class);
            KICK_RIGHT = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.KickRight.class);
            CALIBRATE = indexOfBehavior(sim.app.horde.scenarios.robocup2012.behaviors.CalibrateHome.class);
            }
        setup = true;
        }

    // gracefully shutdown 
    public void stop()
        {
        //wiiMote.stop(); 
        humanoid.stop();
        }

    public void go()
        {
        setupVariables();

        // get latest data from robot 
        humanoidData = humanoid.getCurrentData();
//        System.err.println(humanoidData);

        // update the graphics 
        HumanoidHorde hHorde = (HumanoidHorde) horde;
        ball.update(hHorde, humanoidData); 
        blueGoal.update(hHorde, humanoidData); 
        yellowGoal.update(hHorde, humanoidData); 
                                
        
        /*
        // get latest input from the Wiimote
        WiiMoteData wd = wiiMote.getCurrentData();

        // enter or leave training mode based on Wiimote button press
        if (wd.currentlyTraining && !horde.getTrainingMacro().isTraining())
        {
        horde.getTrainingMacro().userChangedTraining(horde, true);
        System.err.println("TRAINING"); 
        }
        else if (!wd.currentlyTraining && horde.getTrainingMacro().isTraining())
        {
        horde.getTrainingMacro().userChangedTraining(horde, false);
        System.err.println("DONE TRAINING"); 
        }
                
        // switch behavior based on wiimote data and button press
        if (wd.currentlyTraining)
        {
        switch(wd.currentButtonPressed) 
        {
        case FORWARD_BUTTON: 
        horde.getTrainingMacro().userChangedBehavior(horde, FORWARD); 
        break; 
        case LEFT_TURN_BUTTON: 
        horde.getTrainingMacro().userChangedBehavior(horde, TURN_LEFT); 
        break; 
        case RIGHT_TURN_BUTTON: 
        horde.getTrainingMacro().userChangedBehavior(horde, TURN_RIGHT); 
        break; 
        case RIGHT_STEP_BUTTON: 
        horde.getTrainingMacro().userChangedBehavior(horde, STEP_RIGHT); 
        break;
        case LEFT_STEP_BUTTON: 
        horde.getTrainingMacro().userChangedBehavior(horde, STEP_LEFT); 
        break;
        case RCB4_MOT_LEFT_KICK: 
        horde.getTrainingMacro().userChangedBehavior(horde, KICK_LEFT); 
        break; 
        case RCB4_MOT_RIGHT_KICK: 
        horde.getTrainingMacro().userChangedBehavior(horde, KICK_RIGHT); 
        break; 
        case RCB4_MOT_CALIBRATE: 
        horde.getTrainingMacro().userChangedBehavior(horde, CALIBRATE); 
        break; 
        case 0:   // can't use RCB4_MOT_HOME since RCB4_MOT_HOME = FORWARD_BUTTON 
        horde.getTrainingMacro().userChangedBehavior(horde, STOP); 
        break; 
        }
        }
                
        */
        super.go();
        }


    }
