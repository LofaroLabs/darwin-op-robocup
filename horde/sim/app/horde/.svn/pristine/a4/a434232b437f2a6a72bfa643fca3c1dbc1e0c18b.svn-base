package sim.app.horde.agent;

/**
 * This class is an enum with two methods that get the computed requirements for
 * controllers and agent groups respectively. The underlying computation method
 * is selected according to the current enum value. By passing this as an
 * argument into dispenseAgents methods, the dispenseAgents methods do not need
 * to be aware of the mode for which the controller hierarchy is being built
 * 
 */

public class HierarchyConstraints
	{
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int PREFERRED = 2;
	public static final String[] modes =
		{ "Min", "Max", "Preferred" };

	public int mode = MIN;

	public String toString()
		{
		return modes[mode];
		}

	public int getComputedReqts(Controller c, int basicTypeIndex)
		{
		switch (mode)
			{
			case MIN:
				return c.getComputedMinAgents(basicTypeIndex);
			case MAX:
				return c.getComputedMaxAgents(basicTypeIndex);
			case PREFERRED:
				return c.getComputedPreferredAgents(basicTypeIndex);
			default:
				return 0;
			}
		}

	public int getComputedReqts(AgentGroup g, int basicTypeIndex)
		{
		switch (mode)
			{
			case MIN:
				return g.getComputedMinAgents(basicTypeIndex);
			case MAX:
				return g.getComputedMaxAgents(basicTypeIndex);
			case PREFERRED:
				return g.getComputedPreferredAgents(basicTypeIndex);
			default:
				return 0;
			}
		}
	}
