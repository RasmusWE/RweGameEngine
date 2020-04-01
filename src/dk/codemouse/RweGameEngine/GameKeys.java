package dk.codemouse.RweGameEngine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class GameKeys extends KeyAdapter{
	
	public HashMap<Integer, Boolean> keyPressed = new HashMap<>();
	public HashMap<Integer, Boolean> keyReleased = new HashMap<>();
	
	public void resetKeys() {
		keyReleased.forEach((k,v) -> keyReleased.put(k, false));
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keyPressed.put(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyReleased.put(e.getKeyCode(), true);
		keyPressed.put(e.getKeyCode(), false);
	}
	
}
