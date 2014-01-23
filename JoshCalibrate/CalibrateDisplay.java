import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;


public class CalibrateDisplay {
	public JFrame frame;
	public JPanel panel;
	private JMenuBar menuBar;
	private JMenu menu;
	private ButtonGroup colors,modes;
	private JButton select,show;
	private JButton prev,next;
	private boolean adding=true;
	private boolean showing=true;
	private ImageIcon icon;
	private BufferedImage YUV;
	private JLabel label;
	private File[] imageLocs;
	private String[] colorNames;
	private ArrayList<HashSet<Integer>> data;
	private ArrayList<HashSet<Integer>> undoData;
	private ArrayList<HashSet<Integer>> redoData;
	private boolean redoable=false;
	private int imageIndex;
	private int view_x,view_y,view_w,view_h;
	private final int[] protoColors={Color.WHITE.getRGB(),Color.YELLOW.getRGB(),Color.ORANGE.getRGB(),Color.GREEN.getRGB()};// = [];
	public CalibrateDisplay(String[] colorNames,File[] imageLocs) throws Exception {
		this.imageLocs=imageLocs;
		this.colorNames=colorNames;

		data=new ArrayList<HashSet<Integer>>();
		for(int i=0;i<colorNames.length;i++)
			data.add(new HashSet<Integer>());
		frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel =new JPanel();
		//
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("File");
		menuBar.add(menu);
		//a group of JMenuItems
		JMenuItem loadFile = new JMenuItem("Load file");
		menu.add(loadFile);
		loadFile.addActionListener(new loadListener());
		JMenuItem saveFile = new JMenuItem("Save file");
		menu.add(saveFile);
		saveFile.addActionListener(new exportListener());
		frame.setJMenuBar(menuBar);
		//
		//mode=MODE.NONE;
		makeImage();
		makeCommands();
		makeColors(colorNames);
		frame.setLayout(new GridLayout());
		//frame.setSize(1080, 720);
		makeLayout();
		addListeners();
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void addListeners() {
		prev.addActionListener(new changeImageListener(-1));
		next.addActionListener(new changeImageListener(1));
		select.addActionListener(new selectListener());
		label.addMouseListener(new cropMouseListener());
		show.addActionListener(new showListener());
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new undoRedoDispatcher());
	}
	
	private class ZoomPopUp extends JPopupMenu {
	    JMenuItem zoomIn;
	    JMenuItem zoomOut;
	    int xcoord;
	    int ycoord;
	    public ZoomPopUp(int xcoord,int ycoord){
	    	this.xcoord=xcoord;
	    	this.ycoord=ycoord;
	        zoomIn = new JMenuItem("Zoom in");
	        zoomIn.addActionListener(new zoomInListener());
	        zoomOut = new JMenuItem("Zoom out");
	        zoomOut.addActionListener(new zoomOutListener());
	        add(zoomIn);
	        add(zoomOut);
	    }
	    
	    private class zoomInListener implements ActionListener{
			public void actionPerformed(ActionEvent press) {
				// FIXME Auto-generated method stub
				int new_w=view_w/2;
				int new_h=view_h/2;
				int new_x=xcoord-view_w/2;
				int new_y=ycoord-view_h/2;
				new_x=Math.max(0,new_x);
				new_y=Math.max(0,new_y);
				if(new_x+new_w>480){
					new_w-=(new_x+new_w-480);
				}
				if(new_y+new_h>320){
					new_h-=(new_y+new_h-320);
				}
				if(new_h>0&&new_w>0){
					view_w=new_w;
					view_h=new_h;
					view_x=new_x;
					view_y=new_y;
				}
				System.out.println(view_w+" "+view_h+" "+view_x+" "+view_y);
				try {
					makeImage();
				} catch (IOException e) {}
			}
		}
	    
