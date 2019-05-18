

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class Game extends GraphicsProgram implements ActionListener {




	private Page currentPage;

	public void run() {
		this.setSize(Preferences.WINDOW_SIZE_WIDTH, Preferences.WINDOW_SIZE_HEIGHT);
		this.addKeyListeners();
		this.addMouseListeners();
		this.setBackground(Preferences.BACKGROUND_COLOR);
		try {
			Thread.sleep(400);
		} catch(InterruptedException e) {

		}
		begin();
	}
	public void begin() {
		startScreen();
	}
	public void startScreen() {
		this.currentPage = new WelcomeScreen();
		this.currentPage.start();
	}
	public void startGame() {
		clearPage();
		this.currentPage = new GameScreen();
		this.currentPage.start();
	}
	public void gameOver() {
		for (Component object : currentPage.nonGraphicObjects) {
			this.remove(object);
		}
		this.currentPage = new RetryScreen();
		currentPage.start();
	}
	private void clearPage() {
		this.currentPage.quit();
		this.removeAll();
		for (Component object : currentPage.nonGraphicObjects) {
			this.remove(object);
		}
	}






	public void mouseClicked (MouseEvent e) {
        currentPage.inputHandle(e);
    }

	public void keyPressed (KeyEvent e) {
		currentPage.inputHandle(e);
	}



	private void moveUp() {
	}

	private void moveDown() {
	}

	private void moveLeft() {
	}

	private void moveRight() {
	}

	public void actionPerformed(ActionEvent arg0) {
		currentPage.eventHandle(arg0);
	}

}

