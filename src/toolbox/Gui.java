package toolbox;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.userInput.UserInputPublisher;

import static processing.core.PConstants.HSB;

public class Gui {
    PApplet app;
    PGraphics pg;
    Tree tree;

    public Gui(PApplet p) {
        this.app = p;
        UserInputPublisher.CreateSingleton(app);
        lazyResetDisplay();

        tree = new Tree(app);

    }

    public void update() {
        lazyResetDisplay();
        UserInputPublisher.getInstance().update();
    }

    void lazyResetDisplay(){
        if(pg == null || pg.width != app.width || pg.height != app.height){
            pg = app.createGraphics(app.width, app.height, app.sketchRenderer());
        }
    }

    public PImage display() {
        pg.beginDraw();
        pg.clear();
        pg.colorMode(HSB,1,1,1,1);
        tree.update(pg);
        pg.endDraw();
        return pg;
    }
}
