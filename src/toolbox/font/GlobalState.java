package toolbox.font;

import processing.core.PApplet;
import processing.core.PFont;

public class GlobalState {
    static GlobalState singleton;

    public static float textOffsetY = 0;
    public static float cell = 20;

    public static PFont font = null;
    public static PApplet app = null;

    public static void init(PApplet app){
        GlobalState.app = app;
        GlobalState.font = app.createFont("Calibri", 18);
    }

}
