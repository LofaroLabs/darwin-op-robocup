package sim.app.horde.scenarios.pioneer.features;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BlobData implements Cloneable
    {
    public static final int NUM_COLORS = 4; 
    public int xMin[] = new int[NUM_COLORS]; 
    public int xMax[] = new int[NUM_COLORS]; 
    public int yMin[] = new int[NUM_COLORS]; 
    public int yMax[] = new int[NUM_COLORS]; 

    public BlobData()
        {
        for (int i=0; i < NUM_COLORS; i++)
            xMin[i] = xMax[i] = yMin[i] = yMax[i] = -1; 
        }
        
    public Object clone()
        {
        return new BlobData(this);
        }
        
    public BlobData(BlobData bd) 
        {
        System.arraycopy(bd.xMin, 0, xMin, 0, bd.xMin.length); 
        System.arraycopy(bd.yMin, 0, yMin, 0, bd.yMin.length); 
        System.arraycopy(bd.xMax, 0, xMax, 0, bd.xMax.length); 
        System.arraycopy(bd.yMax, 0, yMax, 0, bd.yMax.length); 
        }
        
        
    public void readData(Scanner scanner) throws IOException
        {
        String line = scanner.nextLine();
                
        StringTokenizer st = new StringTokenizer(line, ":");

        for (int i=0; i < NUM_COLORS; i++) { 
            xMin[i] = Integer.valueOf(st.nextToken());
            yMin[i] = Integer.valueOf(st.nextToken());
            xMax[i] = Integer.valueOf(st.nextToken());
            yMax[i] = Integer.valueOf(st.nextToken());
            }

        }
    }
