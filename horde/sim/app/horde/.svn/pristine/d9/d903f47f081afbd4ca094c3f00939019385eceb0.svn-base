
package sim.app.horde.scenarios.humanoid.console;
 
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HordeConsole extends edu.gmu.robocup.Console 
    {       
    public static void main(String[] cmdLine) {
        CLASS_NAME = HordeConsole.class.getName();
        Map<String, Object> args = parseCommandLine(cmdLine);
        if (args.containsKey("usage") || args.containsKey("help")) {
            log.println("Command Line Options:");
            log.println("  -remote_addr  : SRV-1 IP Address");
            log.println("  -remote_port  : SRV-1 Port");
            log.println("  -protocol     : TCP or UDP");
            log.println("  -local_port   : Local port (for UDP only)");
            log.println("  -archive      : Archive directory for JPEGs");
            return;
            }
        if (args.containsKey("remote_addr"))
            SRV_HOST = (String) args.get("remote_addr");
        if (args.containsKey("remote_port"))
            SRV_PORT = toInt((String) args.get("remote_port"), 10001);
        if (args.containsKey("protocol"))
            SRV_PROTOCOL = ((String) args.get("protocol")).toUpperCase();
        if (args.containsKey("local_port"))
            UDP_LOCAL_PORT = toInt((String) args.get("local_port"), 10001);
        if (args.containsKey("archive"))
            ARCHIVE_DIR = (String) args.get("archive");

        // make a panel to contain the SRVPanel objects
        srvPanelContainer = new JPanel();
        srvPanelContainer.setLayout(new BoxLayout(srvPanelContainer,
                BoxLayout.X_AXIS));             
                
        // add the first panel based on the command line parameters
                
        final HordePanel panel = new HordePanel(SRV_HOST, SRV_PORT, UDP_LOCAL_PORT,
            SRV_PROTOCOL, ARCHIVE_DIR, ARCHIVE_PREFIX);
        srvPanelContainer.add(panel);

        // initialize menu bar
        initMenuBar();

        // create a main window
        mainWindow = new JFrame("Horde SRV Console");
        mainWindow.setLayout(new BorderLayout());
        mainWindow.add(menuBar, BorderLayout.NORTH);
        mainWindow.add(srvPanelContainer, BorderLayout.CENTER);

        // we should do a clean exit when window is closed.
        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                //if (panel != null) { 
                //panel.cleanupWiimote(); 
                //panel.stopRobot(); 
                //}
                cleanExit();
                }
            });

        // finally, show it
        mainWindow.pack();
        mainWindow.setLocation(100, 100);
        mainWindow.setVisible(true);
        mainWindow.repaint();

        // start first console
        if (panel != null) panel.startConsole();
        }
    }
