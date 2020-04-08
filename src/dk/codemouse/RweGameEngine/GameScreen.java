package dk.codemouse.RweGameEngine;

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
		
		setPreferredSize(frame.frameDimension);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				GameEngine.MouseX = e.getX() / GameEngine.getPixelSize();
				GameEngine.MouseY = e.getY() / GameEngine.getPixelSize();
			}
			
			public void mouseDragged(MouseEvent e) {
				GameEngine.MouseX = e.getX() / GameEngine.getPixelSize();
				GameEngine.MouseY = e.getY() / GameEngine.getPixelSize();
			}
		});	
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		
		if (frame.useAntiAliasing()) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			// rh.put( RenderingHints.KEY_RENDERING,
			// RenderingHints.VALUE_RENDER_QUALITY);
			// rh.put( RenderingHints.KEY_DITHERING,
			// RenderingHints.VALUE_DITHER_ENABLE);
	
			g2.setRenderingHints(rh);
		}
		
		frame.onUserDraw(g2);
	}
	
	public void resizeWindow(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		frame.frameDimension.width  = this.getSize().width + frame.insets.left + frame.insets.right;
		frame.frameDimension.height = this.getSize().height + frame.insets.top + frame.insets.bottom;
	}

}
