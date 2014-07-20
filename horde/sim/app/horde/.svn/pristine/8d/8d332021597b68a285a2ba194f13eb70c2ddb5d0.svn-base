package sim.app.horde.scenarios.pioneer.vision;

import java.io.*;
import java.util.*; 


public class BlobData
    {
    //public int blobSize; 
    // public int xCoord, yCoord; 
    public int xMin, xMax, yMin, yMax;
        
    public void readData(Scanner scanner) throws IOException
        {           
        //blobSize = scanner.nextInt();
        String line = scanner.nextLine(); 
                                
        StringTokenizer st = new StringTokenizer(line,":"); 

        xMin =  Integer.valueOf(st.nextToken());
        yMin =  Integer.valueOf(st.nextToken());
        xMax =  Integer.valueOf(st.nextToken());
        yMax =  Integer.valueOf(st.nextToken());
        
        //blobSize = Integer.valueOf(st.nextToken()); 
        //      xCoord = Integer.valueOf(st.nextToken()); 
        //yCoord = Integer.valueOf(st.nextToken());
        }

    public String toString() 
        {
        return "BBOX: (" + xMin + ", " + yMin +") (" +xMax + ", " + yMax +")"; 
        }
    }