package toolbox.font;

import processing.core.PApplet;
import processing.core.PFont;

public class GlobalState {
    static GlobalState singleton;

    public static float cell = 20;

    private PFont font = null;
    private PApplet app = null;

    private GlobalState(){

    }

    public static void createSingleton(PApplet app){
        if(singleton == null){
            singleton = new GlobalState();
            singleton.app = app;
            singleton.font = app.createFont("Calibri", 18);
        }
    }

    public static GlobalState getInstance(){
        return singleton;
    }

    public PFont getFont(){
        return font;
    }

    public PApplet getApp(){
        return app;
    }
}
