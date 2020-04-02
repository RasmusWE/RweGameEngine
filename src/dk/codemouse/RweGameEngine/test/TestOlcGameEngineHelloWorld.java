package dk.codemouse.RweGameEngine.test;

import java.awt.Color;
import java.awt.Graphics2D;

import dk.codemouse.RweGameEngine.GameEngine;

public class TestOlcGameEngineHelloWorld extends GameEngine{
	
	int i = 0;
	
	@Override
	public void onUserCreate() {
		//useAntiAliasing(true);
	}

	@Override
	public void onUserUpdate() {
		if (i < 1000)
			i++;
		else
			i = 0;
	}

	@Override
	public void onUserDraw(Graphics2D g) {
		//Clear screen
		g.setColor(Color.black);
		g.fillRect(0, 0, screenWidth(), screenHeight());
		
//		In Olc's Hello World example random colored pixels are displayed		
//		for (int x = 0; x < screenWidth(); x++)
//			for (int y = 0; y < screenHeight(); y++) {			
//				draw(g, x, y, new Color(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1));	
//			}
		
//		I like these dynamically moving colors change		
		for (int x = 0; x < screenWidth(); x++)
			for (int y = 0; y < screenHeight(); y++) {			
				draw(g, x, y, new Color((x + i) % 255 + 1, (y + i) % 255 + 1, (y + x + i) % 255 + 1));	
			}
			
		drawLine(g, 90, 5, 65, 30, Color.WHITE);
		
		drawTriangle(g, 5, 5, 70, 20, 30, 70, Color.WHITE);
		
		fillTriangle(g, 90, 70, 70, 30, 50, 60, Color.WHITE);
		
		drawCircle(g, 35, 40, 20, Color.WHITE);
		
		fillCircle(g, 35, 40, 10, Color.WHITE);
		
		drawRect(g, 5, 50, 10, 10, Color.WHITE);
		
		fillRect(g, 5, 63, 10, 10, Color.WHITE);
	}
	
	public static void main(String[] args) {
		TestOlcGameEngineHelloWorld test = new TestOlcGameEngineHelloWorld();
		if (test.construct("Pixel size example", 100, 75, 8)) {
			test.start();
		} else {
			System.err.println("Could not construct engine!");
		}
	}
	
}
