package lazy;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.*;

/**
 * Main class for controlling the GUI from the library user's processing code.
 * Should be initialized as a global variable in processing </code>setup()</code> function with </code>new LazyGui(this)</code>
 * Registers itself at end of the </code>draw()</code> method and displays the GUI whenever </code>draw()</code> ends.
 * Allows the library user to get the value of a gui control element at any time inside </code>draw()</code>, even repeatedly inside loops.
 * If the control element does not exist yet at the time its value is requested it gets newly created just in time.
 */
public class LazyGui implements UserInputSubscriber {
    static boolean isGuiHidden = false;
    private static boolean screenshotRequestedOnMainThread = false;
    private static boolean screenshotRequestedOnMainThreadWithCustomPath = false;
    private static String requestedScreenshotCustomPath = "";
    private static boolean hotkeyHideActive, undoHotkeyActive, redoHotkeyActive, hotkeyScreenshotActive, hotkeyCloseAllWindowsActive, saveHotkeyActive;
    static boolean drawPathTooltips = false;
    private PGraphics pg;
    NodeFolder toolbar;
    PApplet app;

    /**
     * Constructor for the LazyGui object which acts as an entry point to the entire LazyGui library.
     * Meant to be initialized in <code>setup()</code> with <code>new LazyGui(this)</code>
     * Registers itself at end of the </code>draw()</code> method and displays the GUI whenever </code>draw()</code> ends
     *
     * @param sketch main processing sketch class to display the GUI on and use keyboard and mouse input from
     */
    public LazyGui(PApplet sketch) {
        this.app = sketch;
        if (!app.sketchRenderer().equals(P2D) && !app.sketchRenderer().equals(P3D)) {
            println("The LazyGui library requires the P2D or P3D renderer.");
        }
        State.init(this, app);
        ThemeStore.initSingleton();
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        float cell = State.cell;
        WindowManager.addWindow( new FolderWindow(cell, cell,NodeTree.getRoot(),false));
        State.loadMostRecentSave();
        createOptionsFolder();
        lazyFollowSketchResolution();
        app.registerMethod("draw", this);
    }

    void lazyFollowSketchResolution() {
        if (pg == null || pg.width != app.width || pg.height != app.height) {
            pg = app.createGraphics(app.width, app.height, P2D);
            pg.noSmooth();
        }
    }

    /**
     * Updates and draws the GUI on the main processing canvas.
     * Not meant to be called manually by the library user as it gets called automatically at the end of draw().
     */
    public void draw() {
        draw(State.app.g);
    }

