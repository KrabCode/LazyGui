package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.PaletteStore;
import toolbox.global.State;
import toolbox.global.NodeStore;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.colorPicker.Color;
import toolbox.windows.nodes.colorPicker.ColorPickerFolderNode;
import toolbox.windows.nodes.ButtonNode;
import toolbox.windows.nodes.ToggleNode;
import toolbox.windows.nodes.SliderIntNode;
import toolbox.windows.nodes.SliderNode;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;
import toolbox.windows.nodes.shaderList.ShaderListFolderNode;

import static processing.core.PApplet.*;

@SuppressWarnings("unused")
public class Gui implements UserInputSubscriber {
    PApplet app;
    public PGraphics pg;
    public static boolean isGuiHidden = false;
    boolean lastRecordNow = false;
    String recordingFolderName = "";
    private int recordingFrame = 1;

    public Gui(PApplet p, boolean isGuiVisibleByDefault) {
        isGuiHidden = !isGuiVisibleByDefault;
        new Gui(p);
    }

    public Gui(PApplet p) {
        this.app = p;
        State.init(this, app);
        PaletteStore.initSingleton();
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        float cell = State.cell;
        FolderWindow explorer = new FolderWindow(
                new PVector(cell, cell),
                NodeStore.getTreeRoot(),
                false
        );
        explorer.createToolbar();
        WindowManager.addWindow(explorer);

        lazyResetDisplay();
    }

    void lazyResetDisplay() {
        if (pg == null || pg.width != app.width || pg.height != app.height) {
            pg = app.createGraphics(app.width, app.height, P2D);
            pg.noSmooth();
        }
    }

    public void draw() {
        draw(State.app.g);
    }

    public void draw(PGraphics canvas) {
        lazyResetDisplay();
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.clear();
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
        pg.endDraw();
        canvas.image(pg, 0, 0);
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

    public void record() {
        record(State.app.g);
    }

    boolean isRecording = false;

    public void record(PGraphics pg) {
        boolean screenshot = button("recorder/screenshot");
        boolean recordNow = button("recorder/start recording");
        boolean stopRecording = button("recorder/stop recording");
        int framesToRecord = sliderInt("recorder/frames", 360, 0, Integer.MAX_VALUE);
        if (!lastRecordNow && recordNow) {
            recordingFolderName = generateRecordingFolderName();
            recordingFrame = 1;
            isRecording = true;
        }
        lastRecordNow = recordNow;
        if (isRecording) {
            println("recording " + recordingFrame + " / " + framesToRecord);
            pg.save("out/recorded/" + recordingFolderName + "/" + recordingFrame + ".jpg");
            if (stopRecording || recordingFrame >= framesToRecord) {
                isRecording = false;
            }
            recordingFrame++;
        }
        if (screenshot) {
            String filename = "out/screenshots/" + timestamp() + ".png";
            println("saved screenshot: " + filename);
            pg.save(filename);
        }
    }

    private String timestamp() {
        return year()
                + nf(month(), 2)
                + nf(day(), 2)
                + "-"
                + nf(hour(), 2)
                + nf(minute(), 2)
                + nf(second(), 2);
    }

    private String generateRecordingFolderName() {
        return year() + nf(month(), 2) + nf(day(), 2) + "-" + nf(hour(), 2) + nf(minute(), 2) + nf(second(),
                2);
    }

    private void runFFMPEG() {
        // TODO
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
        SliderNode node = (SliderNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderNode(path, defaultValue, defaultPrecision, min, max, constrained);
            NodeStore.insertNodeAtItsPath(node);
        }
        return node.valueFloat;
    }

    public SliderNode createSliderNode(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
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

    public int sliderInt(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    private int sliderInt(String path, int defaultValue, int min, int max, boolean constrained) {
        SliderIntNode node = (SliderIntNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderIntNode(path, defaultValue, min, max, constrained);
            NodeStore.insertNodeAtItsPath(node);
        }
        return PApplet.floor(node.valueFloat);
    }

    private SliderIntNode createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
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
        ToggleNode node = (ToggleNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            NodeStore.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ToggleNode createToggleNode(String path, boolean defaultValue) {
        FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
        return new ToggleNode(path, folder, defaultValue);
    }

    public boolean button(String path) {
        ButtonNode node = (ButtonNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            node = createButtonNode(path);
            NodeStore.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ButtonNode createButtonNode(String path) {
        FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
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
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            State.colorProvider.colorMode(HSB, 1, 1, 1, 1);
            int hex = State.colorProvider.color(hueNorm, saturationNorm, brightnessNorm, 1);
            FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeStore.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public Color colorPicker(String path, int hex) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeStore.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public void colorPickerSet(String path, int hex) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeStore.findNodeByPathInTree(path);
        if (node == null) {
            FolderNode folder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeStore.insertNodeAtItsPath(node);
        }else{

            node.setHex(hex);
            node.loadValuesFromHex(false);
        }
    }

    public void filter(String path, PGraphics pg) {
        ShaderListFolderNode node = (ShaderListFolderNode) NodeStore.findNodeByPathInTree(path);
        if(node == null){
            FolderNode parentFolder = (FolderNode) NodeStore.getLazyInitParentFolderByPath(path);
            node = new ShaderListFolderNode(path, parentFolder);
            NodeStore.insertNodeAtItsPath(node);
        }
        node.filter(pg);
    }
}
