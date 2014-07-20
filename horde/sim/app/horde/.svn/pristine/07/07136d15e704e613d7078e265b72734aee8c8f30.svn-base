package sim.app.horde.scenarios.forage.hardcoded.behaviors;

import sim.app.horde.agent.Agent;
import sim.app.horde.behaviors.*;
import sim.app.horde.scenarios.forage.hardcoded.*;
import sim.app.horde.*; 

public class Hunt extends CodedBehavior
    {
    private static final long serialVersionUID = 1L;

    int CAN_SEE_BOX = 0; 
    int GOTO_BOX, WANDER; 
        
    static InterestingFeature[] interestedFeatures = {
        new InterestingFeature(sim.app.horde.scenarios.forage.features.CanSeeBox.class, null) };
        
    public Hunt() 
        { 
        super(CodedHorde.initFeatures(interestedFeatures), new Behavior[] { new GotoBox(), new Wander() } ); 
        name = "Hunt"; 
                
        GOTO_BOX = indexOfBehaviorNamed("GotoBox"); 
        WANDER = indexOfBehaviorNamed("Wander");
        } 
        
    public int getNewBehavior(Agent agent, Horde horde, double[] currentFeatureVector)
        {
        if (currentFeatureVector[CAN_SEE_BOX] > 0)
            return GOTO_BOX; 
                
        return WANDER; 
        }
        
    }
