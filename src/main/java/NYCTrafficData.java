package main.java;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class NYCTrafficData {
	
	
	
	
	
	public static void main(String[] args) {
		//id, Speed, Travel Time, DataAsOf, linkName
		//HashMap<Integer, Integer, Integer, String, String> cameraData
		
		/*
		 * CAM 287 = ID 445  BBT Manhattan
		 * 
		 */
		Scanner input = null;
		try {
			URL url = new URL ("http://207.251.86.229/nyc-links-cams/LinkSpeedQuery.txt");
			input = new Scanner(url.openStream());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		while (input.hasNextLine()) {
			System.out.println(input.nextLine());
		}
	}

}
