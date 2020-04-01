
package dk.codemouse.RweGameEngine;

import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * 
 * Simple "Game engine" wrapper - Inspired from OneLoneCoders Console and Pixel GameEngines in C++.
 * 
 * Simply extend this class and implement the onUserCreate, onUserUpdate and onUserDraw to utilize the window and internal game loop.
 * 
 * Mouse position and key controls available out-of-the-box. Easily add custom listeners or access JFrame and JPanel screen directly.
 * 
 * @author Rasmus Wehlast Engelbrecht
 * @version 1.0
 * 
 */

public abstract class GameEngine {
	
	protected String title = "RweGameEngine";
	
	protected GameFrame frame;
	
	public static int TFPS = 60;
	public static int TUPS = 60;
	public static int CURRENT_FPS  = 0;
	public static int CURRENT_UPS  = 0;
	
	public static int MouseX = 0;
	public static int MouseY = 0;
	
	public GameKeys keys = new GameKeys();
	
	private MouseListener customMouseListener;
	private MouseMotionListener customMouseMotionListener;

	private GameLoop loop;
	
	private boolean constructed = false;

	public int screenWidth() {
		return frame.frameDimension.width;
	}
	
	public int screenHeight() {
		return frame.frameDimension.height;
	}
	
	public boolean keyReleased(int keyCode) {
		Object released = keys.keyReleased.get(keyCode);
		if (released != null)
			return (boolean) released;
		
		return false;
	}
	
	public boolean keyPressed(int keyCode) {
		Object pressed = keys.keyPressed.get(keyCode);
		if (pressed != null)
			return (boolean) pressed;
		
		return false;
	}
	
	public boolean construct(int width, int height) {
		return doConstruct(width, height);
	}
	
	public boolean construct() {
		return doConstruct(0, 0);
	}
	
	public boolean construct(String title, int width, int height) {
		this.title = title;
		
		return doConstruct(width, height);
	}
	
	public boolean construct(String title) {
		this.title = title;
		
		return doConstruct(0, 0);
	}
	
	public void start() {
		frame.display();
		loop.start();
	}
	
	public void render() {
		frame.repaint();
	}
	
	public void addMouseListener(MouseListener mouseListener) {
		this.customMouseListener = mouseListener;
		
		if (constructed) {
			frame.addMouseListener(this.customMouseListener);
		} 
	}
	
	public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
		this.customMouseMotionListener = mouseMotionListener;
		
		if (constructed) {
			frame.addMouseMotionListener(this.customMouseMotionListener);
		}
	}
	
	public abstract void onUserCreate();
	
	public abstract void onUserUpdate();
	
	public abstract void onUserDraw(Graphics2D g);
	
	private boolean doConstruct(int width, int height) {
		if (constructed) {	
			return true;
		} 
		
		boolean fullScreen = (width == 0 && height == 0) ? true:false;
		
		try {			
			frame = new GameFrame(this, title, width, height, fullScreen);
			
			onUserCreate();
			
			if (customMouseListener != null)
				frame.addMouseListener(customMouseListener);
			if (customMouseMotionListener != null) 
				frame.addMouseMotionListener(customMouseMotionListener);
			
			frame.addKeyListener(keys);
			
			loop = new GameLoop(this);
			
			constructed = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
