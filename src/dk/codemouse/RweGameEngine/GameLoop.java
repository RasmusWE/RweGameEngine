package dk.codemouse.RweGameEngine;

public class GameLoop {

	private boolean loopRunning = true;

	private int ticks, frames;

	private GameEngine engine;
	
	public GameLoop(GameEngine engine) {
		this.engine = engine;
	}
	
	public void start() {
		new Thread(() -> gameLoop()).start();
	}

	private void gameLoop() {
		frames = 0;
		ticks = 0;

		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / GameEngine.TUPS;
		final double timeF = 1000000000 / GameEngine.TFPS;
		double deltaU = 0, deltaF = 0;
		long timer = System.currentTimeMillis();

		while (loopRunning) {
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaU >= 1) {
				update();
				input();
				ticks++;
				deltaU--;
			}

			if (deltaF >= 1) {
				engine.render();
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
	
	private void update() {
		engine.onUserUpdate();
	}

}
