package dk.codemouse.RweGameEngine.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import dk.codemouse.RweGameEngine.GameEngine;

public class TestSimple extends GameEngine{
	
	int x = 10;
	int y = 10;
	int mX = 0;
	int mY = 0;
	
	@Override
	public void onUserCreate() {
		useAntiAliasing(true);
	}

	@Override
	public void onUserDraw(Graphics2D g) {
		drawRect(g, x - 5, y - 5, 10, 10, Color.BLACK);
		drawCircle(g, x, y, 10, Color.BLACK);
	}

	@Override
	public void onUserUpdate() {
		if (keyPressed(KeyEvent.VK_RIGHT))
			x += 1;
		
		if (keyPressed(KeyEvent.VK_LEFT))
			x -= 1;
		
		if (keyPressed(KeyEvent.VK_DOWN))
			y += 1;
			
		if (keyPressed(KeyEvent.VK_UP))
			y -= 1;
		
		if (mX != GameEngine.MouseX) {
			mX = GameEngine.MouseX;
			x = mX;
		}
		
		if (mY != GameEngine.MouseY) {
			mY = GameEngine.MouseY;
			y = mY;
		}
	}
	
	public static void main(String[] args) {
		TestSimple game = new TestSimple();
		if (game.construct(100, 75, 3)) {
			game.start();
		} else {
			System.err.println("Error occured during construction");
		}
	}

}
