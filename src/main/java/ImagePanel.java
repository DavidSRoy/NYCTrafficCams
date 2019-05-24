package main.java;

import java.awt.Dimension;
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
	private URL url;
	
    public ImagePanel(URL url) throws IOException 
    {
    	this.url = url;
    	image = this.getImage();
    	Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
    }
    
    public BufferedImage getImage() throws IOException {
    	return ImageIO.read(this.url);
    }
    
    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);          
    }
    
    public void repaint(Graphics g) throws IOException {
    	g.clearRect(0, 0, getWidth(), getHeight());
    	image = getImage();
    	this.paintComponent(g);
    	
    }

}
