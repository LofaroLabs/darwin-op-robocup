package sim.app.horde.scenarios.heteroboxpushing.features.pioneer;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class LaserData implements Cloneable
    {
    double angleLongestLine; 
    double distanceToLongestLine; 

    public LaserData() {
                
        }

    public Object clone()
        {
        LaserData ld = new LaserData(); 
        ld.angleLongestLine = angleLongestLine; 
        ld.distanceToLongestLine = distanceToLongestLine; 
                
        return ld; 
        }

    public void readData(Scanner scanner) throws IOException
        {
        String line = scanner.nextLine();
                
        System.out.println("LASER: " + line); 
                
        StringTokenizer st = new StringTokenizer(line, ":");

        angleLongestLine = Double.valueOf(st.nextToken()); 
        distanceToLongestLine = Double.valueOf(st.nextToken()); 
        }

    }