package dk.codemouse.RweGameEngine.examples;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dk.codemouse.RweGameEngine.GameEngine;
import dk.codemouse.RweGameEngine.GameScene;
import dk.codemouse.RweGameEngine.Pair;

public class ExampleGameAsteroids extends GameEngine{
	
	final boolean ANTI_ALIASING = false;
	
	private ArrayList<SpaceObject> asteroids;
	private ArrayList<SpaceObject> bullets;
	private SpaceObject player;
	
	private ArrayList<Pair<Float>> modelShip = new ArrayList<>();

	private int scale, astVerts = 20, score = 0, level = 1;
	private boolean playerDied = false, started = false, paused = false;
	
	private SpaceObject createAsteroid(float x, float y, float vx, float vy, int size, float angle) {
		SpaceObject asteroid = new SpaceObject();
		asteroid.x 	= x;
		asteroid.y 	= y;
		asteroid.vx = vx;
		asteroid.vy = vy;
		
		asteroid.size = size;
		
		//init asteroid model
		for (int i = 0; i < astVerts; i++) {
			float radius = (float) (random.nextInt(10000) / (50000 * 1.2) + 0.9f);
			float a = (float) (((float) i / (float) astVerts) * 6.28318f);

			asteroid.model.add(new Pair<Float>((float) (radius * Math.sin(a)), (float) (radius * Math.cos(a))));
		}
		
		return asteroid;
	}
	
	private void init(boolean resetScore) {
		playerDied = false;
		
		if (resetScore) {
			score = 0;
			level = 1;
		}
		
		asteroids = new ArrayList<>();
		bullets   = new ArrayList<>();

		//init asteroids
		for (int i = 0; i < random.nextInt(2) + 2; i++) {
			asteroids.add(createAsteroid(random.nextInt(screenWidth()), random.nextInt(screenHeight()), random.nextInt(20) - 10, random.nextInt(20) - 10, random.nextInt(16) + 8, 0.0f));
		}

		//init player
		if (resetScore) {
			player = new SpaceObject();
			player.x = screenWidth() / 2.0f;
			player.y = screenHeight() / 2.0f;
			player.angle = 0.0f;
		}
		
		player.vx = 0.0f;
		player.vy = 0.0f;
	}
	
	@Override
	public void onCreate() {
		useAntiAliasing(ANTI_ALIASING);
		
		new StartScene(this, "start", true);

		scale = (screenWidth() / 200);
		if (scale <= 0)
			scale = 1;

		//init player model
		modelShip.add(new Pair<Float>(0.0f, -5.0f));
		modelShip.add(new Pair<Float>(-2.5f, 2.5f));
		modelShip.add(new Pair<Float>(2.5f, 2.5f));
		
		init(true);
	}

	@Override
	public void onUpdate(double elapsedTime) {	
		//Keys
		if (keyPressed(KeyEvent.VK_ESCAPE))
			tryStop();
		
		if (keyPressed(KeyEvent.VK_P)) {
			setScene("start");
			paused = true;
		}
		
		if (started && !paused) {
			//Reset
			if (playerDied) {
				init(true);
			}
			
			if (keyReleased(KeyEvent.VK_SPACE)) {
				SpaceObject bullet = new SpaceObject();
				bullet.x  = player.x;
				bullet.y  = player.y;
				bullet.vx = (float) (50.0f * Math.sin(player.angle));
				bullet.vy = (float) (-50.0f * Math.cos(player.angle));
				
				bullet.size = 0;
				
				bullets.add(bullet);
			}
			
			//Update asteroids
			int rIdx = 0;
			SpaceObject[] or = new SpaceObject[asteroids.size()];
			ArrayList<SpaceObject> newAsteroids = new ArrayList<SpaceObject>();
			for (SpaceObject a : asteroids) {
				a.x += a.vx * scale * elapsedTime;
				a.y += a.vy * scale * elapsedTime;
				a.angle += (a.vx + a.vy) * 0.05f * elapsedTime;

				Pair<Float> wc = screenWrapCoordinates(a.x, a.y);
				a.x = wc.first;
				a.y = wc.last;
				
				for (SpaceObject b : bullets) {
					if (a.containsPoint(b.x, b.y)) {
						b.x = -200;
						
						if (a.size > 4) {
							float angle1 = (float) (random.nextFloat() * (Math.PI * 2));
							float angle2 = (float) (random.nextFloat() * (Math.PI * 2));
							
							SpaceObject asteroid = createAsteroid(a.x, a.y, 
									(float) ((random.nextInt(10) + 10) * Math.sin(angle1)), 
									(float) ((random.nextInt(10) + 10) * Math.cos(angle1)),
									a.size / 2, 0.0f);

							asteroid.size  += random.nextInt(3);
							if (asteroid.size < 4)
								asteroid.size = 4;
							
							newAsteroids.add(asteroid);
							
							asteroid = createAsteroid(a.x, a.y, 
									(float) ((random.nextInt(10) + 10) * Math.sin(angle2)), 
									(float) ((random.nextInt(10) + 10) * Math.cos(angle2)),
									a.size / 2, 0.0f);

							asteroid.size  += random.nextInt(3);
							if (asteroid.size < 4)
								asteroid.size = 4;
							
							newAsteroids.add(asteroid);
						}
						
						or[rIdx] = a;
						rIdx++;
					}
				}
				
				if (a.containsPoint(player.x, player.y))
					playerDied = true;
			}
			
			//Remove "dead" asteroids
			for (int i = 0; i < rIdx; i++) {
				asteroids.remove(or[i]);
				score++;
			}
			
			//Append new asteroids
		    for (SpaceObject a : newAsteroids) 
		    	asteroids.add(a);
			
			//Calc bullets positions
		    rIdx = 0;
			or = new SpaceObject[bullets.size()];
			for (SpaceObject b : bullets) {
				b.x += b.vx * scale * elapsedTime;
				b.y += b.vy * scale * elapsedTime;
				
				if (b.x < 1 || b.y < 1 || b.x > screenWidth() || b.y > screenHeight()) {
					or[rIdx] = b;
					rIdx++;
				}
			}
			
			//Remove "dead" bullets
			for (int i = 0; i < rIdx; i++)
				bullets.remove(or[i]);
			
			//Steering of player
			
			//Angle
			if (keyHeld(KeyEvent.VK_LEFT))
				player.angle -= 5.0f * elapsedTime;		
			if (keyHeld(KeyEvent.VK_RIGHT))
				player.angle += 5.0f * elapsedTime;
			
			//Thrust
			if (keyHeld(KeyEvent.VK_UP)) {
				player.vx += Math.sin(player.angle) * 20.0f * scale * elapsedTime;
				player.vy += -Math.cos(player.angle) * 20.0f * scale * elapsedTime;
			}
			
			if (keyHeld(KeyEvent.VK_DOWN)) {
				player.vx -= Math.sin(player.angle) * 20.0f * scale * elapsedTime;
				player.vy -= -Math.cos(player.angle) * 20.0f * scale * elapsedTime;
			}	
			
			//Position
			player.x += player.vx * elapsedTime;
			player.y += player.vy * elapsedTime;
			
			Pair<Float> wc = screenWrapCoordinates(player.x, player.y);
			player.x = wc.first;
			player.y = wc.last;
			
			//Level complete?
			if (asteroids.isEmpty()) {
				score += 1000;
				level++;
				init(false);
			}	
		}
	}

