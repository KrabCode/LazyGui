package lazy.stores;

import lazy.LazyGui;
import processing.core.PApplet;

public class GlobalReferences {

    public static PApplet app = null;
    public static LazyGui gui = null;

    public static void init(LazyGui gui, PApplet app){
        GlobalReferences.app = app;
        GlobalReferences.gui = gui;
    }
}
