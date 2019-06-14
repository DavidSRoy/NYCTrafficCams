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
		LabelImage classifier = new LabelImage();
		while (true) {
			for (VideoStream cam : cams) {
				ImagePanel img = cam.getImage(frame.getGraphics());
				images.add(img);
				img.setBounds(-110, -200, 200, 200);
				frame.add(img);
				//frame.add(new JLabel(cam.getLocation()));
				frame.pack();
				
				/*
				 * Split the image into 4 subImages and 
				 * classify each subImage
				 */
				int width = img.getImage().getWidth();
				int height = img.getImage().getHeight();
//				/System.out.println(width);
				int scale = 2;
				int subWidth = width / scale;
				int subHeight = height / scale;
				for (int x = 0 ; x < width; x += subWidth) {
					for (int y = 0; y < height; y += subHeight) {
						System.out.println(classifier.classify(img.getImage().getSubimage(x, y, subWidth, subHeight)));
					}
				}

				

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
		int[] channels = {794, 212, 266, 299};
		for (int chnl : channels) {
			String cam = "cctv" + chnl;
			cams.add(new VideoStream("http://207.251.86.238/"
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
}
