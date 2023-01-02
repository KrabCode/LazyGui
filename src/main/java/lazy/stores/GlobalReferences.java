package lazy.stores;

import lazy.LazyGui;
import processing.core.PApplet;

public class GlobalReferences {

    public static PApplet app;
    public static LazyGui gui;

    public static void init(LazyGui gui, PApplet app){
        GlobalReferences.app = app;
        GlobalReferences.gui = gui;
    }
}
