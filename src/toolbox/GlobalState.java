package toolbox;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class GlobalState {
    public static float cell = 30;

    public static PFont font = null;
    public static PApplet app = null;

    public static void init(PApplet app){
        GlobalState.app = app;
        GlobalState.font = app.createFont("Calibri", 20);
    }

}
