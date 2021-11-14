package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.tree.Node;
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
        return slider(path, 0, -Float.MAX_VALUE, Float.MAX_VALUE, false);
    }
    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, -Float.MAX_VALUE, Float.MAX_VALUE,  false);
    }

    float slider(String path, float defaultValue, float min, float max) {
        return slider(path, defaultValue, min, max, true);
    }

    public float slider(String path, float defaultValue, float min, float max, boolean constrained) {
        Node node = WindowManager.treeWindow.findNodeByPathInTree(path);
        if(node == null){
            node = new Node(path, path.replaceAll("/", ""), NodeType.SLIDER_X);
            node.valueFloatDefault = defaultValue;
            node.valueFloat = defaultValue;
            node.valueFloatMin = min;
            node.valueFloatMax = max;
            node.valueFloatConstrained = constrained;
            WindowManager.treeWindow.tryRegisterNode(node);
        }
        return node.valueFloat;
    }

    public int sliderInt(String path) {
        return sliderInt(path, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }
    public int sliderInt(String path, int defaultValue) {
        return sliderInt(path, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    int sliderInt(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    public int sliderInt(String path, int defaultValue, int min, int max, boolean constrained) {
        Node node = WindowManager.treeWindow.findNodeByPathInTree(path);
        if(node == null){
            node = new Node(path, path.replaceAll("/", ""), NodeType.SLIDER_INT_X);
            node.valueFloatDefault = defaultValue;
            node.valueFloat = defaultValue;
            node.valueFloatMin = min;
            node.valueFloatMax = max;
            node.valueFloatConstrained = constrained;
            WindowManager.treeWindow.tryRegisterNode(node);
        }
        return PApplet.floor(node.valueFloat);
    }

    public boolean toggle(String path){
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue){
        Node node = WindowManager.treeWindow.findNodeByPathInTree(path);
        if(node == null){
            node = new Node(path, path.replaceAll("/", ""), NodeType.TOGGLE);
            node.valueBooleanDefault = defaultValue;
            node.valueBoolean = defaultValue;
            WindowManager.treeWindow.tryRegisterNode(node);
        }
        return node.valueBoolean;
    }

    public boolean button(String path) {
        Node node = WindowManager.treeWindow.findNodeByPathInTree(path);
        if(node == null){
            node = new Node(path, path.replaceAll("/", ""), NodeType.BUTTON);
            WindowManager.treeWindow.tryRegisterNode(node);
        }
        return node.valueBoolean;
    }
}
