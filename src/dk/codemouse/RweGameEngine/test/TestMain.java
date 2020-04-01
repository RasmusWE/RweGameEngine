package dk.codemouse.RweGameEngine.test;

public class TestMain {
	
	public static void main(String[] args) {
		TestGame game = new TestGame();
		if (game.construct(400, 300)) {
			game.start();
		} else {
			System.err.println("Error occured during construction");
		}
	}
	
}