    private void draw(PGraphics canvas) {
        lazyFollowSketchResolution();
        updateOptionsFolder();
        updateAllNodeValuesRegardlessOfParentWindowOpenness();
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.clear();
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
        pg.endDraw();
        Utils.resetSketchMatrixInAnyRenderer();
        canvas.pushStyle();
        canvas.imageMode(CORNER);
        canvas.image(pg, 0, 0);
        canvas.popStyle();
        State.updateEndlessLoopDetection();
        takeScreenshotIfNeeded();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isAutoRepeat()) {
            return;
        }
        tryHandleHotkeyInteraction(keyEvent);
    }

    /**
     * Utility function that can help the library user tell if a mouse press collided and interacted with the GUI.
     * For example with a mouse controlled brush you might not want to keep drawing on the main canvas
     * when you just want to adjust its properties in the GUI and don't expect that to affect the artwork.
     * The GUI cannot make the mousePressed() method stop getting called in the processing sketch,
     * so a utility function is needed to help decide what to do.
     *
     * @return whether you should use this mouse press in the processing sketch
     */
    public boolean mousePressedOutsideGui() {
        return State.app.mousePressed && UserInputPublisher.mouseFallsThroughThisFrame;
    }

    /**
     * Gets the value of a float slider, lazily initializes it if needed and uses a default value of 0.
     *
     * @param path forward slash separated unique path to the slider
     * @return current float value of the slider
     */
    public float slider(String path) {
        return slider(path, 0, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    /**
     * Gets the value of a float slider, lazily initializes it if needed and uses a specified default value.
     *
     * @param path forward slash separated unique path to the slider
     * @param defaultValue default value to set the slider to
     * @return current float value of the slider
     */
    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    /**
     * Gets the value of a float slider, lazily initializes it if needed and uses a default value specified in the parameter
     * along with enforcing a minimum and maximum of reachable values.
     *
     * @param path forward slash separated unique path to the slider
     * @param defaultValue the default value, ideally between min and max
     * @param min the value cannot go below this, min < max must be true
     * @param max the value cannot go above this, max > min must be true
     * @return current float value of the slider
     */
    public float slider(String path, float defaultValue, float min, float max) {
        return slider(path, defaultValue, min, max, true);
    }

    private float slider(String path, float defaultValue, float min, float max, boolean constrained) {
        SliderNode node = (SliderNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderNode(path, defaultValue, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueFloat;
    }

    private SliderNode createSliderNode(String path, float defaultValue, float min, float max, boolean constrained) {
        NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
        SliderNode node = new SliderNode(path, folder, defaultValue, min, max, 0.1f, constrained);
        node.initSliderBackgroundShader();
        return node;
    }

    /**
     * Sets the value of a slider manually without user interaction in the sketch.
     * Still allows user interaction after it's set.
     * Initializes a new float slider at the given path if needed
     * with the value param used as a default value and with no constraint on min and max value.
     *
     * @param path forward slash separated unique path to the slider
     * @param value value to set the float slider at the path to
     */
    public void sliderSet(String path, float value){
        SliderNode node = (SliderNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderNode(path, value, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    /**
     * // TODO javadocs
     * @param path
     * @return
     */
    public int sliderInt(String path) {
        return sliderInt(path, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    /**
     *
     * @param path
     * @param defaultValue
     * @return
     */
    public int sliderInt(String path, int defaultValue) {
        return sliderInt(path, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    /**
     *
     * @param path
     * @param defaultValue
     * @param min
     * @param max
     * @return
     */
    public int sliderInt(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    private int sliderInt(String path, int defaultValue, int min, int max, boolean constrained) {
        SliderIntNode node = (SliderIntNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderIntNode(path, defaultValue, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return PApplet.floor(node.valueFloat);
    }

    private SliderIntNode createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
        SliderIntNode node = new SliderIntNode(path, folder, defaultValue, min, max, 0.1f, constrained);
        node.initSliderBackgroundShader();
        return node;
    }

    /**
     *
     * @param path
     * @param value
     */
    public void sliderIntSet(String path, int value){
        SliderIntNode node = (SliderIntNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderIntNode(path, value, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    /**
     *
     * @param path
     * @return
     */
    public boolean toggle(String path) {
        return toggle(path, false);
    }

    /**
     *
     * @param path
     * @param defaultValue
     * @return
     */
    public boolean toggle(String path, boolean defaultValue) {
        ToggleNode node = (ToggleNode) NodeTree.findNode(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    /**
     *
     * @param path
     * @param value
     */
    public void toggleSet(String path, boolean value) {
        ToggleNode node = (ToggleNode) NodeTree.findNode(path);
        if (node == null) {
            node = createToggleNode(path, value);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueBoolean = value;
    }

    private ToggleNode createToggleNode(String path, boolean defaultValue) {
        NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
        return new ToggleNode(path, folder, defaultValue);
    }

    /**
     *
     * @param path
     * @return
     */
    public boolean button(String path) {
        ButtonNode node = (ButtonNode) NodeTree.findNode(path);
        if (node == null) {
            node = createButtonNode(path);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getBooleanValueAndSetItToFalse();
    }

    private ButtonNode createButtonNode(String path) {
        NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
        return new ButtonNode(path, folder);
    }

    /**
     *
     * @param path
     * @param options
     * @return
     */
    public String stringPicker(String path, ArrayList<String> options) {
        return stringPicker(path, options.toArray(new String[0]), null);
    }

    /**
     *
     * @param path
     * @param options
     * @param defaultOption
     * @return
     */
    public String stringPicker(String path, ArrayList<String> options, String defaultOption) {
        return stringPicker(path, options.toArray(new String[0]), defaultOption);
    }

    /**
     *
     * @param path
     * @param options
     * @return
     */
    public String stringPicker(String path, String[] options) {
        return stringPicker(path, options, null);
    }

    /**
     *
     * @param path
     * @param options
     * @param defaultOption
     * @return
     */
    public String stringPicker(String path, String[] options, String defaultOption) {
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("options parameter must not be null nor empty");
        }
        StringPickerFolder node = (StringPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new StringPickerFolder(path, parentFolder, options, defaultOption);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueString;
    }

    /**
     *
     * @param path
     * @param optionToSet
     */
    public void stringPickerSet(String path, String optionToSet){
        StringPickerFolder node = (StringPickerFolder) NodeTree.findNode(path);
        if (node != null) {
            List<String> options = node.getOptions();
            if(options.contains(optionToSet)){
                node.selectOption(optionToSet);
            }else{
                println("attempted to set an option: " + optionToSet +
                    " to a string picker at path: " + path +
                    " which does not appear in the options: " + options);
            }
        }
    }

    /**
     *
     * @param theme
     */
    public void setTheme(Theme theme) {
        ThemeStore.currentSelection = ThemeType.CUSTOM;
        ThemeStore.setCustomPalette(theme);
    }

    /**
     *
     * @param path
     * @return
     */
    public PickerColor colorPicker(String path) {
        return colorPicker(path, 1, 1, 0, 1);
    }

    /**
     *
     * @param path
     * @param grayNorm
     * @return
     */
    public PickerColor colorPicker(String path, float grayNorm) {
        return colorPicker(path, grayNorm, grayNorm, grayNorm, 1);
    }

    /**
     *
     * @param path
     * @param hueNorm
     * @param saturationNorm
     * @param brightnessNorm
     * @return
     */
    public PickerColor colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm) {
        return colorPicker(path, hueNorm, saturationNorm, brightnessNorm, 1);
    }

    /**
     *
     * @param path
     * @param hueNorm
     * @param saturationNorm
     * @param brightnessNorm
     * @param alphaNorm
     * @return
     */
    public PickerColor colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm) {
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            int hex = State.normalizedColorProvider.color(hueNorm, saturationNorm, brightnessNorm, 1);
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    /**
     *
     * @param path
     * @param hex
     * @return
     */
    public PickerColor colorPicker(String path, int hex) {
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    /**
     *
     * @param path
     * @param hex
     */
    public void colorPickerSet(String path, int hex) {
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        } else {
            node.setHex(hex);
            node.loadValuesFromHex(false);
        }
    }

    /**
     *
     * @param path
     * @return
     */
    public PGraphics gradient(String path) {
        return gradient(path, 1);
    }

    /**
     *
     * @param path
     * @param alpha
     * @return
     */
    public PGraphics gradient(String path, float alpha) {
        GradientFolder node = (GradientFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new GradientFolder(path, parentFolder, alpha);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getOutputGraphics();
    }

    void requestScreenshot(String customPath){
        screenshotRequestedOnMainThreadWithCustomPath = true;
        requestedScreenshotCustomPath = customPath;
    }

    private void updateAllNodeValuesRegardlessOfParentWindowOpenness() {
        List<AbstractNode> allNodes = NodeTree.getAllNodesAsList();
        for(AbstractNode node : allNodes){
            node.updateValues();
        }
    }

    private void takeScreenshotIfNeeded() {
        if (!screenshotRequestedOnMainThread && !screenshotRequestedOnMainThreadWithCustomPath) {
            return;
        }
        String randomId = Utils.generateRandomShortId();
        String folderPath = "out/screenshots/";
        String filetype = ".png";
        String filePath = folderPath + State.app.getClass().getSimpleName() + " " + randomId + filetype;

        if(screenshotRequestedOnMainThreadWithCustomPath){
            filePath = requestedScreenshotCustomPath;
        }

        State.app.save(filePath);
        screenshotRequestedOnMainThread = false;
        screenshotRequestedOnMainThreadWithCustomPath = false;
    }

    void createOptionsFolder() {
        String path = "options";
        toolbar = new NodeFolder(path, NodeTree.getRoot());
        NodeTree.insertNodeAtItsPath((toolbar));
        NodeTree.insertNodeAtItsPath(new SaveNodeFolder(path + "/saves", toolbar));
        updateThemePicker(toolbar.path + "/themes");

    }

    private void updateOptionsFolder() {
        String path = toolbar.path;
        updateThemePicker(path + "/themes");
        hotkeyHideActive = toggle(path + "/hotkeys/h: hide gui", true);
        hotkeyCloseAllWindowsActive = toggle(path + "/hotkeys/d: close all windows", true);
        hotkeyScreenshotActive = toggle(path + "/hotkeys/s: take screenshot", true);
        undoHotkeyActive = toggle(path + "/hotkeys/ctrl + z: undo", true);
        redoHotkeyActive = toggle(path + "/hotkeys/ctrl + y: redo", true);
        saveHotkeyActive = toggle(path + "/hotkeys/ctrl + s: new save", true);
        drawPathTooltips = toggle(path + "/window path tooltips", true);
    }

    private void tryHandleHotkeyInteraction(KeyEvent keyEvent) {
        char key = keyEvent.getKeyChar();
        int keyCode = keyEvent.getKeyCode();
        if (key == 'h' && hotkeyHideActive) {
            isGuiHidden = !isGuiHidden;
        }
        screenshotRequestedOnMainThread = (key == 's' && hotkeyScreenshotActive);
        if(key == 'd' && hotkeyCloseAllWindowsActive){
            WindowManager.closeAllWindows();
        }
        if(keyCode == KeyCodes.KEY_CODE_CTRL_Z && undoHotkeyActive){
            State.undo();
        }
        if(keyCode == KeyCodes.KEY_CODE_CTRL_Y && redoHotkeyActive){
            State.redo();
        }
        if(keyCode == KeyCodes.KEY_CODE_CTRL_S && saveHotkeyActive){
            State.createNewSaveWithRandomName();
        }
    }

    private void updateThemePicker(String path) {
        String defaultPaletteName = "dark";
        Theme defaultTheme = ThemeType.getPalette(ThemeType.DARK);
        assert defaultTheme != null;

        String userSelection = State.gui.stringPicker(path + "/gui theme", ThemeType.getAllNames(), defaultPaletteName);
        if (!userSelection.equals(ThemeType.getName(ThemeStore.currentSelection))) {
            ThemeStore.currentSelection = ThemeType.getValue(userSelection);
        }
        String customDefinitionPath = path + "/custom theme editor";
        ThemeStore.setCustomColor(ThemeColorType.FOCUS_FOREGROUND,
                State.gui.colorPicker(customDefinitionPath + "/focus foreground", defaultTheme.focusForeground).hex);
        ThemeStore.setCustomColor(ThemeColorType.FOCUS_BACKGROUND,
                State.gui.colorPicker(customDefinitionPath + "/focus background", defaultTheme.focusBackground).hex);
        ThemeStore.setCustomColor(ThemeColorType.NORMAL_FOREGROUND,
                State.gui.colorPicker(customDefinitionPath + "/normal foreground", defaultTheme.normalForeground).hex);
        ThemeStore.setCustomColor(ThemeColorType.NORMAL_BACKGROUND,
                State.gui.colorPicker(customDefinitionPath + "/normal background", defaultTheme.normalBackground).hex);
        ThemeStore.setCustomColor(ThemeColorType.WINDOW_BORDER,
                State.gui.colorPicker(customDefinitionPath + "/window border", defaultTheme.windowBorder).hex);
    }
}
