package toolbox.font;

import processing.core.PApplet;
import processing.core.PFont;
import toolbox.window.Window;

public class FontProvider {
    static FontProvider singleton;

    private static PFont font = null;

    private FontProvider(PApplet app){

        font = app.createFont("Calibri", 18);
    }

    public static void createSingleton(PApplet app){
        if(singleton == null){
            singleton = new FontProvider(app);
        }
    }

    public static FontProvider getInstance(){
        return singleton;
    }

    public PFont getFont(){
        return font;
    }
}
