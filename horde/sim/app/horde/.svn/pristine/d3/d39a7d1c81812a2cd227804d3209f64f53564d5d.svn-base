/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin;

import sim.app.horde.SimHordeWithUI;
import sim.display.Controller;
import sim.engine.SimState;

/**
 *
 * @author drew
 */
public class DarwinHordeWithUI extends SimHordeWithUI{
    private static final long serialVersionUID = 1;

    public DarwinHordeWithUI(SimState state) {
        super(state);
        }

        
    @Override
    public void setupPortrayals()
        {
        setupAgentsAndPlacesPortrayals();
        }

    @Override
    public void init(Controller c) {
        super.init(c); //To change body of generated methods, choose Tools | Templates.
        /*
        agentsPortrayal.flipY = true;
        obstaclesPortrayal.flipY = true;
        regionsPortrayal.flipY = true;
        placesPortrayal.flipY = true;
        */
    }

        

    
        

    public static void main(String[] args)
        {
        new DarwinHordeWithUI(new DarwinHorde(System.currentTimeMillis())).createController();
        }
    
    
    }
