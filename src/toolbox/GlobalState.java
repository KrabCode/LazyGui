package toolbox;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.awt.*;

public class GlobalState {
    public static float cell = 30;

    public static PFont font = null;
    public static PApplet app = null;
    public static Robot robot;

    public static void init(PApplet app){
        GlobalState.app = app;
        GlobalState.font = app.createFont("Calibri", 20);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
