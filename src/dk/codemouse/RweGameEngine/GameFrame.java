package dk.codemouse.RweGameEngine;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {
	
	public GameScreen screen;

	public Dimension frameDimension;
	public Insets insets;
	public boolean fullScreen;
	
	private String frameTitle;
	
	private GameEngine engine;
	
	public GameFrame(GameEngine engine, String title, int width, int height, boolean fullScreen, boolean fitScreenToPixel) {
		super(title);
		
		frameTitle = title;
		
		this.engine = engine;
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);

		if (fullScreen) {
			this.setUndecorated(true);
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			fullScreen = true;
			
			frameDimension = Toolkit.getDefaultToolkit().getScreenSize();
		} else {
			if (fitScreenToPixel) {
				width *= GameEngine.PIXEL_SIZE;
				height *= GameEngine.PIXEL_SIZE;
			}
			
			frameDimension = new Dimension(width, height);
		}

		prepareGameScreen();
		
		this.pack();
		this.setLocationRelativeTo(null);
		
		insets = this.getInsets();
		
		this.getContentPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resizeWindow(e.getComponent().getWidth(), e.getComponent().getHeight());
			}

			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
		});
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				engine.tryStop();
			}		
		});
	}
	
	public void onUserDraw(Graphics2D g) {
		engine.onDraw(g);
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

	public boolean useAntiAliasing() {
		return engine.useAntiAliasing();
	}
	
	public void resizeWindow(int width, int height) {
		screen.resizeWindow(width, height);
		this.pack();
	}
	
	private void prepareGameScreen() {
		screen = new GameScreen(this);
		add(screen);
	}
}
