import acm.graphics.GLabel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

public class RetryScreen implements Page {
    private GLabel gameoverMessage;
    private Button restartButton;

    public void start() {
        gameoverMessage = new GLabel("Game Over");
        gameoverMessage.setColor(new Color(0xe41749));
        gameoverMessage.setFont(Preferences.INSTRUCTIONS_FONT);
        gameoverMessage.setLocation(
                (
                        (Access.game.getWidth() - gameoverMessage.getSize().getWidth())  /  (float)2
                ),
                (
                        (Access.game.getHeight()/(float)2)
                )
        );
        Access.game.add(gameoverMessage);
        restartButton = new Button("Try Again");
        restartButton.addActionListener(Access.game);
        restartButton.setSize(Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
        restartButton.setLocation(
                Math.round(
                        (Access.game.getWidth() - Preferences.BUTTON_WIDTH)  /  (float)2
                ),
                Math.round(
                        (Access.game.getHeight()/(float)2) + 30
                )
        );
        nonGraphicObjects.add(restartButton);
        Access.game.add(restartButton);
    }

    public void quit() {

    }

    @Override
    public void eventHandle(ActionEvent e) {
        if (e.getSource() == restartButton) {
            Access.game.startGame();
        }

    }

    public void inputHandle(InputEvent e) {

    }
}
