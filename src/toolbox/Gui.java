package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.PaletteStore;
import toolbox.global.State;
import toolbox.tree.*;
import toolbox.tree.rows.*;
import toolbox.tree.rows.color.Color;
import toolbox.tree.rows.color.ColorPickerFolderRow;
import toolbox.tree.rows.ButtonRow;
import toolbox.tree.rows.ToggleRow;
import toolbox.tree.rows.SliderIntRow;
import toolbox.tree.rows.SliderRow;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import static processing.core.PApplet.*;

@SuppressWarnings("unused")
public class Gui implements UserInputSubscriber {
    private Tree tree;
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
        tree = new Tree("main tree");
        float cell = State.cell;
        FolderWindow explorer = new FolderWindow(
                new PVector(cell, cell),
                tree.root,
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

    public void update() {
        update(State.app.g);
    }

    public void update(PGraphics canvas) {
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

    public void recorder() {
        recorder(State.app.g);
    }

    boolean isRecording = false;

    public void recorder(PGraphics pg) {
        boolean screenshot = button("recorder/screenshot");
        boolean recordNow = button("recorder/start recording");
        boolean stopRecording = button("recorder/stop recording");
        int framesToRecord = sliderInt("recorder/frames", 600, 0, Integer.MAX_VALUE);
//        boolean useFfmpeg = toggle("rec/make .mp4", true); // TODO
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
//                if (useFfmpeg) {
//                    runFFMPEG(); // TODO
//                }
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
        SliderRow row = (SliderRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            row = createSliderNode(path, defaultValue, defaultPrecision, min, max, constrained);
            tree.insertNodeAtItsPath(row);
        }
        return row.valueFloat;
    }

    public SliderRow createSliderNode(String path, float defaultValue, float defaultPrecision, float min, float max, boolean constrained) {
        FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
        SliderRow row = new SliderRow(path, folder);
        row.valueFloatDefault = defaultValue;
        row.valueFloat = defaultValue;
        row.valueFloatMin = min;
        row.valueFloatMax = max;
        row.valueFloatPrecision = defaultPrecision;
        row.valueFloatPrecisionDefault = defaultPrecision;
        row.valueFloatConstrained = constrained;
        row.initSliderPrecisionArrays();
        row.initSliderBackgroundShader();
        return row;
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
        SliderIntRow row = (SliderIntRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            row = createSliderIntNode(path, defaultValue, min, max, constrained);
            tree.insertNodeAtItsPath(row);
        }
        return PApplet.floor(row.valueFloat);
    }

    private SliderIntRow createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
        SliderIntRow row = new SliderIntRow(path, folder);
        row.valueFloatDefault = defaultValue;
        row.valueFloat = defaultValue;
        row.valueFloatMin = min;
        row.valueFloatMax = max;
        row.valueFloatConstrained = constrained;
        row.initSliderPrecisionArrays();
        row.initSliderBackgroundShader();
        return row;
    }

    public boolean toggle(String path) {
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue) {
        ToggleRow row = (ToggleRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            row = createToggleNode(path, defaultValue);
            tree.insertNodeAtItsPath(row);
        }
        return row.valueBoolean;
    }

    private ToggleRow createToggleNode(String path, boolean defaultValue) {
        FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
        return new ToggleRow(path, folder, defaultValue);
    }

    public boolean button(String path) {
        ButtonRow row = (ButtonRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            row = createButtonNode(path);
            tree.insertNodeAtItsPath(row);
        }
        return row.valueBoolean;
    }

    private ButtonRow createButtonNode(String path) {
        FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
        return new ButtonRow(path, folder);
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
        ColorPickerFolderRow row = (ColorPickerFolderRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            State.colorProvider.colorMode(HSB, 1, 1, 1, 1);
            int hex = State.colorProvider.color(hueNorm, saturationNorm, brightnessNorm, 1);
            FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
            row = new ColorPickerFolderRow(path, folder, hex);
            tree.insertNodeAtItsPath(row);
        }
        return row.getColor();
    }

    public Color colorPicker(String path, int hex) {
        ColorPickerFolderRow row = (ColorPickerFolderRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
            row = new ColorPickerFolderRow(path, folder, hex);
            tree.insertNodeAtItsPath(row);
        }
        return row.getColor();
    }

    public void colorPickerSet(String path, int hex) {
        ColorPickerFolderRow row = (ColorPickerFolderRow) tree.findNodeByPathInTree(path);
        if (row == null) {
            FolderRow folder = (FolderRow) tree.getLazyInitParentFolderByPath(path);
            row = new ColorPickerFolderRow(path, folder, hex);
            tree.insertNodeAtItsPath(row);
        }else{

            row.hex = hex;
            row.loadValuesFromHex(false);
        }
    }
}
