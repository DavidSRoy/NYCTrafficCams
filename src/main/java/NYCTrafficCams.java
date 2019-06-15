package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * @author David Roy
 * Last edited 06/15/19 11:32
 * 
 * Collect images from traffic cameras
 * in New York City and scan them for objects
 * of interest using a machine learning
 * model
 * 
 */
public class NYCTrafficCams {
	static ArrayList<TrafficCamera> cams = new ArrayList<TrafficCamera>();
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
		LabelImage classifier = new LabelImage();
		
		//main loop
		while (true) {
			for (TrafficCamera cam : cams) {
				ImagePanel img = cam.getImage(frame.getGraphics());
				images.add(img);
				img.setBounds(-110, -200, 200, 200);
				frame.add(img);
				frame.pack();
				
				/*
				 * Split the image into subImages 
				 * and classify each subImage
				 * 
				 * (about ((scale)^2 + scale) images)
				 */
				int width = img.getImage().getWidth();
				int height = img.getImage().getHeight();
				
				double scale = 2.2;
				int subWidth = (int) (width / scale);
				int subHeight = (int) (height / scale);
				
				System.out.println();
				System.out.println(cam.getLocation());
				
				//iterate through the image, shifting the frame of reference
				//draw a red rectangle to show the frame of reference
				for (int x = 0 ; x < width - subWidth; x += clamp(subWidth, 0, width) / 2) {
					for (int y = 0; y < height - subHeight; y += clamp(subHeight, 0, height) / 2) {
						Graphics g = img.getGraphics();
						g.clearRect(0, 0, width, height);
						img.repaint(g);
						g.setColor(Color.RED);
						g.drawRect(x, y, subWidth, subHeight);
						System.out.println(classifier.classify(img.getImage().getSubimage(x, y, subWidth, subHeight)));
					}
				}

				

			}
			
			//remove all images to refresh
			for (ImagePanel img : images) {
				frame.remove(img);
			}
		}

	}
	
	//access and add cameras to cams List
	public static void addCams() throws MalformedURLException {
		int[] channels = {794, 212, 261, 299};
		for (int chnl : channels) {
			String cam = "cctv" + chnl;
			cams.add(new TrafficCamera("http://207.251.86.238/"
					 + cam + ".jpg", chnl));
			System.out.println("Added " + cam + ": " + locations.get(chnl));
		}
	}
	
	public static void setLocations() {
		locations.put(299, "5th Ave @ 57 St.");
		locations.put(261, "1 Ave @ 110 St.");
		locations.put(212, "Water St. @ Wall St.");
		locations.put(266, "null");
		locations.put(794, "5th Ave @ 65th St.");		
	}
	
	/**
	 * Keep x between min and max
	 * @param x
	 * @param min
	 * @param max
	 * @return int between min and max
	 */
	public static int clamp(int x, int min, int max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}
}
