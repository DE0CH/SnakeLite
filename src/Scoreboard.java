import acm.graphics.GLabel;
import acm.graphics.GPoint;

public class Scoreboard extends GLabel {
    private int score;
    public Scoreboard(double x, double y){
        this(new GPoint(x, y));
    }
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
    public void updateScore() {
        this.setLabel("Score: " + this.score);
    }
}