	@Override
	public void onDraw(Graphics2D g) {
		clearScreen(g, Color.BLACK);
		
		if (started) {			
			//Draw asteroids
			for (SpaceObject a : asteroids) {
				drawPolygon(g, a.model, a.x, a.y, a.angle, a.size * scale, Color.YELLOW);
			}
			
			//Draw bullets
			for (SpaceObject b : bullets) {
				if (scale > 1)
					drawCircle(g, b.x, b.y, 5, Color.white);
				else
					draw(g, b.x, b.y, Color.WHITE);
			}
			
			//Draw player
			drawPolygon(g, modelShip, player.x, player.y, player.angle, scale, Color.WHITE);
	
			//Draw score
			setFontSize(6);
			drawString(g, "Level: " + level, 2, 10 * scale, Color.WHITE);
			drawString(g, "Score: " + score, 2, 17 * scale, Color.WHITE);	
		}
	}

	@Override
	public boolean onDestroy() {
		return false;
	}
	
	public static void main(String[] args) {
		ExampleGameAsteroids game = new ExampleGameAsteroids();
		if (game.construct(275, 175, 3))
			game.start();
	}
	
	public void draw(Graphics2D g, float x, float y, Color color) {
		Pair<Float> wc = screenWrapCoordinates(x, y);
		super.draw(g, wc.first, wc.last, color);
	}
	
	private class SpaceObject {
		float x;
		float y;
		float vx;
		float vy;
		
		int size;
		float angle;
		
		ArrayList<Pair<Float>> model = new ArrayList<>();
		
		public boolean containsPoint(float x, float y) {
			float dx = (this.x - x) * (this.x - x);
			float dy = (this.y - y) * (this.y - y);
			double d = Math.sqrt(dx + dy);
			
			if (d < size * scale)
				return true;
			
			return false;
		}
	}

	private class StartScene extends GameScene {

		public StartScene(GameEngine engine, String title, boolean addToEngine) {
			super(engine, title, addToEngine);
			engine.setScene(title);
		}
		
		@Override
		public void onDisplay() {
		}

		@Override
		public void onUpdate(double elapsedTime) {
			if (keyReleased(KeyEvent.VK_ENTER)) {
				clearScene();
			}
		}

		@Override
		public void onDraw(Graphics2D g) {
			engine.setFontSize(25);
			engine.drawString(g, "Asteroids!", 65, 30, Color.yellow);
			engine.setFontSize(10);
			engine.drawString(g, "Press ENTER to start", 77, 100, Color.yellow);
			engine.drawString(g, "Press ESCAPE to end", 80, 125, Color.yellow);
			engine.drawString(g, "Press P to pause", 90, 150, Color.yellow);
		}

		@Override
		public void onClear() {
			started = true;
			paused = false;
		}
		
	}
	
}
