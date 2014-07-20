package sim.app.horde.scenarios.heteroboxpushing.features.pioneer;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BlobData implements Cloneable
    {
    public static final int NUM_COLORS      = 4;
    public int                              xMin[]          = new int[NUM_COLORS];
    public int                              xMax[]          = new int[NUM_COLORS];
    public int                              yMin[]          = new int[NUM_COLORS];
    public int                              yMax[]          = new int[NUM_COLORS];

    public BlobData()
        {
        for (int i = 0; i < NUM_COLORS; i++)
            xMin[i] = xMax[i] = yMin[i] = yMax[i] = -1;
        }

    public Object clone()
        {
        BlobData bd = new BlobData();
        System.arraycopy(xMin, 0, bd.xMin, 0, xMin.length);
        System.arraycopy(yMin, 0, bd.yMin, 0, yMin.length);
        System.arraycopy(xMax, 0, bd.xMax, 0, xMax.length);
        System.arraycopy(yMax, 0, bd.yMax, 0, yMax.length);

        return bd;
        }

    public void readData(Scanner scanner) throws IOException
        {
        String line = scanner.nextLine();

        StringTokenizer st = new StringTokenizer(line, ":");

        System.out.println("CAMERA: " + line); 
                
        for (int i = 0; i < NUM_COLORS; i++)
            {
            xMin[i] = Integer.valueOf(st.nextToken());
            yMin[i] = Integer.valueOf(st.nextToken());
            xMax[i] = Integer.valueOf(st.nextToken());
            yMax[i] = Integer.valueOf(st.nextToken());
            }
        }
    }
