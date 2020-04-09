package dk.codemouse.RweGameEngine.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dk.codemouse.RweGameEngine.GameEngine;
import dk.codemouse.RweGameEngine.Pair;

public class TestOlcGameEngineHelloWorld extends GameEngine{
	
	ArrayList<Pair<Float>> modelRect = new ArrayList<>();
	
	@Override
	public void onCreate() {
//		useAntiAliasing(true);
//		frame.setResizable(true);
		
		modelRect.add(new Pair<Float>(0.0f, 0.0f));
		modelRect.add(new Pair<Float>(5.0f, 0.0f));
		modelRect.add(new Pair<Float>(5.0f, 5.0f));
		modelRect.add(new Pair<Float>(0.0f, 5.0f));
	}

	@Override
	public void onUpdate(double elapsedTime) {
		//Exit
		if (keyReleased(KeyEvent.VK_ESCAPE)) {
			tryStop();
		}
	}
	
	@Override
	public void onDraw(Graphics2D g) {
		//Clear screen
		clearScreen(g, Color.black);
		
//		In Olc's Hello World example random colored pixels are displayed		
		for (int x = 0; x < screenWidth(); x++)
			for (int y = 0; y < screenHeight(); y++) {			
				draw(g, x, y, new Color(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1));	
			}
		
//		for (int x = 0; x < screenWidth(); x++)
//			for (int y = 0; y < screenHeight(); y++) {	
//				if ((x + y) % 2 == 0)
//					draw(g, x, y, Color.DARK_GRAY);	
//				else
//					draw(g, x, y, Color.BLACK);	
//			}
		
		drawLine(g, 90, 5, 65, 30, Color.WHITE);
		
		drawTriangle(g, 1, 5, 70, 20, 30, 72, Color.WHITE);
		
		fillTriangle(g, 90, 70, 70, 30, 50, 60, Color.WHITE);
		
		drawCircle(g, 35, 40, 20, Color.WHITE);
		
		fillCircle(g, 35, 40, 10, Color.WHITE);
		
		drawLine(g, 1, 1, 10, 1, Color.WHITE);
		
		drawRect(g, 1, 50, 10, 10, Color.WHITE);
		
		fillRect(g, 1, 62, 10, 10, Color.WHITE);
		
		setFontSize(8);
		drawString(g, "Hello world", 30, 10, Color.WHITE);
		
		drawPolygon(g, modelRect, 90, 20, -5.0f, 2.0f, Color.WHITE);
	}
	
	@Override
	public boolean onDestroy() {
		return false;
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
