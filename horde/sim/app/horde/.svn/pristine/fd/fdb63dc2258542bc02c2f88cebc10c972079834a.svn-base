package sim.app.horde.scenarios.heteroboxpushing.pioneer;

public class Sonar
    {
    public final static int NUM_SONARS = 16;

    // angles of sonars on pioneer in degrees
    double sonarAngles[] = new double[NUM_SONARS];
    
    // values of each sonar 
    double sonars[] = new double[NUM_SONARS];

    public double closestObstacleDistance, closestObstacleAngle;
    public double closestFrontDistance, closestFrontAngle;
    public double closestRearDistance;

        
    public Sonar() 
        {
        sonars = new double[NUM_SONARS]; 
        for (int i=0; i < NUM_SONARS; i++) 
            sonars[i] = 0; 
                
        sonarAngles[0] = Math.PI;
        sonarAngles[7] = 0;
        sonarAngles[8] = 0;
        sonarAngles[15] = Math.PI;
        for (int i = 1; i <= 6; i++)
            sonarAngles[i] = Math.PI - Math.PI / 9 * (i + 1);

        for (int i = 9; i <= 14; i++)
            sonarAngles[i] = 2 * Math.PI - (Math.PI / 9 * (i - 8));
        }
        
        
    public void update(double sonar[])
        {
        sonars = sonar;

        closestObstacleDistance = 100000;
        closestObstacleAngle = 0;

        closestFrontDistance = 100000;
        closestFrontAngle = 0;

        closestRearDistance = 100000;

        for (int i = 0; i < NUM_SONARS; i++)
            {
            if (sonars[i] < closestObstacleDistance)
                {
                closestObstacleDistance = sonars[i];
                closestObstacleAngle = sonarAngles[i];
                }

            if (i < 8 && sonars[i] < closestFrontDistance)
                {
                closestFrontDistance = sonars[i];
                closestFrontAngle = sonarAngles[i];
                }

            if (i >= 8 && sonars[i] < closestRearDistance)
                closestRearDistance = sonars[i];

            }
        }
        
    public double[] getSonarValues() 
        {
        return sonars; 
        }
        
    public double getClosestObstacleDist()
        {
        return closestObstacleDistance; 
        }
        
    public double getClosestObstacleAngle()
        {
        return closestObstacleAngle; 
        }

    }