	    private class zoomOutListener implements ActionListener{
			public void actionPerformed(ActionEvent press) {
				// FIXME Auto-generated method stub
				int new_w=view_w*2;
				int new_h=view_h*2;
				int new_x=view_x-view_w/4;
				int new_y=view_y-view_h/4;
				new_x=Math.max(0,new_x);
				new_y=Math.max(0,new_y);
				if(new_x+new_w>480){
					new_w-=(new_x+new_w-480);
				}
				if(new_y+new_h>320){
					new_h-=(new_y+new_h-320);
				}
				if(new_h>0&&new_w>0){
					view_w=new_w;
					view_h=new_h;
					view_x=new_x;
					view_y=new_y;
				}
				try {
					makeImage();
				} catch (IOException e) {
				}
			}
		}
	    
	}
	
	private class cropMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent click) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		int x,y;
		@Override
		public void mousePressed(MouseEvent press) {
			if(press.getButton()==MouseEvent.BUTTON1){
			x=press.getX();
			y=press.getY();
			}
			System.out.println(press.getButton());
			if(press.getButton()==MouseEvent.BUTTON3){
		        ZoomPopUp menu = new ZoomPopUp(press.getX(),press.getY());
		        menu.show(press.getComponent(), press.getX(), press.getY());
		        menu.setVisible(true);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void mouseReleased(MouseEvent release) {
			if(release.getButton()==MouseEvent.BUTTON1){
			redoable=false;
			undoData=new ArrayList<HashSet<Integer>>();//) data.clone();
			for(HashSet<Integer> hash:data){
				undoData.add((HashSet<Integer>) hash.clone());
			}
			int newX=release.getX();
			newX=Math.max(0,newX);
			newX=Math.min(newX,icon.getIconWidth());
			int newY=release.getY();
			newY=Math.max(0,newY);
			newY=Math.min(newY,icon.getIconHeight());
			int width=Math.max(1,Math.abs(newX-x));
			int height=Math.max(1,Math.abs(newY-y));
			x=Math.min(x,newX);
			y=Math.min(y,newY);
			BufferedImage subimage=toBufferedImage(YUV.getSubimage(view_x,view_y,view_w,view_h).getScaledInstance(480, 320,  java.awt.Image.SCALE_SMOOTH)).getSubimage(x,y,width,height);
			//Get index of color currently selected.
			Enumeration<AbstractButton> colorList=colors.getElements();
			int index=0;
			try{
			while(!colorList.nextElement().isSelected()){
				index++;
			}
			}catch(NoSuchElementException e){}
			addpixelsToSet(subimage,index);
			if(showing)
				try {
					makeImage();
				} catch (IOException e) {}
			}
		}

		private void addpixelsToSet(BufferedImage subimage, int index) {
			//We're given a bufferedImage and we want to take the pixels in it, get the set of unique 18-bit color codes present in the image,
			//add them to the set specified by index and remove it from the other sets if necessary.
			HashSet<Integer> pixelValues =new HashSet<Integer>();
			for(int x=0;x<subimage.getWidth()-1;x++){
				for(int y=0;y<subimage.getHeight()-1;y++){
					pixelValues.add(to16bit(subimage.getRGB(x,y)));
				}
			}
			if(adding){
			for(int x=0;x<data.size();x++){
				if(x==index){
					data.get(x).addAll(pixelValues);
				}
				else{
					data.get(x).removeAll(pixelValues);
				}
			}
			}
			else
				data.get(index).removeAll(pixelValues);
			return;
		}
	}
	
	private BufferedImage toBufferedImage(Image im) {
		BufferedImage result=new BufferedImage(480,320,BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(im,0,0,null);
		return result;
	}
	
	private class selectListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//Change the label.
			adding=!adding;
			select.setText(adding?"Adding labels":"Removing labels");
			try {
				makeImage();
			} catch (IOException e1) {}
			
		}
	}
	
	private class showListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			showing=!showing;
			show.setText(showing?"Hide labels":"Show labels");
			try {
				makeImage();
			} catch (IOException e1) {}
		}
	}
	
	private class loadListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
		    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("RAW colortables", "raw");
		        chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String fname=chooser.getSelectedFile().toString();
		    	Path path = Paths.get(fname);
		    	try {
		    		byte[] bytes = Files.readAllBytes(path);
		    		data=new ArrayList<HashSet<Integer>>();
		    		for(int i=0;i<4;i++)
		    			data.add(new HashSet<Integer>());
		    		HashMap<Integer,Integer>colorMap= new HashMap<Integer, Integer>();
		    		colorMap.put(16,0);
		    		colorMap.put(2,1);
		    		colorMap.put(1,2);
		    		colorMap.put(8,3);
		    		for(int b=0;b<bytes.length;b++){
		    			int val=bytes[b];
		    			if(val!=0){
		    				int index=colorMap.get(val);
		    				data.get(index).add(b);
		    			}
		    		}
		    		JOptionPane.showMessageDialog(null,"Finished loading.");
		    		if(showing)
		    			makeImage();
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	}
		    }
		}
		
	}
	
	private class exportListener implements ActionListener{
		//These are 8-bit numbers
		//0=Orange
		//1=Yellow
		//2=Ignored (Cyan)
		//3=Green(Field)
		//4=White
		//5,6,8=Explicitly nothing (Default)
		public void actionPerformed(ActionEvent e) {
			//FIXME This is so suboptimal. 
		    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("RAW colortables", "raw");
		        chooser.setFileFilter(filter);
		    int returnVal = chooser.showSaveDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String fname=chooser.getSelectedFile().toString();
			HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
			//{"White","Yellow","Orange","Green"};
			//White=16
			//Yellow=2
			//Orange=1
			//Green=8
			//Black=0
			colorMap.put(0,16);
			colorMap.put(1,2);
			colorMap.put(2,1);
			colorMap.put(3,8);
			try {
				byte[] output = new byte[(int) Math.pow(2,18)];
				for(int b=0;b<output.length;b++){
					output[b]=0;
				}
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fname));
				for(int x=0;x<data.size();x++){
					HashSet<Integer> colour=data.get(x);
					for(Integer i:colour){
						output[i]=(byte)(int)colorMap.get(x);
					}
				}
				bos.write(output);
				bos.flush();
				bos.close();
				JOptionPane.showMessageDialog(null,"Finished writing.");
			} catch (IOException e1) {}
		}
		}
	}
	
	private class changeImageListener implements ActionListener{
		int delta;
		private changeImageListener(int delta){
			this.delta=delta;
		}
		public void actionPerformed(ActionEvent arg0) {
			imageIndex+=delta;
			try {
				makeImage();
				frame.revalidate();
			}catch(IOException e){e.printStackTrace();}
		}
	}
	
    private class undoRedoDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent key) {
        	if(key.getID()==KeyEvent.KEY_PRESSED){
        		if (!redoable&&(key.getKeyCode() == KeyEvent.VK_Z) && ((key.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
        			redoData=data;
                	data=undoData;
                	redoable=true;
            	}
            
            	if (redoable&&(key.getKeyCode() == KeyEvent.VK_Y) && ((key.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            		undoData=data;
                	data=redoData;
                	redoable=false;
            	}
            	try {
					makeImage();
				} catch (IOException e) {}
        	}
            return true;
        }
    }

	private BufferedImage getShowListenerImage(){
		Image img=icon.getImage();
		BufferedImage result = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D bGr = result.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		BufferedImage backing=toBufferedImage(YUV.getSubimage(view_x,view_y,view_w,view_h).getScaledInstance(480, 320,  java.awt.Image.SCALE_SMOOTH));
		for(int i=0;i<data.size();i++){
			HashSet<Integer> set=data.get(i);
			for(int x=0;x<result.getWidth();x++){
				for(int y=0;y<result.getHeight();y++){
					int temp=to16bit(backing.getRGB(x,y));
					if(set.contains(temp)){
						result.setRGB(x,y,protoColors[i]);
					}
				}
			}
		}
		return result;
	}
	
	public ImageIcon getImage() throws IOException{
		if(YUV==null){
			view_x=0;
			view_y=0;
			view_w=480;
			view_h=320;
		}
		if(imageIndex<0)
			imageIndex+=imageLocs.length;
		else
			imageIndex=imageIndex%imageLocs.length;
		BufferedImage read=ImageIO.read(imageLocs[imageIndex]);
		Image tem=read.getScaledInstance(480, 320,  java.awt.Image.SCALE_SMOOTH);
		read=new BufferedImage(480,320,BufferedImage.TYPE_INT_RGB);
		read.getGraphics().drawImage(tem,0,0,null);
		YUV=read;
		read=ycbcr2rgb(read);
		tem=read.getSubimage(view_x,view_y,view_w,view_h).getScaledInstance(480, 320,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon temp=new ImageIcon(tem);
		if(showing){
			setImage(temp);
			BufferedImage b=getShowListenerImage();
			temp=new ImageIcon(b);
		}
		return temp;
	}
	
	private  void setImage(ImageIcon icon){
		this.icon=icon;
		if(label==null)
			label=new JLabel(icon);
		else
			label.setIcon(icon);
	}
	private void makeImage() throws IOException {
        setImage(getImage());
	}
	
	private void makeLayout() {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.ipadx=3;
		c.ipady=3;
		panel.add(prev,c);
		c.gridx = 1;
		panel.add(next,c);
		c.gridx = 2;
		panel.add(select,c);
		c.gridx = 3;
		panel.add(show,c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth=5;
		c.gridheight=3;
		panel.add(label,c);
		c.gridx=0;
		c.gridy = 7;
		c.gridwidth=1;
		c.gridheight=1;
		Enumeration<AbstractButton> colorElems=colors.getElements();
		for(int i=0;i<colorNames.length;i++){
			panel.add((JRadioButton)colorElems.nextElement(),c);
			c.gridx++;
		}	
	}
	
	private void makeCommands() {
		prev=new JButton("Previous Image");
		next=new JButton("Next Image");
		modes = new ButtonGroup();
		select = new JButton("Adding labels");
		show = new JButton("Hide labels");
		modes.add(show);
		modes.add(select);
	}
	private void makeColors(String[] colorNames) {
		colors= new ButtonGroup();
		for(int i=0;i<colorNames.length;i++){
			colors.add(new JRadioButton(colorNames[i]));
		}
	}
	private int to16bit(int rgb) {
		Color c = new Color(rgb);
		int red= c.getRed()>>2;
		int green= c.getGreen()>>2;
		int blue= c.getBlue()>>2;
		int result=red<<12|green<<6|blue;
		return result;
	}
	
	private BufferedImage ycbcr2rgb(BufferedImage ycbcr){
		BufferedImage rgb = new BufferedImage(ycbcr.getWidth(), ycbcr.getHeight(), BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<ycbcr.getWidth();x++){
			for(int z=0;z<ycbcr.getHeight();z++){
				Color yuv=new Color(ycbcr.getRGB(x,z));
				int y =yuv.getRed();
				int cb=yuv.getGreen();
				int cr=yuv.getBlue();
				double Y = (double) y;
				double Cb = (double) cb;
				double Cr = (double) cr;
				int r = (int) (Y + 1.40200 * (Cr - 0x80));
				int g = (int) (Y - 0.34414 * (Cb - 0x80) - 0.71414 * (Cr - 0x80));
				int b = (int) (Y + 1.77200 * (Cb - 0x80));
				r = Math.max(0, Math.min(255, r));
				g = Math.max(0, Math.min(255, g));
				b = Math.max(0, Math.min(255, b));
				Color c= new Color(r, g, b);
				rgb.setRGB(x,z,c.getRGB());
			}
		}

		return rgb;
	}
	public static void main(String[] args) throws Exception{
		String[] colorNames=getColors();
		File[] images = getImages();
		new CalibrateDisplay(colorNames,images);
	}

	private static String[] getColors() {
		return new String[]{"White","Yellow","Orange","Green"};
	}

	private static File[] getImages() throws IOException {
	    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setDialogTitle("Select calibration images directory");
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File folder =chooser.getSelectedFile();
	    	File[] listOfFiles = folder.listFiles();
	    	return listOfFiles;
	    }
	    return null;
	}
}
