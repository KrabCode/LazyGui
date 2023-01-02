package lazy.stores;

import lazy.LazyGui;
import processing.core.PApplet;

public class Globals {

    public static PApplet app = null;
    public static LazyGui gui = null;

    public static void init(LazyGui gui, PApplet app){
        Globals.app = app;
        Globals.gui = gui;
    }
}
