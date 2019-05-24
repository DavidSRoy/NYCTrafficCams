package main.java;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class ImagePanel extends JPanel
{
     
	private BufferedImage image;
	
    public ImagePanel(URL url) throws IOException 
    {
    	image = ImageIO.read(url);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);          
    }
    
    public void repaint(Graphics g) {
    	g.clearRect(0, 0, getWidth(), getHeight());
    	this.paintComponent(g);
    	
    }

}
