package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.PaletteStore;
import toolbox.global.State;
import toolbox.global.NodeTree;
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
import toolbox.windows.nodes.shaderList.ShaderListFolder;

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
        State.loadMostRecentTreeSave();
        PaletteStore.initSingleton();
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        float cell = State.cell;
        FolderWindow explorer = new FolderWindow(
                new PVector(cell, cell),
                NodeTree.getMainRoot(),
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
        if(State.app.frameCount == 2){
            // TODO save this state and check if values exist there for newly created nodes instead of this frame 2 value overwrite

        }
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
            String filename = "out/screenshots/" + State.timestamp() + ".png";
            println("saved screenshot: " + filename);
            pg.save(filename);
        }
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
        SliderNode node = (SliderNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderNode(path, defaultValue, defaultPrecision, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueFloat;
    }

    public SliderNode createSliderNode(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
        SliderNode node = new SliderNode(path, folder, defaultValue, min, max, defaultPrecision, constrained);
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
        SliderIntNode node = (SliderIntNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            node = createSliderIntNode(path, defaultValue, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return PApplet.floor(node.valueFloat);
    }

    private SliderIntNode createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
        SliderIntNode node = new SliderIntNode(path, folder, defaultValue,  min, max,0.1f, constrained);
        node.initSliderBackgroundShader();
        return node;
    }

    public boolean toggle(String path) {
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue) {
        ToggleNode node = (ToggleNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ToggleNode createToggleNode(String path, boolean defaultValue) {
        FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
        return new ToggleNode(path, folder, defaultValue);
    }

    public boolean button(String path) {
        ButtonNode node = (ButtonNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            node = createButtonNode(path);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    private ButtonNode createButtonNode(String path) {
        FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
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
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            int hex = State.colorProvider.color(hueNorm, saturationNorm, brightnessNorm, 1);
            FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public Color colorPicker(String path, int hex) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public void colorPickerSet(String path, int hex) {
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            FolderNode folder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
            node = new ColorPickerFolderNode(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }else{

            node.setHex(hex);
            node.loadValuesFromHex(false);
        }
    }

    public void filterList(String path, PGraphics pg) {
        ShaderListFolder node = (ShaderListFolder) NodeTree.findNodeByPathInTree(path);
        if(node == null){
            FolderNode parentFolder = (FolderNode) NodeTree.getLazyInitParentFolderByPath(path);
            node = new ShaderListFolder(path, parentFolder);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.filter(pg);
    }
}
