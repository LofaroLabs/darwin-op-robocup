package sim.app.horde.scenarios.forage;

import sim.app.horde.scenarios.forage.hardcoded.CodedHorde;
import sim.engine.*;

import java.io.*; 

public class DataCollection implements Steppable, Stoppable 
    {

    private static final long serialVersionUID = 1L;
        
    PrintWriter outFile; 
    public Stoppable stoppable; 

    public DataCollection(SimState state, String fileName, int[] agents)
        {
        try { 
            for (int i=0; i < agents.length; i++) 
                fileName += agents[i] + "-"; 
            fileName +=  state.job() + ".dat";
            outFile = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
            } catch (IOException e) {
            e.printStackTrace();
            }
        }

    public void stop()
        {
        outFile.close(); 
        }

    public void step(SimState state)
        {
        outFile.println(state.schedule.getSteps() + " \t" + CodedHorde.collectedBoxes); 
        outFile.flush(); 
        }
        
    }
