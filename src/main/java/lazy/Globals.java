package lazy;

import processing.core.PApplet;

class Globals {

    static PApplet app = null;
    static LazyGui gui = null;

    static void init(LazyGui gui, PApplet app){
        Globals.app = app;
        Globals.gui = gui;
    }
}
