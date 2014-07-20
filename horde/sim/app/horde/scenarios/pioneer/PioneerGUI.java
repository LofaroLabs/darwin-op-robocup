package sim.app.horde.scenarios.pioneer;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class PioneerGUI
    {
    private static final long serialVersionUID = 1L;

    JFrame mainWindow;
    RobotPanel robotPanel;
    JMenuBar menuBar;
    JMenu mainMenu;
    JMenuItem fileQuit;

    public static void main(String[] args)
        {
        PioneerGUI pg = new PioneerGUI();
        pg.run();
        }

    private void run()
        {
        // create a main window
        mainWindow = new JFrame("Pioneer Horde");
        mainWindow.setLayout(new BorderLayout());
                
        // make a panel to contain
        //try { 
                
        robotPanel = new RobotPanel(mainWindow);
        /*} catch (Exception e) {
          System.out.println("ERROR:") ;
          e.printStackTrace();
          throw new RuntimeException("MENU");
                        
          } */
        //robotPanel.setLayout(new FlowLayout()); 
                
        // setup menu bar
        initMenuBar();
                
        mainWindow.add(menuBar, BorderLayout.NORTH);
        mainWindow.add(robotPanel, BorderLayout.CENTER);

        // we should do a clean exit when window is closed.
        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter()
            {
            public void windowClosing(WindowEvent e)
                {
                cleanExit();
                }
            });

        // finally, show it
        mainWindow.pack();
        mainWindow.setLocation(10, 10);
        mainWindow.setVisible(true);
        mainWindow.setSize(1300, 650);
        mainWindow.repaint();
                
        robotPanel.start();

        }

    private void initMenuBar()
        {
        menuBar = new JMenuBar();
        mainMenu = new JMenu("Menu");
        // quit
        fileQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
        fileQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        fileQuit.addActionListener(menuBarActionListener);

        mainMenu.add(fileQuit);
        menuBar.add(mainMenu);
        }

    private ActionListener menuBarActionListener = new ActionListener()
        {
        public void actionPerformed(ActionEvent e)
            {
            Object src = e.getSource();
            if (src == fileQuit)
                cleanExit();
            }
        };

    private void cleanExit()
        {
        robotPanel.stop(); 
        mainWindow.setVisible(false);
        mainWindow.dispose();
        System.exit(0);
        }

    }
