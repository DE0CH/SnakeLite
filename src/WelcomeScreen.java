import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

public class WelcomeScreen implements Page {
    private Instructions instructions;
    private Button startButton;
    public synchronized void start() {
        this.setUpLabels();
    }
    public void quit() {
    }
    public void inputHandle(InputEvent e) {
//        if (e instanceof MouseEvent) {
//            Access.game.startGame();
//        }
    }
    public void eventHandle(ActionEvent e) {
        if (e.getSource() == startButton) {
            Access.game.startGame();
        }

    }
    private void setUpLabels() {
        instructions = new Instructions(Preferences.INSTRUCTIONS_MESSAGE);
        instructions.setColor(Preferences.INSTRUCTIONS_COLOR);
        instructions.setFont(Preferences.INSTRUCTIONS_FONT);
        instructions.setLocation(
                (
                        (Access.game.getWidth() - instructions.getSize().getWidth())  /  (float)2
                ),
                (
                        (Access.game.getHeight()/(float)2)
                )
        );
        Access.game.add(instructions);
        startButton = new Button("Start");
        startButton.addActionListener(Access.game);
        startButton.addKeyListener(Access.game);
        startButton.setSize(Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
        startButton.setLocation(
                Math.round(
                        (Access.game.getWidth() - 100)  /  (float)2
                ),
                Math.round(
                        (Access.game.getHeight()/(float)2) + 30
                )
        );
        nonGraphicObjects.add(startButton);
        Access.game.add(startButton);
    }
}
