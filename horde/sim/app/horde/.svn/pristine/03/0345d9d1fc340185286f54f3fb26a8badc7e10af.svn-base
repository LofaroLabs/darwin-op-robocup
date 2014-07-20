package sim.app.horde.scenarios.heteroboxpushing.pioneer;

import sim.app.horde.scenarios.heteroboxpushing.features.pioneer.BlobData;

public class Camera extends PioneerSensor
    {
    BlobData        currentBlobData = new BlobData();

    public Camera(String ipAddress, int port)
        {
        super(ipAddress, port);
        }

    public void readData() throws Exception
        {
        BlobData ld = new BlobData();
        ld.readData(scanner);
        synchronized (sensorLock)
            {
            currentBlobData = (BlobData)ld.clone();
            }
        }

    public BlobData getBlobData()
        {
        BlobData bd = new BlobData();
        synchronized (sensorLock)
            {
            bd = (BlobData) currentBlobData.clone();
            }
        return bd;
        }

    }
