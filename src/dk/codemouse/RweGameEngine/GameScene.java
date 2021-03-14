package dk.codemouse.RweGameEngine;

import java.awt.Graphics2D;

public abstract class GameScene {
	
	public String title;	
	protected GameEngine engine;
	
	public GameScene(GameEngine engine, String title) {
		this.engine = engine;
		this.title  = title;

		this.engine.addGameScene(this);
	}
	
	public abstract void onDisplay();
	
	public abstract void onUpdate(double elapsedTime);
	
	public abstract void onDraw(Graphics2D g);
	
	public abstract void onClear(); 
	
}
