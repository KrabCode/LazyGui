package lazy.stores;

import com.jogamp.newt.opengl.GLWindow;
import lazy.LazyGui;
import processing.core.PApplet;

public class GlobalReferences {

    public static PApplet app;
    public static LazyGui gui;
    public static GLWindow appWindow;

    public static void init(LazyGui gui, PApplet app){
        GlobalReferences.app = app;
        appWindow = (com.jogamp.newt.opengl.GLWindow) app.getSurface().getNative();
        GlobalReferences.gui = gui;
    }
}
