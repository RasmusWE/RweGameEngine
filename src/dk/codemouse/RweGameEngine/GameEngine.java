
package dk.codemouse.RweGameEngine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.SwingUtilities;

/**
 * 
 * Simple "Game engine" wrapper - Inspired from OneLoneCoders Console and Pixel GameEngines in C++.
 * 
 * Simply extend this class and implement the onUserCreate, onUserUpdate and onUserDraw to utilize the window and internal game loop.
 * 
 * You are encouraged to use the drawing functions specifically designed for this engine - But you can also use std. functions in Graphics2D.
 * Not that if you use the std. functions from Graphics2D changing of pixel size will have no effect.
 * 
 * Note if you change pixel size the default behaviour is that the width and height is affected as you choose a width of n pixels and height of n pixels.
 * You can choose not to have this behaviour by specifying "fitScreenToPixel = false" in the "construct" function.
 * This will make it so the width and height you specify is in normal screen pixel dimensions (Everything else still behaves with specified pixel size).
 * 
 * Note that the original OlcPixelGameEngine uses no anti-aliasing and the default behavior in this engine is also emulating this
 * though you can turn on anti-aliasing with "useAntiAliasing(boolean toggle)". 
 * 
 * Mouse position and key controls are available out-of-the-box. Easily add custom listeners.
 * Access mouse position with GameEngine.MouseX, GameEngine.MouseY.
 * Easily add key controls like so:
 * 
 *		if (keyReleased(KeyEvent.VK_ESCAPE)) {
 *			System.exit(0);
 *		}
 * 
 * Access JFrame and JPanel screen directly to do this. For example you can also change frame properties like so: 
 * frame.setResizable(false);
 * 
 * @author Rasmus Wehlast Engelbrecht
 * @version 1.2
 * 
 */

public abstract class GameEngine {
	
	protected String title = "RweGameEngine";
	
	protected GameFrame frame;

	public static int PIXEL_SIZE = 1;
	
	public static int TFPS = 60; //Target FPS (FPS CAP)
	public static int TUPS = 60;  //Target Updates - lowering or increasing this will make the engine update logic more or less often
	public static int CURRENT_FPS  = 0;
	public static int CURRENT_UPS  = 0;
	
	public static int MouseX = 0;
	public static int MouseY = 0;
	
	public Random random = new Random();
	public GameKeys keys = new GameKeys();
	
	private GameLoop loop;
	private GameGraphics gameGraphics;
	
	private MouseListener customMouseListener;
	private MouseMotionListener customMouseMotionListener;
	
	private boolean constructed = false, fitScreenToPixel = true;

	public int screenWidth() {
		return frame.frameDimension.width / GameEngine.PIXEL_SIZE;
	}
	
	public int screenHeight() {
		return frame.frameDimension.height / GameEngine.PIXEL_SIZE;
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
	
	public boolean construct() {
		return doConstruct(0, 0, PIXEL_SIZE);
	}
	
	public boolean construct(int pixelSize) {
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(String title) {
		this.title = title;
		
		return doConstruct(0, 0, PIXEL_SIZE);
	}
	
	public boolean construct(String title, int pixelSize) {
		this.title = title;
		
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(int width, int height) {
		return doConstruct(width, height, PIXEL_SIZE);
	}
	
	public boolean construct(int width, int height, int pixelSize) {
		return doConstruct(width, height, pixelSize);
	}
	
	public boolean construct(int width, int height, int pixelSize, boolean fitScreenToPixel) {
		this.fitScreenToPixel = fitScreenToPixel;
		
		return doConstruct(width, height, pixelSize);
	}
	
	public boolean construct(String title, int width, int height) {
		this.title = title;
		
		return doConstruct(width, height, PIXEL_SIZE);
	}
	
	public boolean construct(String title, int width, int height, int pixelSize) {
		this.title = title;
		
		return doConstruct(width, height, pixelSize);
	}
	
	public boolean construct(String title, int width, int height, int pixelSize, boolean fitScreenToPixel) {
		this.title = title;
		this.fitScreenToPixel = fitScreenToPixel;
		
		return doConstruct(width, height, pixelSize);
	}
	
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.display();
			}
		});

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
	
	public void sendFpsToFrame(int fps) {
		frame.setFPS(fps);
	}
	
	public abstract void onUserCreate();
	
	public abstract void onUserUpdate();
	
	public abstract void onUserDraw(Graphics2D g);
	
	public boolean useAntiAliasing() {
		return gameGraphics.useAntiAliasing();
	}
	
	public void useAntiAliasing(boolean tof) {
		gameGraphics.useAntiAliasing(tof);
	}
	
	public void clearScreen(Graphics2D g, Color color) {
		gameGraphics.clearScreen(g, color);
	}
	
	public void draw(Graphics2D g, int x, int y, Color color) {
		gameGraphics.draw(g, x, y, color);
	}
	
	public void drawLine(Graphics2D g, int x1, int x2, int y1, int y2, Color color) {
		gameGraphics.drawLine(g, x1, x2, y1, y2, color);
	}
	
	public void drawTriangle(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		gameGraphics.drawTriangle(g, x1, y1, x2, y2, x3, y3, color);
	}
	
	public void fillTriangle(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		gameGraphics.fillTriangle(g, x1, y1, x2, y2, x3, y3, color);
	}
	
	public void drawCircle(Graphics2D g, int xCenter, int yCenter, int radius, Color color) {
		gameGraphics.drawCircle(g, xCenter, yCenter, radius, color);
	}
	
	public void fillCircle(Graphics2D g, int xCenter, int yCenter, int radius, Color color) {
		gameGraphics.fillCircle(g, xCenter, yCenter, radius, color);
	}
	
	public void drawRect(Graphics2D g, int x, int y, int w, int h, Color color) {
		gameGraphics.drawRect(g, x, y, w, h, color);
	}
	
	public void fillRect(Graphics2D g, int x, int y, int w, int h, Color color) {
		gameGraphics.fillRect(g, x, y, w, h, color);
	}
	
	public void drawString(Graphics2D g, String string, int x, int y, Color color) {
		gameGraphics.drawString(g, string, x, y, color);
	}
	
	public void setFont(Font font) {
		gameGraphics.setFont(font);
	}
	
	private boolean doConstruct(int width, int height, int pixelSize) {
		if (constructed) {
			System.err.println("GameEngine already constructed!");
			return false;
		} 

		boolean fullScreen = (width == 0 && height == 0) ? true:false;

		GameEngine.PIXEL_SIZE = pixelSize > 1 ? pixelSize:1;

		try {			
			frame = new GameFrame(this, title, width, height, fullScreen, fitScreenToPixel);		
			gameGraphics = new GameGraphics(this, pixelSize, fullScreen);
			
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
