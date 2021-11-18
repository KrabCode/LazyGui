package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.Folder;
import toolbox.tree.Node;
import toolbox.tree.NodeType;
import toolbox.tree.Tree;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import static processing.core.PConstants.HSB;

public class Gui implements UserInputSubscriber {
    private Tree tree;
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
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();

        tree = new Tree("main tree");
        float cell = GlobalState.cell;
        FolderWindow explorer = new FolderWindow(
                new PVector(cell, cell),
                new PVector(cell * 8, cell * 10),
                tree.root,
                false
        );
        WindowManager.uncoverOrAddWindow(explorer);

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
        pg.colorMode(HSB, 1, 1, 1, 1);
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
        return slider(path, 0, 0.1f, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, 0.1f, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    public float sliderConstrained(String path, float defaultValue, float min, float max) {
        return slider(path, defaultValue, 0.1f, min, max, true);
    }

    public float slider(String path, int defaultValue, float defaultPrecision) {
        return slider(path, defaultValue, defaultPrecision, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    private float slider(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        Node node = tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderNode(path, defaultValue, defaultPrecision, min, max, constrained);
            tree.insertNodeAtPath(node);
        }
        return node.value.valueFloat;
    }

    public Node createSliderNode(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        Folder folder = (Folder) tree.findParentFolderByNodePath(path);
        Node node = new Node(path, NodeType.SLIDER_X, folder);
        node.value.valueFloatDefault = defaultValue;
        node.value.valueFloat = defaultValue;
        node.value.valueFloatMin = min;
        node.value.valueFloatMax = max;
        node.value.valueFloatPrecision = defaultPrecision;
        node.value.valueFloatPrecisionDefault = defaultPrecision;
        node.value.valueFloatConstrained = constrained;
        return node;
    }

    public int sliderInt(String path) {
        return sliderInt(path, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    public int sliderInt(String path, int defaultValue) {
        return sliderInt(path, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    public int sliderIntConstrained(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    private int sliderInt(String path, int defaultValue, int min, int max, boolean constrained) {
        Node node = tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderIntNode(path, defaultValue, min, max, constrained);
            tree.insertNodeAtPath(node);
        }
        return PApplet.floor(node.value.valueFloat);
    }

    private Node createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        Folder folder = (Folder) tree.findParentFolderByNodePath(path);
        Node node = new Node(path, NodeType.SLIDER_INT_X, folder);
        node.value.valueFloatDefault = defaultValue;
        node.value.valueFloat = defaultValue;
        node.value.valueFloatMin = min;
        node.value.valueFloatMax = max;
        node.value.valueFloatConstrained = constrained;
        return node;
    }

    public boolean toggle(String path) {
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue) {
        Node node = tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            tree.insertNodeAtPath(node);
        }
        return node.value.valueBoolean;
    }

    private Node createToggleNode(String path, boolean defaultValue) {
        Folder folder = (Folder) tree.findParentFolderByNodePath(path);
        Node node = new Node(path, NodeType.TOGGLE, folder);
        node.value.valueBooleanDefault = defaultValue;
        node.value.valueBoolean = defaultValue;
        return node;
    }

    public boolean button(String path) {
        Node node = tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createButtonNode(path);
            tree.insertNodeAtPath(node);
        }
        return node.value.valueBoolean;
    }

    private Node createButtonNode(String path) {
        Folder folder = (Folder) tree.findParentFolderByNodePath(path);
        return new Node(path, NodeType.BUTTON, folder);
    }
}
