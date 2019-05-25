package main.java;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;


//https://webcams.nyctmc.org/
public class VideoStream {
	private URL url;
	private ImagePanel image;
	private String location;
	private int id;
	
	public VideoStream(String url, int id) throws MalformedURLException {
		this.url = new URL(url);
		this.id = id;
		this.location = NYCTrafficCams.locations.get(id);
	}
	
	public ImagePanel getImage(Graphics g) throws IOException {
		image = new ImagePanel(url);
		image.paintComponent(g);
		return image;
	}
	
	public void startStream() {

	}
	
	public String getLocation() {
		return this.location;
	}
	
	

	
	
	
	public static void main(String args[]) throws InterruptedException, IOException {
		String camIP = "http://207.251.86.238:8080/cctv261.mjpg";
		URL url = new URL("http://207.251.86.238/cctv261.jpg");
		URLConnection connection = url.openConnection();


		// Initialize swing components
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImagePanel img = new ImagePanel(url);

		/*
		 * VideoCapture camView = new VideoCapture();
		 * 
		 * camView.open(camIP);
		 * 
		 * if(!camView.isOpened()) { System.out.println("Camera Error.."); } else {
		 * System.out.println("Camera successfully opened"); }
		 * 
		 * videoCamera cam=new videoCamera(camView);
		 * 
		 * 
		 * frame.add(cam);
		 * 
		 */

		frame.setSize(1080, 720);
		frame.setVisible(true);

		while (true) {

			frame.remove(img);
			img = new ImagePanel(url);
			img.paintComponent(frame.getGraphics());
			img.setLocation(20, 20);

			img.setSize(img.getWidth() * 2, img.getHeight() * 2);
			frame.add(img, BorderLayout.NORTH);
			// frame.pack();
			Thread.sleep(1);

			// cam.repaint();
		}
	}
}


/*
@SuppressWarnings("serial")
class videoCamera extends JPanel {
	VideoCapture camera;

	public videoCamera(VideoCapture cam) {
		camera = cam;
	}

	public BufferedImage Mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return img;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Mat mat = new Mat();

		if (camera.read(mat)) {
			System.out.print("IMAGE");
		}

		BufferedImage image = Mat2BufferedImage(mat);

		g.drawImage(image, 10, 10, image.getWidth(), image.getHeight(), null);
	}

	public Mat turnGray(Mat img) {
		Mat mat1 = new Mat();
		Imgproc.cvtColor(img, mat1, Imgproc.COLOR_RGB2GRAY);
		return mat1;
	}

	public Mat threash(Mat img) {
		Mat threshed = new Mat();
		int SENSITIVITY_VALUE = 100;
		Imgproc.threshold(img, threshed, SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);
		return threshed;
	}
}

*/
