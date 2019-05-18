import acm.graphics.GRect;
import acm.graphics.GLabel;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Scanner;


public class GameScreen implements Page, ActionListener {
    public enum Directions {
        GOING_UP,
        GOING_DOWN,
        GOING_LEFT,
        GOING_RIGHT;
    }
    private Directions direction;

    /** the last element is the head. */

    private SpampedeData data;


    public Timer timer = new Timer(50, this);

    private Button aiButton;
    private Button pauseButton;
    private Button newGameButton;
    private Scoreboard scoreboard;

    private GLabel pauseMessage;

    private boolean isPlaying;
    private Scoreboard scoreLabel;
    
    public void start() {
        data = new SpampedeData();
        scoreboard = new Scoreboard(Preferences.SCOREBOARD_POSITION);
        Access.game.add(scoreboard);
        makeDock();
        makeButtons();
        aiButton.requestFocusInWindow();
        timer.start();
        data.spawnSpam();
        pauseMessage = new GLabel("Game Paused");
        pauseMessage.setColor(Preferences.INSTRUCTIONS_COLOR);
        pauseMessage.setFont(Preferences.INSTRUCTIONS_FONT);
        pauseMessage.setLocation(
                (
                        (Access.game.getWidth() - pauseMessage.getSize().getWidth())  /  (float)2
                ),
                (
                        (Access.game.getHeight()/(float)2)
                )
        );
        isPlaying = true;
    }
    public void restart() {
        timer.stop();
        Access.game.startGame();
    }
    private void makeButtons() {
        aiButton = new Button("AI Mode: ON");
        aiButton.addActionListener(Access.game);
        aiButton.addKeyListener(Access.game);
        aiButton.setSize(Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
        aiButton.setLocation(
                (Preferences.WINDOW_SIZE_WIDTH - 3*Preferences.BUTTON_WIDTH - 2* Preferences.BUTTON_SPACING) / 2,
                Preferences.GAME_SIZE_HEIGHT + 10
        );
        nonGraphicObjects.add(aiButton);
        Access.game.add(aiButton);
        pauseButton = new Button("Pause");
        pauseButton.addActionListener(Access.game);
        pauseButton.addKeyListener(Access.game);
        pauseButton.setSize(Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
        pauseButton.setLocation(
                (Preferences.WINDOW_SIZE_WIDTH - 3*Preferences.BUTTON_WIDTH - 2* Preferences.BUTTON_SPACING) / 2
                        + (Preferences.BUTTON_WIDTH+ Preferences.BUTTON_SPACING),
                Preferences.GAME_SIZE_HEIGHT + 10
        );
        nonGraphicObjects.add(pauseButton);
        Access.game.add(pauseButton);
        newGameButton = new Button("New Game");
        newGameButton.addActionListener(Access.game);
        newGameButton.addKeyListener(Access.game);
        newGameButton.setSize(Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
        newGameButton.setLocation(
                (Preferences.WINDOW_SIZE_WIDTH - 3*Preferences.BUTTON_WIDTH - 2* Preferences.BUTTON_SPACING) / 2
                        + 2 * (Preferences.BUTTON_WIDTH+ Preferences.BUTTON_SPACING),
                Preferences.GAME_SIZE_HEIGHT + 10

        );
        nonGraphicObjects.add(newGameButton);
        Access.game.add(newGameButton);
    }
    private void makeDock() {
        GRect dock = new GRect(0, Preferences.GAME_SIZE_HEIGHT,Preferences.WINDOW_SIZE_WIDTH, Preferences.DOCK_HEIGHT);
        dock.setFilled(true);
        dock.setColor(Preferences.DOCK_COLOR);
        Access.game.add(dock);
    }
    public void quit() {
        timer.stop();
    }
    public void inputHandle(InputEvent e) {
        if (!(e instanceof KeyEvent)) return;
        if (((KeyEvent) e).getKeyChar() == 'a') {
            counsoleAsk();
        }
        if (((KeyEvent) e).getKeyChar() == 's') {
            counsoleAddSpam();
        }
        if (data.getSnakeMode() == SpampedeData.SnakeMode.AI_MODE) {
            return;
        }
        switch (((KeyEvent) e).getKeyCode()) {
            case KeyEvent.VK_UP:
                if (data.getSnakeMode() != SpampedeData.SnakeMode.GOING_SOUTH) {
                    data.setDirectionNorth();
                } else {
                    data.reverseSnake();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (data.getSnakeMode() != SpampedeData.SnakeMode.GOING_NORTH) {
                    data.setDirectionSouth();
                } else {
                    data.reverseSnake();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (data.getSnakeMode() != SpampedeData.SnakeMode.GOING_EAST) {
                    data.setDirectionWest();
                } else {
                    data.reverseSnake();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (data.getSnakeMode() != SpampedeData.SnakeMode.GOING_WEST) {
                    data.setDirectionEast();
                } else {
                    data.reverseSnake();
                }
                break;
        }
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            update();
        }
    }
    public void eventHandle(ActionEvent e) {
        if (e.getSource() == aiButton) {
            if (data.getSnakeMode() == SpampedeData.SnakeMode.AI_MODE) {
                data.setMode_AI(false);
                aiButton.setLabel("AI Mode: ON");
            } else {
                data.setMode_AI(true);
                aiButton.setLabel("AI Mode: OFF");
            }
        } else if (e.getSource() == pauseButton) {
            if (isPlaying) {
                timer.stop();
                Access.game.add(pauseMessage);
                pauseButton.setLabel("Resume");
                isPlaying = false;
            } else {
                Access.game.remove(pauseMessage);
                pauseButton.setLabel("Pause");
                isPlaying = true;
                timer.start();
            }
        } else if (e.getSource() == newGameButton) {
            restartConfirm();
        }
    }
    public void restartConfirm() {
        restart();
    }
    private void update() {
        if (data.move()) {
            scoreboard.addOneScore();
        }
        if (data.getGameOver()) {
            gameOver();
        }
    }
    private void gameOver() {
        timer.stop();
        Access.game.gameOver();
    }
    private void counsoleAsk() {
        System.out.println("Change Speed: ");
        Scanner scan = new Scanner(System.in);
        try {
            timer.setDelay(scan.nextInt());
        } catch (Exception e) {
            System.err.println("invalid input");
        }
    }
    private void counsoleAddSpam() {
        System.out.println("Add Length: ");
        Scanner scan = new Scanner(System.in);
        int lengthToAdd = scan.nextInt();
        for (int i = 0; i<lengthToAdd; i++){
            data.spawnSpam();
        }
    }

}
