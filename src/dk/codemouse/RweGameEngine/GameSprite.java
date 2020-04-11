package dk.codemouse.RweGameEngine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GameSprite {
	
	private BufferedImage image;
	private boolean loaded;

	public GameSprite(String imageFile) {
		try {
			URL url = this.getClass().getResource(imageFile);  
			if (url != null)
				image = javax.imageio.ImageIO.read(new File(url.getFile()));
			else
				image = javax.imageio.ImageIO.read(new File(imageFile));
			
			loaded 	= true;
		} catch (IOException e) {
			System.err.println("Failed to load game sprite: " + imageFile);
			e.printStackTrace();
		}
	}
	
	public GameSprite(GameSprite sprite) {
		this.image 	= sprite.image;
		loaded 		= true;
	}
	
	public GameSprite(BufferedImage image) {
		this.image 	= image;
		loaded 		= true;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public BufferedImage getBufferedImage() {
		return image;
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
	
	public Color getPixel(int x, int y) {
		return new Color(image.getRGB(x, y), true);
	}
	
	public GameSprite rotate(double angleInDegrees) {
        double rads = Math.toRadians(angleInDegrees);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = getWidth();
        int h = getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotated.createGraphics();
        
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put( RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		rh.put( RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHints(rh);

        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g.setTransform(at);
        g.drawImage(image, null, 0, 0);
        g.dispose();

        return new GameSprite(rotated);
    }
	
}
