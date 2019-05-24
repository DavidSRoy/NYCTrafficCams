package main.java;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		VideoStream cctv261 = new VideoStream("http://207.251.86.238/cctv261.jpg"); //?
		VideoStream cctv212 = new VideoStream("http://207.251.86.238/cctv212.jpg"); // Water St. and Wall St.
		ArrayList<VideoStream> cams = new ArrayList<VideoStream>();
		cams.add(cctv261);
		cams.add(cctv212);
		
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(1080, 720);
		frame.setLayout(new GridLayout(4, 4));
		frame.setVisible(true);
		//while (true) {
			int x = 0;
			int y = 0;
			frame.add(new JLabel("Test"));
			for (VideoStream cam : cams) {
				ImagePanel img = cam.getImage(frame.getGraphics());
				img.setBounds(x += 60, y, frame.getWidth(), frame.getHeight());
				frame.add(img);
				// frame.pack();
				
			}
			Thread.sleep(1);
			
			frame.removeAll();
			x = 0;
			y = 0;
		//}
		
		
	}
}
