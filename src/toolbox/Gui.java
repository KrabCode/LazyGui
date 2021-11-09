package toolbox;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.tree.TreeWindow;
import toolbox.userInput.UserInputPublisher;

import static processing.core.PConstants.HSB;

public class Gui {
    PApplet app;
    PGraphics pg;
    TreeWindow treeWindow;

    public Gui(PApplet p) {
        this.app = p;
        UserInputPublisher.CreateSingleton(app);
        lazyResetDisplay();

        treeWindow = new TreeWindow(app);

    }

    void lazyResetDisplay(){
        if(pg == null || pg.width != app.width || pg.height != app.height){
            pg = app.createGraphics(app.width, app.height, app.sketchRenderer());
        }
    }

    public void update() {
        lazyResetDisplay();
    }

    public PGraphics display() {
        pg.beginDraw();
        pg.clear();
        pg.colorMode(HSB,1,1,1,1);
        treeWindow.update(pg);
        pg.endDraw();
        return pg;
    }
}
