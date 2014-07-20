package sim.app.horde.scenarios.boxpushing;

import java.io.IOException;
import java.util.StringTokenizer;

public class PioneerData implements Cloneable
{
	public double angle;
	public double distance;

	public Object clone()
	{
		PioneerData pd = new PioneerData(); 
		pd.angle = angle; 
		pd.distance = distance; 
		return pd; 
	}
	
	
	public void readData(String line) throws IOException
	{
		StringTokenizer st = new StringTokenizer(line, ":");
		angle = Double.valueOf(st.nextToken());
		distance = Double.valueOf(st.nextToken());
	}
}