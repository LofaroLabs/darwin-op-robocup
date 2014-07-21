package sim.app.horde.scenarios.forage.agent;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.AgentGroup;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.agent.SimControllerAgent;
import sim.app.horde.behaviors.Macro;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;

public class Supervisor extends SimControllerAgent
	{
	private static final long serialVersionUID = 1L;
	
	//private static final String[] types = {"Global", "Controller", "Supervisor", "Supervisor-L1"};
	private static final String[] types = {"Global", "Controller", "Supervisor", "Supervisor-L2"};
	public SimAgent[] agentStatus = new SimAgent[Forager.NUM_STATUSES];
	public int[] agentStatusCount = new int[Forager.NUM_STATUSES];

	public Agent doneAgent = null;
	public int doneCount = 0;
	public int prevDoneCount = 0;
	protected boolean coded = false;

	// The box manipulated by a forager with the highest attachment requirements
	private int biggestBox = -1;

	// The (first) forager that has manipulated the biggest box
	private Forager attachedToBiggestBox;

	public Supervisor()
	{
	super();

	for (int i = 0; i < Forager.NUM_STATUSES; i++)
		agentStatus[i] = null;
	}

	public SimAgent getAgentStatus(int status)
		{
		return agentStatus[getIndex(status)];
		}

	int getIndex(int value)
		{
		return (value == 0) ? 0 : (int) (Math.log10(value) / Math.log10(2));
		}

	public void step(SimState state)
		{
		super.step(state);

		// Going to recompute aggregate status info
		for (int i = 0; i < Forager.NUM_STATUSES; i++)
			{
			agentStatus[i] = null;
			agentStatusCount[i] = 0;
			}

		doneAgent = null;
		doneCount = 0;

		int tmpBox = biggestBox;
		Forager tmpAgent = attachedToBiggestBox;

		// resetBiggest();

		for (AgentGroup ag : getSubsidiaryAgents())
			{
			// Get the biggest box for all children
			for (int i = 0; i < ag.getAgents().size(); i++)
				{
				SimAgent a = (SimAgent) ag.getAgent(i);

				// Supervisor is done if any subsidiary agent is done?
				/* In this scenario if a Forager is attached to a box, and then suddenly isn't, it goes to DONE even if it hasn't reached Home.
				 * This is likely due to how Help was trained, but the net is that Help is still running, but there is no BiggestAttached any more. */
				//if (a.getFinished())
					{
//					setFinished(true);
//					((Macro) getBehavior()).setFlag(Macro.FLAG_DONE, true);
					}

				// update agent status count
				int idx = getIndex(a.getStatus());
				agentStatusCount[idx]++;
				if (agentStatus[idx] == null) agentStatus[idx] = a;

				// load done
				if (((Macro) (a.getBehavior())).getFlag(Macro.FLAG_DONE))
					{
					doneCount++;
					doneAgent = a;
					//System.out.println(state.schedule.getTime() + " - Done Count = " + doneCount);
					}

				if (a instanceof Supervisor)
					{
					Supervisor s = (Supervisor) a;

					// child knows of a bigger box. If the child needs help with the
					// box, then update the biggest box for this controller
					if (s.biggestBox() > biggestBox)
						{
						
						// child is a supervisor and has a box with required
						// attachments more than it can handle. Need help from
						// above
						// (i.e. me).
						if (s.biggestBox() > s.agentCount("Forager"))
							{
							biggestBox = s.biggestBox();
							attachedToBiggestBox = s.attachedToBiggestBox();
							}
						}
					}
				// child is a basic agent, so definitely needs help from
				// above
				else
					{
					Forager f = (Forager) a;

					if (f.biggestBox() > biggestBox)
						{
						biggestBox = f.biggestBox();
						attachedToBiggestBox = (Forager) a;
						}
					}
				}
			}
		
		//if (doneCount == 0 && prevDoneCount > 0)
		//	System.out.println(state.schedule.getTime() + " - I was done, but now I'm not");
		
		prevDoneCount = doneCount;

		// my parent (or is it me) knows of a bigger box than my children
		if (tmpBox > biggestBox)
			{
			biggestBox = tmpBox;
			attachedToBiggestBox = tmpAgent;
			restart(getHorde());  // Bill: Wonder what this is for??
			}

		// I have someone attached to a box, p
		if (biggestBox > -1) updateBiggest();
		}

	/*
	 * Push biggest box info down the controller hierarchy
	 */
	void updateBiggest()
		{
		for (AgentGroup ag : getSubsidiaryAgents())
			for (int i = 0; i < ag.getAgents().size(); i++)
				{
				Agent f = (Agent) ag.getAgent(i);

				// pass the biggest down the hierarchy
				if (f instanceof Supervisor)
					{
					((Supervisor) f).setBiggestBox(biggestBox);
					((Supervisor) f).setAttachedToBiggestBox(attachedToBiggestBox);
					((Supervisor) f).updateBiggest();
					}
				else if (attachedToBiggestBox != f && ((Forager)f).biggestBox() < biggestBox)
					{
					// some other forager needs help with a bigger box, drop this and help him
					((Forager) f).setAttachToBox(false);
					}
				}
		}

	/*public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
		{
		driftToPose(meanAgentLoc(), getOrientation());  // why are we setting the location here?  Shouldn't this be done higher up in sopme more official capacity? -- Sean
		
		if (attachedToBiggestBox != null)
			{
			Double2D l = attachedToBiggestBox.getLocation();
			double dx = l.x - getLocation().x;
			double dy = l.y - getLocation().y;
			int x2 = (int) (info.draw.x + info.draw.width * dx);
			int y2 = (int) (info.draw.y + info.draw.height * dy);
			//graphics.drawLine((int) info.draw.x, (int) info.draw.y, x2, y2);
			}
		super.draw(object, graphics, info);
		}*/

	/**
	 * Reset biggest manipulated body (box) information for this agent and any
	 * parents due to this agent no longer manipulating a body (box).
	 */
	public void resetBiggest(Forager f)
		{
		Supervisor ctrl = (Supervisor) getGroup().getController();

		// Reset the parent if: 1) There is a controller and it has a biggest
		// attached agent and 2) The biggest box of the controller is not
		// larger than my biggest box (meaning it got it's biggest box info
		// from another forager)
		if (ctrl != null && ctrl.attachedToBiggestBox != null
				&& (biggestBox > 0 && biggestBox >= ctrl.biggestBox))

			ctrl.resetBiggest(f);

		if (attachedToBiggestBox != null && attachedToBiggestBox == f)
			{
			biggestBox = -1;
			attachedToBiggestBox = null;
			updateBiggest();
			}
		}

	public Forager attachedAgent()
		{
		Supervisor ctrl = (Supervisor)getGroup().getController();

		if (ctrl == null) // i am the root
			return attachedToBiggestBox;

		Forager a = ctrl.attachedAgent();

		if (a == null) return attachedToBiggestBox;

		return a;
		}

	public int biggestBox()
		{
		return biggestBox;
		}

	public void setBiggestBox(int biggest)
		{
		biggestBox = biggest;
		}

	public Forager attachedToBiggestBox()
		{
		return attachedToBiggestBox;
		}

	public void setAttachedToBiggestBox(Forager f)
		{
		attachedToBiggestBox = f;
		}
	}
