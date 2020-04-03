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
		useAntiAliasing(false);
		frame.setResizable(true);
		
		//setFont(new Font("TimesRoman", Font.PLAIN, 20));
	}

	@Override
	public void onUserDraw(Graphics2D g) {
		drawRect(g, x - 5, y - 5, 10, 10, Color.BLACK);
		drawCircle(g, x, y, 10, Color.BLACK);

		drawString(g, "Hello world!", 1, 20, Color.BLACK);
		
		draw(g, 0, 0, Color.RED);
		draw(g, 0, 1, Color.BLUE);
		draw(g, 0, 2, Color.RED);
		draw(g, 0, 3, Color.BLUE);
		draw(g, 0, 4, Color.RED);
		draw(g, 0, 5, Color.BLUE);
		draw(g, 0, 6, Color.RED);
		draw(g, 0, 7, Color.BLUE);
		draw(g, 0, 8, Color.RED);
		draw(g, 0, 9, Color.BLUE);
		draw(g, 0, 10, Color.RED);
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
		
		//Exit
		if (keyReleased(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		TestSimple game = new TestSimple();
		if (game.construct(180, 120, 4)) {
			game.start();
		} else {
			System.err.println("Error occured during construction");
		}
	}

}