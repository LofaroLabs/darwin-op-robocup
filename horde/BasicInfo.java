package sim.app.horde;

/**
 * Basics are allocated to a controller in the arena file. However, this
 * information won't be used until the hierarchy is built, so BasicInfo just
 * allows this information to be saved for later. Using this class makes it easy
 * to add new constraint or other information relevant to basics in the future.
 * 
 */

public class BasicInfo
	{
	String basicType;
	int numBasics;
	public double xPos;
	public double yPos;
	public double height;
	public double width;

	public int getNumBasics()
		{
		return numBasics;
		}
	}
