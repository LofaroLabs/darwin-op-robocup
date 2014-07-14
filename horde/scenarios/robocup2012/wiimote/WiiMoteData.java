package sim.app.horde.scenarios.robocup2012.wiimote;

import java.util.Scanner;

public class WiiMoteData
    {

    public boolean currentlyTraining;
    public int currentButtonPressed;

    public void readData(Scanner scanner)
        {
        String line = scanner.nextLine();
        String[] arr = line.split(":");
        currentlyTraining = true;
        if (Integer.valueOf(arr[0]) == 0)
            currentlyTraining = false;
        currentButtonPressed = Integer.valueOf(arr[1]);

        }
    }