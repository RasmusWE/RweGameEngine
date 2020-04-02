package dk.codemouse.RweGameEngine;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameScreen extends JPanel {

	private GameFrame frame;

	public GameScreen(GameFrame frame) {
		this.frame = frame;
		this.setPreferredSize(frame.frameDimension);
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				GameEngine.MouseX = e.getX() / GameEngine.PIXEL_SIZE;
				GameEngine.MouseY = e.getY() / GameEngine.PIXEL_SIZE;
			}
			
			public void mouseDragged(MouseEvent e) {
				GameEngine.MouseX = e.getX() / GameEngine.PIXEL_SIZE;
				GameEngine.MouseY = e.getY() / GameEngine.PIXEL_SIZE;
			}
		});	
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		// rh.put( RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_QUALITY);
		// rh.put( RenderingHints.KEY_DITHERING,
		// RenderingHints.VALUE_DITHER_ENABLE);

		g2.setRenderingHints(rh);
		g2.setStroke(new BasicStroke(GameEngine.PIXEL_SIZE));
		
		frame.onUserDraw(g2);
	}
	
	public void resizeWindow(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		this.setSize(width, height);

		frame.frameDimension = this.getSize();
	}

}
