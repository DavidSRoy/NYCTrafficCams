package main.java;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class NYCTrafficCams {
	static ArrayList<VideoStream> cams = new ArrayList<VideoStream>();
	static ArrayList<ImagePanel> images = new ArrayList<ImagePanel>();
	public static void main(String[] args) throws IOException, InterruptedException {
		addCams();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(1080, 720);
		frame.setLayout(new GridLayout(0, 4, 10, 10));
		frame.setVisible(true);
		while (true) {
			for (VideoStream cam : cams) {
				ImagePanel img = cam.getImage(frame.getGraphics());
				images.add(img);
				img.setBounds(-100, -200, 100, 100);
				frame.add(img);
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
		String[] channels = {"212", "266", "4"};
		for (String chnl : channels) {
			String cam = "cctv" + chnl;
			cams.add(new VideoStream("http://207.251.86.238/" + cam + ".jpg"));
			System.out.println("Added " + cam);
		}
	}
}
