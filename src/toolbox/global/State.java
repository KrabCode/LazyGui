package toolbox.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PSurface;
import toolbox.Gui;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static processing.core.PApplet.println;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;


public class State {
    public static float cell = 24;
    public static PFont font = null;
    public static PApplet app = null;
    public static Gui gui = null;
    public static Robot robot = null;
    public static GLWindow window = null;
    public static String libraryPath = null;
    public static PGraphics colorProvider = null;
    public static float textMarginX = 5;

    public static int clipboardHex = 0;
    public static float clipboardFloat = 0;

    public static void init(Gui gui, PApplet app){
        State.gui = gui;
        State.app = app;
        try {
            State.font = app.createFont("Calibri", 20);
        }catch(RuntimeException ex){
            if(ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")){
                throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
            }

        }
        colorProvider = app.createGraphics(256,256, P2D);
        State.colorProvider.colorMode(HSB,1,1,1,1);

        PSurface surface = State.app.getSurface();
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
