import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public interface Page {
    ArrayList<Component> nonGraphicObjects = new ArrayList<>();
    void start();
    void quit();
    void inputHandle(InputEvent e);
    void eventHandle(ActionEvent e);
}
