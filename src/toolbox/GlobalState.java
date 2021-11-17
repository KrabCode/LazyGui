package toolbox;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class GlobalState {
    static GlobalState singleton;
    public static float cell = 22;

    public static PFont font = null;
    public static PApplet app = null;

    public static void init(PApplet app){
        GlobalState.app = app;
        GlobalState.font = app.createFont("Calibri", 20);
    }

}
