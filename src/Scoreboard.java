import acm.graphics.GLabel;
import acm.graphics.GPoint;

public class Scoreboard extends GLabel {
    private int score;

    public Scoreboard(GPoint position) {
        super("Score: 0", 0, 0);
        this.setLocation(position);
        this.setColor(Preferences.SCOREBOARD_COLOR);
        this.setFont(Preferences.SCOREBOARD_FONT);
    }
    public void addOneScore() {
        score++;
        updateScore();
    }
    private void updateScore() {
        this.setLabel("Score: " + this.score);
    }
}
