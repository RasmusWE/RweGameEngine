package dk.codemouse.RweGameEngine;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	private String frameTitle;
	
	private GameEngine engine;
	
	public GameScreen screen;

	public Dimension frameDimension;
	public boolean fullScreen;
	
	public GameFrame(GameEngine engine, String title, int width, int height, boolean fullScreen) {
		super(title);
		
		frameTitle = title;
		
		this.engine = engine;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		if (fullScreen) {
			this.setUndecorated(true);
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			fullScreen = true;
			
			frameDimension = Toolkit.getDefaultToolkit().getScreenSize();
		} else {
			width *= GameEngine.PIXEL_SIZE;
			height *= GameEngine.PIXEL_SIZE;
			
			frameDimension = new Dimension(width, height);
		}

		prepareGameScreen();
		
		this.pack();
		this.setLocationRelativeTo(null);

		this.getContentPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resizeWindow(e.getComponent().getWidth(), e.getComponent().getHeight());
			}

			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
		});
	}
	
	public void onUserDraw(Graphics2D g) {
		engine.onUserDraw(g);
	}

	public void display() {
		setVisible(true);
	}
	
	public void addMouseMotionListener(MouseMotionListener listener) {
		screen.addMouseMotionListener(listener);
	}
	
	public void addMouseListener(MouseListener listener) {
		screen.addMouseListener(listener);
	}
	
	public void setFPS(int fps) {
		this.setTitle(frameTitle + " - FPS: " + Integer.toString(fps));
	}

	private void prepareGameScreen() {
		screen = new GameScreen(this);
		add(screen);
	}

	public void resizeWindow(int width, int height) {
		screen.resizeWindow(width, height);
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
