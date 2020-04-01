package dk.codemouse.RweGameEngine.test;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.SwingUtilities;

import dk.codemouse.RweGameEngine.GameEngine;

public class TestCircles extends GameEngine {

	class Ball {
		float px, py;
		float vx, vy;
		float ax, ay;
		float radius;
		float mass;
		
		int id;
	}
	
	class CollidingPair {
		Ball ball1, ball2;
		float distance;
		public CollidingPair(Ball ball1, Ball ball2, float fDistance) {
			this.ball1 = ball1;
			this.ball2 = ball2;
			this.distance = fDistance;
		}
	}
	
	Random random = new Random();
	
	private ArrayList<Ball> balls = new ArrayList<>();
	private ArrayList<CollidingPair> collidingPairs = new ArrayList<>();
	private Ball selectedBall = null;
	
	public void addBall(float px, float py, float radius) {
		Ball newBall = new Ball();
		newBall.px = px;
		newBall.py = py;
		newBall.vx = 0;
		newBall.vy = 0;
		newBall.ax = 0;
		newBall.ay = 0;
		
		newBall.radius = radius;
		
		newBall.mass = radius * 10.0f;
		
		newBall.id = balls.size() + 1;
		
		balls.add(newBall);
	}
	
	@Override
	public void onUserCreate() {
		//Setup mouse input
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				GameEngine.MouseX = e.getX();
				GameEngine.MouseY = e.getY();
				
				if (selectedBall != null) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						selectedBall.px = e.getX();
						selectedBall.py = e.getY();
					}
				} else {
					for (Ball ball : balls) {
						if (isPointInCircle(ball.px, ball.py, ball.radius, GameEngine.MouseX, GameEngine.MouseY)) {
							selectedBall = ball;
							break;
						}
					}	
				}	
			}
		});
	
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					if (selectedBall != null) {
						//Apply velocity
						selectedBall.vx = 0.05f * ((selectedBall.px) - (float) GameEngine.MouseX);
						selectedBall.vy = 0.05f * ((selectedBall.py) - (float) GameEngine.MouseY);
					}
				}
				
				selectedBall = null;
			}
		});
		
		//Setup balls
