package sim.app.horde.scenarios.robocup2011;

import java.io.*;
import sim.app.horde.*; 
import sim.engine.*;
import sim.app.horde.behaviors.*;

public class HumanoidHorde extends Horde implements MacroObserver {

    private static final long serialVersionUID = 1L;
                
    public void setBasicBehaviorLocation(String s) { BASIC_BEHAVIORS_LOCATION = s; } 
    public void setBasicTargetLocation(String s) { BASIC_TARGETS_LOCATION = s; } 
    public void setBasicFeatureLocation(String s) { BASIC_FEATURES_LOCATION = s; } 
    public void setTrainableMacroDirectory(String s) { TRAINABLE_MACRO_DIRECTORY = s;} 

        
    public HumanoidHorde() { this(System.currentTimeMillis()); }
        
    public HumanoidHorde(long millis) {
        super(millis);
                
        setBasicBehaviorLocation("scenarios/robocup2011/humanoid.behaviors"); 
        setBasicTargetLocation("scenarios/robocup2011/humanoid.targets"); 
        setBasicFeatureLocation("scenarios/robocup2011/humanoid.features"); 
        setTrainableMacroDirectory("/tmp/trained/");
                
        trainingAgent = new HumanoidAgent(this); 
        }

    public void transitioned(Macro macro, int from, int to) {} 

    public static String getPathInDirectory(String s) {
        File f = new File(HumanoidHorde.class.getResource("").getPath() + "/" + s +"/"); 
        //if (!f.isDirectory()) { // create directory since it doesn't exist 
        //      f.mkdirs(); 
        //      System.out.println("Created directory: " + f.getPath()); 
        //}
        return f.getPath() + File.separatorChar;                
        }
                
    public void start()
        {
        schedule.scheduleRepeating(new Steppable() { public void step(SimState state) { trainingAgent.go(); } });
        }
    }
