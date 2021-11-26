package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.tree.*;
import toolbox.tree.nodes.*;
import toolbox.tree.nodes.color.Color;
import toolbox.tree.nodes.color.ColorPickerFolderNode;
import toolbox.tree.nodes.simple_clickables.ButtonNode;
import toolbox.tree.nodes.simple_clickables.ToggleNode;
import toolbox.tree.nodes.SliderIntNode;
import toolbox.tree.nodes.SliderNode;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import static processing.core.PConstants.BLEND;
import static processing.core.PConstants.HSB;

@SuppressWarnings("unused")
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
        State.init(this, app);
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        tree = new Tree("main tree");
        float cell = State.cell;
        FolderWindow explorer = new FolderWindow(
                new PVector(cell, cell),
                tree.root,
                false
        );
        WindowManager.addWindow(explorer);

        lazyResetDisplay();
    }

    void lazyResetDisplay() {
        if (pg == null || pg.width != app.width || pg.height != app.height) {
            pg = app.createGraphics(app.width, app.height, app.sketchRenderer());
        }
    }

    public void update(){
        update(State.app.g);
    }

    public void update(PGraphics canvas) {
        lazyResetDisplay();
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.blendMode(BLEND);
        pg.clear();
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
        pg.endDraw();
        canvas.image(pg, 0, 0, canvas.width, canvas.height);
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

    public float slider(String path, float defaultValue, float defaultPrecision) {
        return slider(path, defaultValue, defaultPrecision, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    private float slider(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        SliderNode node = (SliderNode) tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderNode(path, defaultValue, defaultPrecision, min, max, constrained);
            tree.insertNodeAtItsPath(node);
        }
        return node.valueFloat;
    }

    public SliderNode createSliderNode(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
        SliderNode node = new SliderNode(path, folder);
        node.valueFloatDefault = defaultValue;
        node.valueFloat = defaultValue;
        node.valueFloatMin = min;
        node.valueFloatMax = max;
        node.valueFloatPrecision = defaultPrecision;
        node.valueFloatPrecisionDefault = defaultPrecision;
        node.valueFloatConstrained = constrained;
        node.initSliderPrecisionArrays();
        node.initSliderBackgroundShader();
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
        SliderIntNode node = (SliderIntNode) tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderIntNode(path, defaultValue, min, max, constrained);
            tree.insertNodeAtItsPath(node);
        }
        return PApplet.floor(node.valueFloat);
    }

    private SliderIntNode createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
        SliderIntNode node = new SliderIntNode(path, folder);
        node.valueFloatDefault = defaultValue;
        node.valueFloat = defaultValue;
        node.valueFloatMin = min;
        node.valueFloatMax = max;
        node.valueFloatConstrained = constrained;
        node.initSliderPrecisionArrays();
        node.initSliderBackgroundShader();
        return node;
    }

    public boolean toggle(String path) {
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue) {
        ToggleNode node = (ToggleNode) tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            tree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ToggleNode createToggleNode(String path, boolean defaultValue) {
        FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
        return new ToggleNode(path, folder, defaultValue);
    }

    public boolean button(String path) {
        ButtonNode node = (ButtonNode) tree.findNodeByPathInTree(path);
        if (node == null) {
            node = createButtonNode(path);
            tree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ButtonNode createButtonNode(String path) {
        FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
        return new ButtonNode(path, folder);
    }

    public Color colorPicker(String path) {
        return colorPicker(path, 1, 1, 0, 1);
    }

    public Color colorPicker(String path, float grayNorm) {
        return colorPicker(path, grayNorm, grayNorm, grayNorm, 1);
    }

    public Color colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm) {
        return colorPicker(path, hueNorm, saturationNorm, brightnessNorm, 1);
    }

    public Color colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) tree.findNodeByPathInTree(path);
        if(node == null){
            State.colorProvider.colorMode(HSB, 1, 1, 1, 1);
            int hex = State.colorProvider.color(hueNorm,saturationNorm,brightnessNorm,1);
            FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            tree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public Color colorPicker(String path, int hex) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) tree.findNodeByPathInTree(path);
        if(node == null){
            FolderNode folder = (FolderNode) tree.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            tree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }
}
