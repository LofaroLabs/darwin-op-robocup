package sim.app.horde.classifiers.obliquedecisiontree;

import ec.util.*;
import java.io.*;
import java.util.*;

public class Generator
    {

    public static void main(String[] args)
        {
        Generator g = new Generator();
        try
            {
            g.run();
            } catch (Exception e)
            {
            e.printStackTrace();
            }
        }

    void run() throws Exception
        {
        MersenneTwisterFast random = new MersenneTwisterFast();

        double maxX = 130;
        double maxY = 30;
        int NUM_LINES = 2;

        double xDelta = maxX / (NUM_LINES + 1);
        double yDelta = maxY / (NUM_LINES + 1);

        double x1 = xDelta;
        double y1 = x1;
        double x2 = 2 * xDelta;
        double y2 = x2;

        System.out.println(x1 + " " + y1);
        System.out.println(x2 + " " + y2);

        int NUM_POINTS = 100;
        PrintWriter outFile = new PrintWriter(new FileWriter(new File("data.examples")));

        ArrayList<String> left = new ArrayList<String>();
        ArrayList<String> right = new ArrayList<String>();
        ArrayList<String> forward = new ArrayList<String>();

        for (int i = 0; i < NUM_POINTS; i++)
            {
            double x, y;

            x = random.nextDouble() * maxX;
            y = random.nextDouble() * maxY;

            // Assuming the points are (Ax,Ay) (Bx,By) and (Cx,Cy), you need to compute:
            // (Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)

            String action;

            // below first line?
            //if (((0 - x1) * (y - 0) - (y1 - 0) * (x - x1)) > 0)
            //      action = "Forward[0.1]";
            if (((0 - x2) * (y - 0) - (y2 - 0) * (x - x2)) > 0)
                action = "Rotate[-2.0]";
            else
                action = "Rotate[2.0]";

            String str = "+ " + x + " " + y + " " + action;

            if (action.contains("Forward"))
                forward.add(str);
            else if (action.contains("Rotate[2.0]"))
                right.add(str);
            else
                left.add(str);
            }

        for (int i = 0; i < forward.size(); i++)
            outFile.println(forward.get(i));
        outFile.println();
        outFile.println();
        for (int i = 0; i < left.size(); i++)
            outFile.println(left.get(i));
        outFile.println();
        outFile.println();

        for (int i = 0; i < right.size(); i++)
            outFile.println(right.get(i));

        outFile.close();
        }

    }
