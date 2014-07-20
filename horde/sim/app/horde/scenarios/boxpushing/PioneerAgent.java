package sim.app.horde.scenarios.boxpushing;

import java.net.Socket;
import java.util.Scanner;

import sim.app.horde.*;

// sean stuff
import java.awt.*;
import java.awt.geom.*;
import sim.display.*;
import sim.portrayal.*;
import sim.portrayal.simple.*;

public class PioneerAgent extends sim.app.horde.agent.SimAgent
{
	private static final long serialVersionUID = 1L;

	public Pioneer bot;
	int myID = 0;
	static int curAgent = 0;
	final String ipAddress = "10.0.0.156";
	final int laserport = 7000;

	private static final String[] types = { "Global", "Basic", "Pioneer" };

	Object[] pioneerLock = new Object[0];

	Thread pioneerThread;
	PioneerData currentPioneerData;

	// Sean Stuff
	AbstractShapePortrayal2D myBottom;
	LabelledPortrayal2D myBottom2;

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
	{
		bottom = myBottom;
		//op2d.child = myBottom2;
		super.draw(object, graphics, info); 
	}

	public PioneerAgent(Horde horde)
	{
		super(horde);

		myID = curAgent;
		curAgent++;
		bot = new Pioneer(horde, ipAddress);
		addTypes(types);

		try
		{
			final Socket robotSocket = new Socket(ipAddress, laserport);

			pioneerThread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						Scanner scanner = new Scanner(robotSocket.getInputStream());

						while (true)
						{
							if (Thread.interrupted())
								break;

							String line = scanner.nextLine();

							PioneerData pd = new PioneerData();
							pd.readData(line);

							synchronized (pioneerLock)
							{
								currentPioneerData = pd;
								// System.err.println(pd.angle + "\t" + pd.distance);
								pioneerLock.notifyAll();
							}
						}

						scanner.close();
					} catch (Exception e)
					{
						// System.err.println("Error reading data from robot!");
						// e.printStackTrace();
					}

					try
					{
						System.err.println("[" + getClass().getName() + "] Shutting down robot socket");
						robotSocket.close();
						System.err.println("[" + getClass().getName() + "] Robot socket closed");
					} catch (Exception e)
					{
					}
				}
			});
			System.err.println("[" + getClass().getName() + "] Starting robot thread");
			pioneerThread.start();
			System.err.println("[" + getClass().getName() + "] Robot thread started");
		} catch (Exception e)
		{
			System.err.println("Failed to connect to robot: ");
			e.printStackTrace();
			System.exit(-1);
		}

		// Sean Stuff
		myBottom = new ImagePortrayal2D(Pioneer.class, "pioneer3dx.png")
		{
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
			{
				super.draw(object, graphics, info);

				// Draw the Border
				Rectangle2D.Double draw = info.draw;
				final double width = draw.width * scale;
				final double height = draw.height * scale;
				graphics.setPaint(paint);
				final int x = (int) (draw.x - width / 2.0);
				final int y = (int) (draw.y - height / 2.0);
				final int w = (int) (width);
				final int h = (int) (height);
				final int arc = (int) (width / 8.0);
				graphics.drawRoundRect(x, y, w, h, arc, arc);
			}
		};

		myBottom2 = new LabelledPortrayal2D(myBottom, LabelledPortrayal2D.DEFAULT_SCALE_X, -LabelledPortrayal2D.DEFAULT_SCALE_Y,
				LabelledPortrayal2D.DEFAULT_OFFSET_X, -LabelledPortrayal2D.DEFAULT_OFFSET_Y, new java.awt.Font("SansSerif", Font.PLAIN, 10),
				LabelledPortrayal2D.ALIGN_LEFT, null, java.awt.Color.RED, false);

	}

	public String getAgentName()
	{
		return super.getAgentName() + "_" + myID;
	}

	public void go()
	{
		super.go();
	}

	public PioneerData getCurrentData()
	{
		PioneerData pd;
		synchronized (pioneerLock)
		{
			try
			{
				pioneerLock.wait();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			pd = currentPioneerData;
		}

		return pd;
	}

	public double getBoxAngle()
	{
		PioneerData pd = getCurrentData();
		return pd.angle;
	}

	public double getDistToBox()
	{
		PioneerData pd = getCurrentData();
		return pd.distance;
	}

}
