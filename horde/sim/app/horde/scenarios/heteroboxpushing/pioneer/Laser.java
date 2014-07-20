package sim.app.horde.scenarios.heteroboxpushing.pioneer;

import sim.app.horde.scenarios.heteroboxpushing.features.pioneer.LaserData;

public class Laser extends PioneerSensor
    {
    LaserData       currentLaserData        = new LaserData();

    public Laser(String address, int port)
        {
        super(address, port);
        }

    public void readData() throws Exception
        {
        LaserData ld = new LaserData();
        ld.readData(scanner);
        synchronized (sensorLock)
            {
            currentLaserData = ld;
            }
        }

    public LaserData getCurrentData()
        {
        LaserData ld = new LaserData(); 
        synchronized (sensorLock)
            {
            ld = (LaserData)currentLaserData.clone();
            }
        return ld;
        }
    }
