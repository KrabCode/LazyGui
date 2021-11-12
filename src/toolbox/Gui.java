package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.structs.Color;
import toolbox.tree.NodeType;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.WindowManager;

import static processing.core.PConstants.HSB;

public class Gui implements UserInputSubscriber {
    PApplet app;
    public PGraphics pg;
    public static boolean isGuiHidden = false;

    public Gui(PApplet p, boolean isGuiVisibleByDefault) {
        isGuiHidden = !isGuiVisibleByDefault;
        new Gui(p);
    }

    public Gui(PApplet p) {
        this.app = p;
        GlobalState.init(app);
        UserInputPublisher.createSingleton(app);
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        lazyResetDisplay();
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
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.SLIDER_X);
        return (float) WindowManager.treeWindow.getNodeValue(path);
    }

    public int sliderInt(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.SLIDER_INT_X);
        return (int) WindowManager.treeWindow.getNodeValue(path);
    }

    public PVector plotXY(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.PLOT_XY);
        return (PVector) WindowManager.treeWindow.getNodeValue(path);
    }

    public PVector plotXYZ(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.PLOT_XYZ);
        return (PVector) WindowManager.treeWindow.getNodeValue(path);
    }

    public boolean button(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.BUTTON);
        return (boolean) WindowManager.treeWindow.getNodeValue(path);
    }

    public boolean toggle(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.TOGGLE);
        return (boolean) WindowManager.treeWindow.getNodeValue(path);
    }

    public Color picker(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.COLOR_PICKER);
        return (Color) WindowManager.treeWindow.getNodeValue(path);
    }

    public PGraphics gradient(String path) {
        WindowManager.treeWindow.tryRegisterNode(path, NodeType.GRADIENT_PICKER);
        return (PGraphics) WindowManager.treeWindow.getNodeValue(path);
    }

}
