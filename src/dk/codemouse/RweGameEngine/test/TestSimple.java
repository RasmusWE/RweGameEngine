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
	public void onCreate() {
		useAntiAliasing(true);
		frame.setResizable(true);
		
		//setFont(new Font("TimesRoman", Font.PLAIN, 20));
	}

	@Override
	public void onDraw(Graphics2D g) {
		clearScreen(g, Color.cyan);
		
		drawString(g, "Hello world!", 20, 20, Color.BLACK);
		
		for (int i = 0; i < 10; i++) {
			Color drawColor = Color.RED;
			if (i % 2 == 0)
				drawColor = Color.BLUE;
			
			draw(g, 0, i, drawColor);
		}
		
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
		
		drawTriangle(g, 120, 20, 75, 40, 110, 110, Color.WHITE);
		
		fillTriangle(g, 40, 60, 60, 30, 75, 70, Color.WHITE);
		
		drawRect(g, x - 5, y - 5, 10, 10, Color.BLACK);
		drawCircle(g, x, y, 10, Color.BLACK);
	}

	@Override
	public void onUpdate(double elapsedTime) {
		if (keyHeld(KeyEvent.VK_RIGHT))
			x += 1;
		
		if (keyHeld(KeyEvent.VK_LEFT))
			x -= 1;
		
		if (keyHeld(KeyEvent.VK_DOWN))
			y += 1;
			
		if (keyHeld(KeyEvent.VK_UP))
			y -= 1;
		
		if (keyPressed(KeyEvent.VK_ENTER)) 
			System.out.println("PRESSED ENTER!");
		
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
			tryStop();
		}
	}
	
	public boolean onDestroy() {
		return false;
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