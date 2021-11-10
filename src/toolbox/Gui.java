package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.TreeWindow;
import toolbox.types.Color;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.window.WindowManager;

public class Gui implements UserInputSubscriber {
    PApplet app;
    public PGraphics pg;
    private boolean isGuiVisible = true;

    public Gui(PApplet p, boolean isGuiVisibleByDefault){
        isGuiVisible = isGuiVisibleByDefault;
        new Gui(p);
    }

    public Gui(PApplet p) {
        this.app = p;
        UserInputPublisher.createSingleton(app);
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        lazyResetDisplay();
        WindowManager.createOrUncoverWindow(new TreeWindow(app, "/", "main", new PVector(20, 20), new PVector(150, 250), false));
    }

    void lazyResetDisplay(){
        if(pg == null || pg.width != app.width || pg.height != app.height){
            pg = app.createGraphics(app.width, app.height, app.sketchRenderer());
        }
    }

    public void update() {
        lazyResetDisplay();
        pg.beginDraw();
        pg.clear();
        if(isGuiVisible){
            WindowManager.updateAndDrawWindows(pg);
            UserInputPublisher.updateSubscriberList();
        }
        pg.endDraw();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.isAutoRepeat()){
            return;
        }
         if(keyEvent.getKeyChar() == 'h'){
             isGuiVisible = !isGuiVisible;
         }
    }

    public float slider(String path) {
        return 3;
    }

    public boolean button(String path) {
        return false;
    }

    public boolean toggle(String path) {
        return false;
    }

    public Color picker(String path){
        return new Color();
    }

    public PGraphics gradient(String path){
        return null;
    }

}
