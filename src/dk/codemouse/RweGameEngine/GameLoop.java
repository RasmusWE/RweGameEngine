package dk.codemouse.RweGameEngine;

import java.time.Duration;
import java.time.LocalTime;

public class GameLoop {
	
	private int ticks, frames;
	private final double MAX_DT = 1 / 60.0;

	private GameEngine engine;
	private Thread loopThread;

	private volatile boolean running;
	
	public GameLoop(GameEngine engine) {
		this.engine = engine;
	}
	
	public void start() {
		loopThread = new Thread(() -> gameLoop());
		loopThread.start();
	}
	
	public void stop() {
		running = false;
		
		/*
		try {
			loopThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}

	private void gameLoop() {
		running = true;
		
		frames = 0;
		ticks = 0;

		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / GameEngine.TUPS;
		final double timeF = 1000000000 / GameEngine.TFPS;
		double deltaU = 0, deltaF = 0;
		
		long timer  = System.currentTimeMillis();
		LocalTime time = LocalTime.now();
		
		while (running) {
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaU >= 1) {
				LocalTime timeNow = LocalTime.now();
				Duration elapsed = Duration.between(time, timeNow);
				double elapsedSeconds = (double) elapsed.toMillis() / 1000;
				time = timeNow;

				double currDt = Math.min(elapsedSeconds, MAX_DT);
				GameEngine.T_ELAPSED_TIME += currDt;
				
				update(currDt);
				input();	
				
				ticks++;
				deltaU--;
			}

			if (deltaF >= 1 || GameEngine.TFPS == -1) {
				render();
				
				frames++;
				deltaF--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				GameEngine.CURRENT_FPS = frames;
				GameEngine.CURRENT_UPS = ticks;
				
				frames = 0;
				ticks = 0;
				timer += 1000;
			}
		}
	}
	
	private void input() {
		engine.keys.resetKeys();
	}
	
	private void update(double elapsedTime) {
		engine.onUpdate(elapsedTime);
		engine.onUpdateScene(elapsedTime);
		
		engine.sendFpsToFrame(GameEngine.CURRENT_FPS);
	}
	
	private void render() {
		engine.render();
	}

}
