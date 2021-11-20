package toolbox;

import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PSurface;
import processing.opengl.PShader;

import java.awt.*;
import java.util.HashMap;

import static processing.core.PApplet.println;

public class GlobalState {
    public static float cell = 30;

    public static PFont font = null;
    public static PApplet app = null;
    public static Robot robot;
    public static GLWindow window;
    public static String libraryPath;


    public static void init(PApplet app){
        GlobalState.app = app;
        GlobalState.font = app.createFont("Calibri", 20);


        PSurface surface = GlobalState.app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        libraryPath = Utils.getLibraryPath();
    }

}
