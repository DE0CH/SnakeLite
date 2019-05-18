import acm.graphics.GPoint;
import sun.text.resources.ro.CollationData_ro;

import java.awt.*;

public class Preferences {
    public static final int WINDOW_SIZE_HEIGHT = 1070;
    public static final int WINDOW_SIZE_WIDTH = 1000;
    public static final int GAME_SIZE_HEIGHT = 1000;

    public static final Color DOCK_COLOR = new Color(0x010172);
    public static final int DOCK_HEIGHT = 70;

    public static final int NUM_CELLS_TALL = 50;
    public static final int NUM_CELLS_WIDE = 50;

    public static final int WINDOW_BOARDER_TOP = 0;
    public static final int WINDOW_BOARDER_BOTTOM = 0;
    public static final int WINDOW_BOARDER_LEFT = 0;
    public static final int WINDOW_BOARDER_RIGHT = 0;

    public static final Color BACKGROUND_COLOR = new Color(0x00004d);
    public static final Color BALL_COLOR = new Color(0x107595);
    public static final double BALL_SIZE = 20;

    public static final GPoint SCOREBOARD_POSITION = new GPoint(10, 20);
    public static final Color SCOREBOARD_COLOR = new Color(0xfdbfb3);
    public static final Font SCOREBOARD_FONT = new Font("Helvetica", Font.PLAIN, 18);

    public static final String INSTRUCTIONS_MESSAGE = "Welcome to Snake Lite";
    public static final Color INSTRUCTIONS_COLOR = new Color(0xffffff);
    public static final Font INSTRUCTIONS_FONT = new Font("Helvetica ", Font.PLAIN, 24);


    public static final double SNAKEBODY_SIZE = 20;
    public static final Color SNAKEBODY_COLOR = new Color(0xfcf594);

    public static final int BUTTON_HEIGHT = 20;
    public static final int BUTTON_WIDTH = 120;
    public static final int BUTTON_SPACING = 20;
}
