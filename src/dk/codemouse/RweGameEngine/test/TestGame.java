package dk.codemouse.RweGameEngine.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import dk.codemouse.RweGameEngine.GameEngine;

public class TestGame extends GameEngine{
	
	int x = 5;
	int y = 5;
	
	@Override
	public void onUserCreate() {
		
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				x = GameEngine.MouseX;
				y = GameEngine.MouseY;
			}
			
		});
	}

	@Override
	public void onUserDraw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 20, 20);
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

	}

}
