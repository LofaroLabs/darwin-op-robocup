import java.io.File;
import java.io.IOException;


public class Calibrate {
	private static String[] colorNames;
	public static void main(String[] args) throws Exception{
		getColors();
		File[] images = getImages();
		new CalibrateDisplay(colorNames,images);
	}

	private static void getColors() {
		colorNames=new String[]{"White","Yellow","Orange","Green"};
	}

	private static File[] getImages() throws IOException {
		// FIXME Auto-generated method stub
		File folder = new File("C:/Copy/workspace/Color Calibration/CalibrationImages");
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
}
