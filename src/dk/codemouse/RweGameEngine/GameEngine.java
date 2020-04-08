
package dk.codemouse.RweGameEngine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.SwingUtilities;

/**
 * 
 * Simple "Game engine" wrapper - Inspired from OneLoneCoders Console and Pixel GameEngines in C++.
 * 
 * Simply extend this class and implement the onCreate, onUpdate and onDraw to utilize the window and internal game loop.
 * Optionally use onDestroy to handle closing of application or potentially stop the application from closing by returning true.
 * (Default is false and application will close normally when user tries closing window or function tryStop() is called). 
 * 
 * You are encouraged to use the drawing functions specifically designed for this engine - But you can also use std. functions in Graphics2D.
 * Note that if you use the std. functions from Graphics2D changing of pixel size will have no effect.
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
 *		if (keyReleased(KeyEvent.VK_ESCAPE)) {      if (keyHeld(KeyEvent.VK_RIGHT)) {      	if (keyPressed(KeyEvent.VK_ENTER)) 
 *			tryStop();									x++;									System.Out.println("ENTER");
 *		}											}										}
 * 
 * You can also access JFrame and JPanel screen directly. For example you can change frame properties like so: 
 * frame.setResizable(false);
 * 
 * @author Rasmus Wehlast Engelbrecht
 * @version 1.4
 * 
 */

public abstract class GameEngine {
	
	protected String title = "RweGameEngine";
	
	protected GameFrame frame;
	
	public static int TFPS 				= 60; 	//Target FPS (FPS CAP)
    public static int TUPS 				= 60; 	//Target Updates - lowering or increasing this will make the engine update logic more or less often
	public static int CURRENT_FPS  		= 0;
	public static int CURRENT_UPS  		= 0;
	public static double T_ELAPSED_TIME = 0;	//Total elapsed game time
	
	public static int MouseX = 0;
	public static int MouseY = 0;
	
	public Random random = new Random();
	public GameKeys keys = new GameKeys();
	
	private GameLoop loop;
	private GameGraphics gameGraphics;
	
	private static int pixelSize = 1;
	
	private MouseListener customMouseListener;
	private MouseMotionListener customMouseMotionListener;
	
	private boolean constructed = false, fitScreenToPixel = true;

	public abstract void onCreate();
	
	public abstract void onUpdate(double elapsedTime);
	
	public abstract void onDraw(Graphics2D g);
	
	public abstract boolean onDestroy(); 
	
	public int screenWidth() {
		return frame.frameDimension.width / GameEngine.pixelSize;
	}
	
	public int screenHeight() {
		return frame.frameDimension.height / GameEngine.pixelSize;
	}
	
	public boolean keyPressed(int keyCode) {
		Object pressed = keys.keyPressed.get(keyCode);
		if (pressed != null)
			return (boolean) pressed;
		
		return false;
	}
	
	public boolean keyHeld(int keyCode) {
		Object held = keys.keyHeld.get(keyCode);
		if (held != null)
			return (boolean) held;
		
		return false;
	}
	
	public boolean keyReleased(int keyCode) {
		Object released = keys.keyReleased.get(keyCode);
		if (released != null)
			return (boolean) released;
		
		return false;
	}
	
	public boolean construct() {
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(int pixelSize) {
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(String title) {
		this.title = title;
		
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(String title, int pixelSize) {
		this.title = title;
		
		return doConstruct(0, 0, pixelSize);
	}
	
	public boolean construct(int width, int height) {
		return doConstruct(width, height, pixelSize);
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
		
		return doConstruct(width, height, pixelSize);
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
	
	public boolean tryStop() {
		if (!onDestroy()) {
			frame.setVisible(false);
			frame.dispose();
			loop.stop();
		}
		
		return false;
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
	
	public boolean useAntiAliasing() {
		return gameGraphics.useAntiAliasing();
	}
	
	public void useAntiAliasing(boolean tof) {
		gameGraphics.useAntiAliasing(tof);
	}
	
	public void clearScreen(Graphics2D g, Color color) {
		gameGraphics.clearScreen(g, color);
	}
	
	public void draw(Graphics2D g, float x, float y, Color color) {
		gameGraphics.draw(g, x, y, color);
	}
	
	public void drawLine(Graphics2D g, int x1, int y1, int x2, int y2, Color color) {
		gameGraphics.drawLine(g, x1, y1, x2, y2, color);
	}
	
	public void drawTriangle(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		gameGraphics.drawTriangle(g, x1, y1, x2, y2, x3, y3, color);
	}
	
	public void fillTriangle(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		gameGraphics.fillTriangle(g, x1, y1, x2, y2, x3, y3, color);
	}
	
	public void drawCircle(Graphics2D g, float xCenter, float yCenter, float radius, Color color) {
		gameGraphics.drawCircle(g, xCenter, yCenter, radius, color);
	}
	
	public void fillCircle(Graphics2D g, float xCenter, float yCenter, float radius, Color color) {
		gameGraphics.fillCircle(g, xCenter, yCenter, radius, color);
	}
	
	public void drawRect(Graphics2D g, float x, float y, int w, int h, Color color) {
		gameGraphics.drawRect(g, x, y, w, h, color);
	}
	
	public void fillRect(Graphics2D g, float x, float y, int w, int h, Color color) {
		gameGraphics.fillRect(g, x, y, w, h, color);
	}
	
	public void drawString(Graphics2D g, String string, float x, float y, Color color) {
		gameGraphics.drawString(g, string, x, y, color);
	}
	
	public void drawPolygon(Graphics2D g, ArrayList<Pair<Float>> modelCoordinates, float x, float y, float angle, Color color) {
		gameGraphics.drawPolygon(g, modelCoordinates, x, y, angle, 1.0f, color);
	}
	
	public void drawPolygon(Graphics2D g, ArrayList<Pair<Float>> modelCoordinates, float x, float y, float angle, float scale, Color color) {
		gameGraphics.drawPolygon(g, modelCoordinates, x, y, angle, scale, color);
	}
	
	public void setFont(Font font) {
		gameGraphics.setFont(font);
	}
	
	public void setFontSize(int size) {
		gameGraphics.setFontSize(size);
	}
	
	public static int getPixelSize() {
		return pixelSize;
	}
	
	public Pair<Float> screenWrapCoordinates(float x, float y) {
		Pair<Float> pair = new Pair<>(x, y);

		if (pair.first < 0.0f)
			pair.first = pair.first + screenWidth();
		else if (pair.first >= screenWidth())
			pair.first = pair.first - screenWidth();
		
		if (pair.last < 0.0f)
			pair.last = pair.last + screenHeight();
		else if (pair.last >= screenHeight())
			pair.last = pair.last - screenHeight();

		return pair;
	}
	
	private boolean doConstruct(int width, int height, int pixelSize) {
		if (constructed) {
			System.err.println("GameEngine already constructed!");
			return false;
		} 

		boolean fullScreen = (width == 0 && height == 0) ? true:false;

		GameEngine.pixelSize = pixelSize > 1 ? pixelSize:1;

		try {			
			frame = new GameFrame(this, title, width, height, fullScreen, fitScreenToPixel);	
			
			gameGraphics = new GameGraphics(this, fullScreen);
			pixelSize = gameGraphics.getGPixelSize();
			
			onCreate();

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
