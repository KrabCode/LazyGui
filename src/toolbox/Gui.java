package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.font.FontProvider;
import toolbox.windows.TreeWindow;
import toolbox.types.Color;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.window.Window;
import toolbox.window.WindowManager;

import static processing.core.PConstants.HSB;

public class Gui implements UserInputSubscriber {
    PApplet app;
    public PGraphics pg;
    public static boolean isGuiHidden = false;
    TreeWindow treeWindow = null;

    public Gui(PApplet p, boolean isGuiVisibleByDefault) {
        isGuiHidden = !isGuiVisibleByDefault;
        new Gui(p);
    }

    public Gui(PApplet p) {
        this.app = p;
        UserInputPublisher.createSingleton(app);
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        FontProvider.createSingleton(app);
        lazyResetDisplay();
        treeWindow = new TreeWindow(app, "/", "main",
                new PVector(0,0), // new PVector(Window.cell, Window.cell),
                new PVector(Window.cell * 10, Window.cell * 21),
                false);
        WindowManager.createOrUncoverWindow(treeWindow);
    }

    void lazyResetDisplay() {
        if (pg == null || pg.width != app.width || pg.height != app.height) {
            pg = app.createGraphics(app.width, app.height, app.sketchRenderer());
        }
    }

    public void update() {
        lazyResetDisplay();
        pg.beginDraw();
        pg.colorMode(HSB,1,1,1,1);
        pg.clear();
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
//        treeWindow.debugHitboxes(pg, treeWindow.root);
        pg.endDraw();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isAutoRepeat()) {
            return;
        }
        if (keyEvent.getKeyChar() == 'h') {
            isGuiHidden = !isGuiHidden;
        }
    }

    public float slider(String path) {
        return 3.14159f;
    }

    public int sliderInt(String path) {
        return 3;
    }

    public boolean button(String path) {
        return false;
    }

    public boolean toggle(String path) {
        return false;
    }

    public Color picker(String path) {
        return new Color();
    }

    public PGraphics gradient(String path) {
        return null;
    }

}
