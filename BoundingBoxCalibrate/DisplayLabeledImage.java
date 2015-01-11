import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

/**
   CALIBRATE2.JAVA
        
   @author Sean Luke
   @version 1
   @date Tue Apr  1 08:43:14 EDT 2014
        
   <p>
   Collor calibration tool for the Darwin-OP Robot
        
   <p>
   This class expects 100 image files, named imLogsFOO.jpg inside a directory named
   CalibrationImages/ right where you executed "java Calibrate2".    The FOO are
   zero-padded values 001 ... 100.  The images should store YCbCr values 
   masquerading as RGB  values in (at present) 320 x 480 JPG images (don't ask).
        
**/

public class DisplayLabeledImage extends JFrame
    {
    /** Width of the BufferedImages being displayed. */
    public static final int IMAGE_WIDTH = 640;
    /** Height of the BufferedImages being displayed. */
    public static final int IMAGE_HEIGHT = 480;
    /** The dimensionality for Y, Cb, and Cr respectively (thus a bit-depth of 6 each, totalling 18) */
    public static final int NUM_COLORS = 64;  // 2^COLOR_DEPTH

    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    int[/*width*/][/*height*/] ycbcrImage; // = new int[IMAGE_WIDTH][IMAGE_HEIGHT];

    /** Pixels colored to indicate labels.  This is the overlay image drawn on top of the underlying image in the window. */
    BufferedImage overlay = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    /** Display the overlay? Don't set this programmatically, let the checkbox do it. */
    boolean displayOverlay = true;
        
    /** The Y Cb Cr color space, each point given a different label. */     
    int[][][] data = new int[NUM_COLORS][NUM_COLORS][NUM_COLORS];
    /** The backup for the data, for undo purposes. */
    int[][][] prev = new int[NUM_COLORS][NUM_COLORS][NUM_COLORS];
        
    /** Should we disregard the Y information in the bounding box, that is, make the box maximal in the Y direction? */
    boolean disregardY = false;
        
    /** Should we only overlay all colors? */
    boolean allColors = true;
    
    /** Should we displaying a live feed */
    boolean liveFeed = false;
        
    /** GUI Widgets */
    JButton send = new JButton("Send");
    JButton save = new JButton("Save");
    JButton load = new JButton("Load");
    JButton undo = new JButton("Undo");
    JButton reload = new JButton("Reload");
    JComboBox labels;
    JLabel indexLabel = new JLabel("1");
    JCheckBox displayOverlayCheck = new JCheckBox("Overlay");
    JCheckBox liveFeedCheck = new JCheckBox("Live?");
    JCheckBox allColorsCheck = new JCheckBox("All Colors");
    JCheckBox disregardYCheck = new JCheckBox("No Y");
        
    /** Labels for the JComboBox */
    String cyan = new String("<html><body bgcolor=#00FFFF><font color=black><b>Cyan</b></font></body></html>");
    String magenta = new String("<html><body bgcolor=#FF00FF><font color=black><b>Magenta</b></font></body></html>");
    String green = new String("<html><body bgcolor=#00FF00><font color=black><b>Green</b></font></body></html>");
    String white = new String("<html><body bgcolor=white><font color=black><b>White</b></font></body></html>");
    String yellow = new String("<html><body bgcolor=yellow><font color=black><b>Yellow</b></font></body></html>");
    String orange = new String("<html><body bgcolor=orange><font color=black><b>Orange</b></font></body></html>");
    String other = new String("<html><body bgcolor=black><font color=white><b>Other</b></font></body></html>");

        
        
    /** Indexes for color labels */
    public static final int OTHER = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int WHITE = 4;
    public static final int MAGENTA = 5;
    public static final int CYAN = 6;
        
    /** Displayed colors for color labels. */
    public static final int C_OTHER = new Color(0,0,0,0).getRGB();
    public static final int C_ORANGE = Color.ORANGE.getRGB();
    public static final int C_YELLOW = Color.YELLOW.getRGB();
    public static final int C_GREEN = new Color(0,255,0).getRGB();
    public static final int C_WHITE = Color.WHITE.getRGB();
    public static final int C_MAGENTA = new Color(255,0,255).getRGB();
    public static final int C_CYAN = new Color(0,255,255).getRGB();
        
        
    /** back up data to perhaps later undo. */
    public void backupData()
        {
        for(int x = 0; x < NUM_COLORS; x++)
            {
            int[][] datax = data[x];
            int[][] prevx = prev[x];
            for(int y = 0; y < NUM_COLORS; y++)
                {
                int[] dataxy = datax[y];
                int[] prevxy = prevx[y];
                for(int z = 0; z < NUM_COLORS; z++)
                    {
                    prevxy[z] = dataxy[z];
                    }
                }
            }
        }

    /** undo */
    public void restoreData()
        {
        for(int x = 0; x < NUM_COLORS; x++)
            {
            int[][] datax = data[x];
            int[][] prevx = prev[x];
            for(int y = 0; y < NUM_COLORS; y++)
                {
                int[] dataxy = datax[y];
                int[] prevxy = prevx[y];
                for(int z = 0; z < NUM_COLORS; z++)
                    {
                    dataxy[z] = prevxy[z];
                    }
                }
            }
        }
                
    /** Label all the color space data in the provided bounding volume with the provided label. */ 
    public void labelData(int minx, int maxx, int miny, int maxy, int minz, int maxz, int label)
        {
        int div = 256 / NUM_COLORS;
        for(int x = minx / div; x <= maxx / div; x++)
            {
            int[][] datax = data[x];
            for(int y = miny / div; y <= maxy / div;  y++)
                {
                int[] dataxy = datax[y];
                for(int z = minz / div; z <= maxz / div;  z++)
                    {
                    dataxy[z] = label;
                    }
                }
            }
        }

    final static byte[] lookupValuesOut = new byte[]{ 0, 1, 2, 8, 16, 4, 32 };

    public void saveData(OutputStream out) throws IOException
        {
        int count = 0;
        for(int x = 0; x < data.length; x++)
            {
            int[][] datax = data[x];
            for(int y = 0; y < datax.length;  y++)
                {
                int[] dataxy = datax[y];
                for(int z = 0; z < dataxy.length;  z++)
                    {
                    count++;
                    out.write(lookupValuesOut[dataxy[z]]);
                    }
                }
            }
        System.out.println("total count: "+count);
        }
    
    final static int[] lookupValuesIn = new int[]
    { OTHER, ORANGE, YELLOW, -1, MAGENTA, -1, -1, -1, GREEN, -1, -1, -1, -1, -1, -1, -1, WHITE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, CYAN};
    public void loadData(InputStream in) throws IOException
        {
        for(int x = 0; x < data.length; x++)
            {
            int[][] datax = data[x];
            for(int y = 0; y < datax.length;  y++)
                {
                int[] dataxy = datax[y];
                for(int z = 0; z < dataxy.length;  z++)
                    {
                    dataxy[z] = lookupValuesIn[in.read()];
                    }
                }
            }
        }
        

    /** Returns the width and height of the image as displayed in the window. */
    Dimension getDisplayedImageDimension()
        {
        // determine proper size to scale
        int scaleWidth = getWidth();
        int scaleHeight = (scaleWidth * IMAGE_HEIGHT) / IMAGE_WIDTH;
        if (scaleHeight > getHeight())
            {
            scaleHeight = getHeight();
            scaleWidth = (scaleHeight * IMAGE_WIDTH) / IMAGE_HEIGHT;
            }
        return new Dimension(scaleWidth, scaleHeight);
        }
        
    /** The first point in the rubber band.  Null when not set or unset. */
    Point from = null;
    /** The final point in the rubber band.  Null when not set or unset. */
    Point to = null;

    /** The widget which actually draws the images */
    public JPanel display = new JPanel()
        {
        public Dimension getPreferredSize() { return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT); }
        public Dimension getMinimumSize() { return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT); }
        public void paintComponent(Graphics g)
            {
            Graphics2D g2d = (Graphics2D) g;
            Dimension size = getDisplayedImageDimension();
                        
            // draw the basic image
            g2d.drawImage(image, 0, 0, size.width, size.height, Color.RED, null);
            // draw the overlay
            if (displayOverlay) g2d.drawImage(overlay, 0, 0,  size.width, size.height, null, null);
            // draw the rubber band
            if (from != null && to != null) 
                {
                g2d.setPaint(Color.RED);          // you might change the rubber band color
                int fx = Math.min(from.x, to.x);
                int fy = Math.min(from.y, to.y);
                int tx = Math.max(from.x, to.x);
                int ty = Math.max(from.y, to.y);
                g2d.drawRect(fx, fy, tx-fx, ty-fy);
                }
            }
        };
        
    // Java is irritating.  There's no adapter which implements both MouseMotionListener and MouseListener, grrrrrrr
    class MouseThing extends MouseAdapter implements MouseMotionListener 
        {
        public void mouseMoved(MouseEvent evt) { }
        public void mouseDragged(MouseEvent evt) { }
        }
        
    /** Event listener for the MouseEvent and MouseMotionEvent */
    MouseThing mouse = new MouseThing()
        {
        public void mousePressed(MouseEvent evt)
            {
            from = evt.getPoint();
                        
            // restrict the point to be within the image region
            Dimension d = getDisplayedImageDimension();
            if (from.x < 0) from.x = 0;
            if (from.x >= d.width) from.x = d.width - 1;
            if (from.y < 0) from.y = 0;
            if (from.y >= d.height) from.y = d.height - 1;

            // to is initially same as from, so we can do single-pixel clicks
            to = from;
            }
                        
        public void mouseReleased(MouseEvent evt)
            {
            // mouse event started elsewhere?  If so, ignore.
            if (from == null || to == null) return;
                        
            // backup for later undo if needed
            backupData();
                        
            to = evt.getPoint();

            // restrict the point to be within the image region
            Dimension d = getDisplayedImageDimension();
            if (to.x < 0) to.x = 0;
            if (to.x >= d.width) to.x = d.width - 1;
            if (to.y < 0) to.y = 0;
            if (to.y >= d.height) to.y = d.height - 1;

            // load the data based on the rubber band size
            grabPixels((from.x * IMAGE_WIDTH) / d.width, (from.y * IMAGE_HEIGHT) / d.height, 
                (to.x * IMAGE_WIDTH) / d.width, (to.y * IMAGE_HEIGHT) / d.height);
                        
            // get rid of the rubber band
            from = null;
            to = null;
            repaint();
            }
                        
        public void mouseDragged(MouseEvent evt)
            {
            to = evt.getPoint();

            // restrict the point to be within the image region
            Dimension d = getDisplayedImageDimension();
            if (to.x < 0) to.x = 0;
            if (to.x >= d.width) to.x = d.width - 1;
            if (to.y < 0) to.y = 0;
            if (to.y >= d.height) to.y = d.height - 1;

            display.repaint();
            }
                        
        public void mouseExited(MouseEvent evt)
            {
            // blah
            to = null;
            repaint();
            }
        };
                

    /** Converts from y cb cr to rgb, with the values stored in the provided (three-element) rgb array.  All values range 0...255. */
    public static void ycbcr2rgb(int y, int cb, int cr, int[] rgb) {
        int r = (int)(y + 1.402 * (cr - 128.0));
        int g = (int)(y - 0.34414 * (cb - 128.0) - 0.71414 * (cr - 128.0));
        int b = (int)(y + 1.772 * (cb - 128.0));
        if (r < 0) r = 0;
        if (r > 255) r = 255;
        if (g < 0) g = 0;
        if (g > 255) g = 255;
        if (b < 0) b = 0;
        if (b > 255) b = 255;
                        
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;         
        }

        
    /** Unpacks a ycbcr packed pixel integer into its pieces, each 0...255 */
    public void unpack_ycbcr(int packed, int[] separate)
        {
        Color c = new Color(packed);
        int y_ = c.getRed();
        int cb = c.getGreen();
        int cr = c.getBlue();
        separate[0] = y_;
        separate[1] = cb;
        separate[2] = cr;
        }
        

    /** The central algorithmic gimzmo.  Extracts all the pixels in the rubber band, gets their YCbCR values, 
        builds a bounding box on those values, labels all color space points inside that bounding box,
        then updates the overlay to dipslay the result.  */
    public void grabPixels(int x1, int y1, int x2, int y2)
        {
        int miny = 255;
        int maxy = 0;
        int mincr = 255;
        int maxcr = 0;
        int mincb = 255;
        int maxcb = 0;
        int[] ycbcr = new int[3];
                
                
        // are we flattening to two dimensions?
        if (disregardY)
            { miny = 0; maxy = 255; }
                
        // normalize coordinates
        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x2 < 0) x2 = 0;
        if (y2 < 0) y2 = 0;
        if (x1 >= IMAGE_WIDTH) x1 = IMAGE_WIDTH - 1;
        if (y1 >= IMAGE_HEIGHT) y1 = IMAGE_HEIGHT - 1;
        if (x2 >= IMAGE_WIDTH) x2 = IMAGE_WIDTH - 1;
        if (y2 >= IMAGE_HEIGHT) y2 = IMAGE_HEIGHT - 1;
        if (x1 > x2) { int tmp = x1; x1 = x2; x2 = tmp; }
        if (y1 > y2) { int tmp = y1; y1 = y2; y2 = tmp; }
                
        // get the bounding box
        for(int x = x1; x <= x2; x++)  // note <=, else we don't get a single point
            for(int y = y1; y <= y2; y++) // note <=, else we don't get a single point
                {
                // this is a lot slower than just grabbing the raster I guess
                unpack_ycbcr(ycbcrImage[x][y], ycbcr);
                if (miny > ycbcr[0]) miny = ycbcr[0];
                if (maxy < ycbcr[0]) maxy = ycbcr[0];
                if (mincb > ycbcr[1]) mincb = ycbcr[1];
                if (maxcb < ycbcr[1]) maxcb = ycbcr[1];
                if (mincr > ycbcr[2]) mincr = ycbcr[2];
                if (maxcr < ycbcr[2]) maxcr = ycbcr[2];
                }
                
        // label the data
        labelData(miny, maxy, mincb, maxcb, mincr, maxcr, labels.getSelectedIndex());
                
        updateOverlay();
        }
        
    /** Labels and colors all the pixels in the overlay image to reflect the current color space labeling. */
    public void updateOverlay()
        {
        int div = 256 / NUM_COLORS;
        int[] ycbcr = new int[3];
                
        // update the overlay
        for(int x = 0; x < IMAGE_WIDTH; x++)
            for(int y = 0; y < IMAGE_HEIGHT; y++)
                {
                unpack_ycbcr(ycbcrImage[x][y], ycbcr);
                overlay.setRGB(x,y,C_OTHER);  // clear it
                switch(data[ycbcr[0]/div][ycbcr[1]/div][ycbcr[2]/div])
                    {
                    case OTHER:
                        break;
                    case ORANGE:
                        if (allColors || labels.getSelectedIndex() == ORANGE) overlay.setRGB(x,y,C_ORANGE);
                        break;
                    case YELLOW:
                        if (allColors || labels.getSelectedIndex() == YELLOW) overlay.setRGB(x,y,C_YELLOW);
                        break;
                    case GREEN:
                        if (allColors || labels.getSelectedIndex() == GREEN) overlay.setRGB(x,y,C_GREEN);
                        break;
                    case WHITE:
                        if (allColors || labels.getSelectedIndex() == WHITE) overlay.setRGB(x,y,C_WHITE);
                        break;
                    case MAGENTA:
                        if (allColors || labels.getSelectedIndex() == MAGENTA) overlay.setRGB(x,y,C_MAGENTA);
                        break;
                    case CYAN:
                        if (allColors || labels.getSelectedIndex() == CYAN) overlay.setRGB(x,y,C_CYAN);
                        break;
                    default:
                        System.err.println("Invalid color label: " + data[ycbcr[0]/div][ycbcr[1]/div][ycbcr[2]/div]);
                        break;
                    }
                }
        display.repaint();
        }
        
        
        
    /** Stretches a single image. */
    public void stretchImage()
        {
        int[] rgb = new int[3];
                
        // this is the SLOW way to do it, assuming we're not grabbing the raster
        BufferedImage newImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < IMAGE_WIDTH; x++)
            for(int y = 0; y < IMAGE_HEIGHT; y++)
                {
                int yuv = image.getRGB(x, y);
                ycbcrImage[x][y] = yuv;

                Color c = new Color(yuv);
                int y_ = c.getRed();
                int cb = c.getGreen();
                int cr = c.getBlue();
                ycbcr2rgb(y_, cb, cr, rgb);
                newImage.setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
                }
        image = newImage;
        }
    public Color labelToColor(int color){
		switch(color)
                    {
                    case OTHER:
                        return new Color(0,0,0);
                    case ORANGE:
                	return C_ORANGE;       
                    case YELLOW:
                    	return C_YELLOW;    
                    case GREEN:
                        return C_GREEN;    
                    case WHITE:
			return C_WHITE;                        
                    case MAGENTA:
			return C_MAGENTA;
                    case CYAN:
			return C_CYAN;
                    default:
                	return new Color(0,0,0);    
		}

	

	
	}
    public static final byte COMMAND_PICTURE = 0;
    public static final byte COMMAND_LOOKUP = 1;
        
    /** Reloads the zeroth image */
    public void reload()
        {
        Socket sock = null;
        try 
            {
            sock = new Socket(ip, port);
            }
        catch(UnknownHostException e) { System.err.println("Unknown Host " + ip); return; }
        catch(IOException e) { System.err.println("IOError on connecting to " + ip + " on port " + port + "\n\n" + e); return; }
                
        InputStream input = null;
        OutputStream output = null;
        try
            {
            input = new BufferedInputStream(sock.getInputStream());
            output = sock.getOutputStream();
            output.write(COMMAND_PICTURE);
            output.flush();
                        
            for(int y = 0; y < IMAGE_HEIGHT; y++)         // we only grab half the stream
                for(int x = 0; x < IMAGE_WIDTH; x++)
                    {
                	    int _alpha = (byte)(input.read());  // This is actually y2, but we're ignoring that
               		    image.setRGB(x,y, labelToColor(_alpha));   
                    }
            }
        catch(IOException e) { System.err.println("Can't read image from socket."); }
                
        try { if (input != null) input.close(); } catch (IOException e) { }
        try { if (output != null) output.close(); } catch (IOException e) { 	

	switch(data[ycbcr[0]/div][ycbcr[1]/div][ycbcr[2]/div])
                    {
                    case OTHER:
                        break;
                    case ORANGE:
                        if (allColors || labels.getSelectedIndex() == ORANGE) overlay.setRGB(x,y,C_ORANGE);
                        break;
                    case YELLOW:
                        if (allColors || labels.getSelectedIndex() == YELLOW) overlay.setRGB(x,y,C_YELLOW);
                        break;
                    case GREEN:
                        if (allColors || labels.getSelectedIndex() == GREEN) overlay.setRGB(x,y,C_GREEN);
                        break;
                    case WHITE:
                        if (allColors || labels.getSelectedIndex() == WHITE) overlay.setRGB(x,y,C_WHITE);
                        break;
                    case MAGENTA:
                        if (allColors || labels.getSelectedIndex() == MAGENTA) overlay.setRGB(x,y,C_MAGENTA);
                        break;
                    case CYAN:
                        if (allColors || labels.getSelectedIndex() == CYAN) overlay.setRGB(x,y,C_CYAN);
                        break;
                    default:
                        System.err.println("Invalid color label: " + data[ycbcr[0]/div][ycbcr[1]/div][ycbcr[2]/div]);
                        break;
                    }
}
        try { sock.close(); } catch (IOException e) { }
        stretchImage();
        updateOverlay();
        display.repaint();
        }
                
    /** Reloads the zeroth image */
    public void sendData()
        {
        Socket sock = null;
        try 
            {
            sock = new Socket(ip, port);
            }
        catch(UnknownHostException e) { System.err.println("Unknown Host " + ip); return; }
        catch(IOException e) { System.err.println("IOError on connecting to " + ip + " on port " + port + "\n\n" + e); return; }
                
        OutputStream output = null;
        try
            {
            output = sock.getOutputStream();
            output.write(COMMAND_LOOKUP);
            saveData(output);
            }
        catch(IOException e) { System.err.println("Can't send calibration data to robot."); }
                
        try { if (output != null) output.close(); } catch (IOException e) { }
        try { sock.close(); } catch (IOException e) { }
        }


    String ip;  // if ip == null then we know we're loading files rather than fetching over a socket
    int port;
                
    /** Builds the GUI, loads the images, and sets things up. */
    public Calibrate2(String[] args) throws IOException
        {
        super("Bleah");
                
        if (args.length > 1)
            {
            ip = args[0];
            
            port = Integer.parseInt(args[1]);
            // we need to connect to a robot to get a feed
            System.err.println("Connecting to " + ip + " at " + port);
            ycbcrImage = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
            }
        else
            {
            System.err.println("Must provide both ip and port");
            }
                        
        for(int x = 0; x < IMAGE_WIDTH; x++)
            for(int y = 0; y < IMAGE_HEIGHT; y++)
                overlay.setRGB(x,y,C_OTHER);  // clear

        getContentPane().setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.X_AXIS);
                
        undo.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                restoreData();
                updateOverlay();
                display.repaint();
                }
            });

        load.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                load();
                }
            });

        send.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                sendData();
                }
            });

        save.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                save();
                }
            });
                        
        reload.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                reload();
                }
            });
                        
        liveFeedCheck.setSelected(false);
        liveFeedCheck.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent er)
                {
                liveFeed = liveFeedCheck.isSelected();
                }
            });
                
        box.add(liveFeedCheck);
        box.add(reload);
        box.add(load);
        box.add(save);
        box.add(send);
        box.add(undo);
        box.add(Box.createGlue());
        add(box, BorderLayout.NORTH);
        add(display, BorderLayout.CENTER);
        display.addMouseListener(mouse);
        display.addMouseMotionListener(mouse);
        box = new Box(BoxLayout.X_AXIS);
                
        labels = new JComboBox(new String[] { other, orange, yellow, green, white, magenta, cyan });

        labels.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                updateOverlay();
                display.repaint();
                }
            });

        box.add(labels);
        box.add(new JLabel("  Image "));
        box.add(indexLabel);
        box.add(displayOverlayCheck);
                
        displayOverlayCheck.setSelected(true);
        displayOverlayCheck.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                displayOverlay = displayOverlayCheck.isSelected();
                updateOverlay();
                display.repaint();
                }
            });
                        
        box.add(disregardYCheck);
        disregardYCheck.setSelected(false);
        disregardYCheck.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                disregardY = disregardYCheck.isSelected();
                updateOverlay();
                display.repaint();
                }
            });
                
        box.add(allColorsCheck);
        allColorsCheck.setSelected(true);
        allColorsCheck.addActionListener(new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
                {
                allColors = allColorsCheck.isSelected();
                updateOverlay();
                display.repaint();
                }
            });

        box.add(Box.createGlue());
        add(box, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        SwingUtilities.invokeLater(new Runnable() { public void run() { reload(); } });
                
        java.util.TimerTask task = new java.util.TimerTask() 
            { 
            public void run() 
                {
                if (liveFeed)
                    try { SwingUtilities.invokeAndWait(new Runnable() { public void run() { reload(); updateOverlay(); display.repaint(); } }); }
                    catch (Exception e) { } // do nothing
                } 
            };
        java.util.Timer timer = new java.util.Timer(true);
        timer.schedule(task, 0, INTERVAL);              
        }
                
    public static long INTERVAL = 1;
        
    /** Loads a new color space. You'll probably need to set up a file dialog. */
    public void load()
        {
        String path = "";
        InputStream input = null;
        try
            {
            FileDialog fd = new FileDialog(this, "Load File", FileDialog.LOAD);
            fd.setVisible(true);
            path = fd.getDirectory() + fd.getFile();
            if (path == null) return;
            input = new BufferedInputStream(new FileInputStream(new File(path)));
                
            // load stuff into data[] using scan
            // If you could't read properly, throw a RuntimeException with an appropriate string
            loadData(input);

            backupData();
            updateOverlay();
            display.repaint();
            }
        catch (FileNotFoundException e)
            {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No such file, or file not openable:\n" + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        catch (IOException e)
            {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cannot load file " + path + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        finally
            {
            try { if (input != null) input.close(); } catch (IOException e) { }
            }
        }
        
    /** Saves out a color space. You'll probably need to set up a file dialog, or just dump it to a random file. */
    public void save()
        {
        String path = "";
        OutputStream print = null;
        try
            {
            FileDialog fd = new FileDialog(this, "Load File", FileDialog.SAVE);
            fd.setVisible(true);
            path = fd.getDirectory() + fd.getFile();
            if (path == null) return;
            print = new BufferedOutputStream(new FileOutputStream(new File(path)));
                
            // save stuff from data[] using print
            // If you could't print properly for some reason -- unlikely -- throw a RuntimeException with an appropriate string
            saveData(print);
            System.err.println("I wrote the bytes");


            print.close();
            }
        catch (IOException e)
            {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error writing:\n" + path, "Error", JOptionPane.ERROR_MESSAGE);
            }
        /*catch (FileNotFoundException e)
          {
          e.printStackTrace();
          JOptionPane.showMessageDialog(this, "No such file, or file not openable:\n" + path, "Error", JOptionPane.ERROR_MESSAGE);
          }*/
        catch (RuntimeException e)
            {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cannot save to file " + path + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        finally
            {
            if (print!= null) try {
                    print.close();
                    }
                catch (IOException e)
                    {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error writing:\n" + path, "Error", JOptionPane.ERROR_MESSAGE);
                    }
            }
        }


    public static void main(String[] args) throws IOException
        {
        new Calibrate2(args);
        }
        
    }
