package main.java;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class NYCTrafficCams {
	static ArrayList<VideoStream> cams = new ArrayList<VideoStream>();
	static ArrayList<ImagePanel> images = new ArrayList<ImagePanel>();
	public static HashMap<Integer, String> locations = new HashMap<Integer, String>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		setLocations();
		addCams();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(2000, 720);
		frame.setLayout(new GridLayout(0, 4, 10, 10));
		frame.setVisible(true);
		while (true) {
			for (VideoStream cam : cams) {
				ImagePanel img = cam.getImage(frame.getGraphics());
				images.add(img);
				img.setBounds(-110, -200, 200, 200);
				frame.add(img);
				//frame.add(new JLabel(cam.getLocation()));
				frame.pack();

			}
			
			//Thread.sleep(1);
			
			for (ImagePanel img : images) {
				frame.remove(img);
			}

			// Thread.sleep(1);
			// frame.removeAll();

		}

	}
	
	public static void addCams() throws MalformedURLException {
		int[] channels = {212, 266, 299, 261};
		for (int chnl : channels) {
			String cam = "cctv" + chnl;
			cams.add(new VideoStream("http://207.251.86.238/" + cam + ".jpg", chnl));
			System.out.println("Added " + cam + ": " + locations.get(chnl));
		}
	}
	
	public static void setLocations() {
		locations.put(299, "5th Ave @ 57 St.");
		locations.put(261, "1 Ave @ 110 St.");
		locations.put(212, "null");
		locations.put(266, "null");
		
		
	}
}