//		float defaultRadius = 25.0f;
//		addBall(screenWidth() * 0.25f, screenHeight() * 0.5f, defaultRadius);
//		addBall(screenWidth() * 0.75f, screenHeight() * 0.5f, defaultRadius);
		
		for (int i = 0; i < 15; i++) 
			addBall(random.nextInt(10000) % screenWidth(), random.nextInt(10000) % screenHeight(), random.nextInt(10000) % 86 + 2);
	}

	@Override
	public void onUserUpdate() {
		//Update ball positions
		for (Ball ball : balls) {
			
			ball.ax = -ball.vx * 0.008f;
			ball.ay = -ball.vy * 0.008f;
			ball.vx += ball.ax;
			ball.vy += ball.ay;
			ball.px += ball.vx;
			ball.py += ball.vy;
			
			if (ball.px < 0) ball.px += (float) screenWidth();
			if (ball.px > screenWidth()) ball.px -= (float) screenWidth();
			if (ball.py < 0) ball.py += (float) screenHeight();
			if (ball.py >= screenHeight()) ball.py -= (float) screenHeight();
			
			if (Math.abs(ball.vx * ball.vx + ball.vy * ball.vy) < 0.1f) {
				ball.vx = 0;
				ball.vy = 0;
			}
		}
		
		//Determine collisions
		for (Ball ball : balls) {
			for (Ball target : balls) {
				if (ball.id == target.id) 
					continue;
				
				if (doCirclesOverlap(ball.px, ball.py, ball.radius, target.px, target.py, target.radius)) {					
					//Distance between ball centers
					float fDistance = (float) Math.sqrt((ball.px - target.px) * (ball.px - target.px) + (ball.py - target.py) * (ball.py - target.py));
					
					//Add the colliding pairs to the list
					collidingPairs.add(new CollidingPair(ball, target, fDistance));
					
					//Calculate displacement required
					float fOverlap = 0.5f * (fDistance - ball.radius - target.radius);
					
					//Displace current ball
					ball.px -= fOverlap * (ball.px - target.px) / fDistance;
					ball.py -= fOverlap * (ball.py - target.py) / fDistance;
					
					//Displace target ball
					target.px += fOverlap * (ball.px - target.px) / fDistance;
					target.py += fOverlap * (ball.py - target.py) / fDistance;
				}
			}
		}
		
		//Now work out dynamic collisions
		for (CollidingPair collidingPair : collidingPairs) {
			Ball b1 = collidingPair.ball1;
			Ball b2 = collidingPair.ball2;
			
			//Normal
			float nx = (b2.px - b1.px) / collidingPair.distance;
			float ny = (b2.py - b1.py) / collidingPair.distance;
			
			//Tangent
			float tx = -ny;
			float ty = nx;
			
			//Dot product tangent
			float dpTan1 = b1.vx * tx + b1.vy * ty;
			float dpTan2 = b2.vx * tx + b2.vy * ty;
			
			//Dot product normal
			float dpNorm1 = b1.vx * nx + b1.vy * ny;
			float dpNorm2 = b2.vx * nx + b2.vy * ny;
			
			//Conservation of momentum in 1D
			float m1 = (dpNorm1 * (b1.mass - b2.mass) + 1.0f * b2.mass * dpNorm2) / (b1.mass + b2.mass);
			float m2 = (dpNorm2 * (b2.mass - b1.mass) + 1.0f * b1.mass * dpNorm1) / (b1.mass + b2.mass);
			
			b1.vx = tx * dpTan1 + nx * m1;
			b1.vy = ty * dpTan1 + ny * m1;
			b2.vx = tx * dpTan2 + nx * m2;
			b2.vy = ty * dpTan2 + ny * m2;
		}
	}
	
	@Override
	public void onUserDraw(Graphics2D g) {
		//Clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, screenWidth(), screenHeight());
		
		
		//Draw balls
		g.setColor(Color.BLACK);
		for (Ball ball : balls) {
			//x and y in drawOval is not the center of the oval but rather the top left point
			int x = (int) (ball.px - ball.radius);
			int y = (int) (ball.py - ball.radius);
			g.drawOval(x, y, (int) ball.radius * 2, (int) ball.radius * 2);
		}
		
		//Draw collision lines
		g.setColor(Color.RED);
		for (CollidingPair collidingPair : collidingPairs) {
			g.drawLine((int) collidingPair.ball1.px, (int) collidingPair.ball1.py, (int) collidingPair.ball2.px, (int) collidingPair.ball2.py);
		}
		
		//Clear collidingPairs list since we don't have a single update function scope as ONL has
		//so the list is placed in scope of entire CirclesTest class and cleared when the pairs has been used here in draw
		collidingPairs.clear();
		
		//Draw cue
		if (selectedBall != null) {
			g.setColor(Color.BLUE);
			g.drawLine((int) selectedBall.px, (int) selectedBall.py, GameEngine.MouseX, GameEngine.MouseY);
		}
	}
	
	private boolean doCirclesOverlap(float x1, float y1, float r1, float x2, float y2, float r2) {
		return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) <= (r1 + r2) * (r1 + r2);	
	}
	
	private boolean isPointInCircle(float x1, float y1, float r1, float px, float py) {
		return Math.abs((x1 - px) * (x1 - px) + (y1 - py) * (y1 - py)) < (r1 * r1);	
	}

	public static void main(String[] args) {
		TestCircles circlesTest = new TestCircles();
		if (circlesTest.construct("Circle Test - OLC", 800, 600)) {
			circlesTest.start();
		} else {
			System.err.println("Error constructing engine");
		}
	}
	
}

