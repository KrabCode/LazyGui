package lazy;


import lazy.input.LazyKeyEvent;
import lazy.input.UserInputPublisher;
import lazy.input.UserInputSubscriber;
import lazy.nodes.*;
import lazy.stores.*;
import lazy.themes.Theme;
import lazy.themes.ThemeStore;
import lazy.themes.ThemeType;
import lazy.utils.ContextLines;
import lazy.utils.KeyCodes;
import lazy.utils.SnapToGrid;
import lazy.utils.JsonSaves;
import lazy.windows.Window;
import lazy.windows.WindowManager;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static lazy.stores.NormColorStore.color;
import static lazy.stores.GlobalReferences.app;
import static lazy.stores.NodeTree.getAllNodesAsList;
import static lazy.stores.LayoutStore.*;
import static lazy.utils.JsonSaves.getGuiDataFolderPath;
import static lazy.utils.JsonSaves.getNextUnusedIntegerFileNameInFolder;
import static processing.core.PApplet.*;

/**
 * Main class for controlling the GUI from a processing sketch.
 * Should be initialized as a global variable in processing setup() function with new LazyGui(this).
 * Registers itself at end of the draw() method and displays the GUI whenever draw() ends.
 * Allows the library user to get the value of a gui control element at any time inside draw(), even repeatedly inside loops.
 * If the control element does not exist yet at the time its value is requested it gets newly created just in time.
 * Please note that only one LazyGui object is expected to exist in any given sketch, which is not enforced, it just won't work.
 */
@SuppressWarnings("unused")
public class LazyGui implements UserInputSubscriber {

    private static int lastFrameCountGuiWasShown = -1;
    public static boolean isGuiHidden = false;
    private static boolean screenshotRequestedOnMainThread = false;
    private static boolean screenshotRequestedOnMainThreadWithCustomPath = false;
    private static String requestedScreenshotCustomFilePath = "";
    private static boolean hotkeyHideActive, hotkeyUndoActive, hotkeyRedoActive, hotkeyScreenshotActive,
            hotkeyCloseAllWindowsActive, hotkeySaveActive, hotkeyOpenSketchFolderActive;

    ArrayList<String> pathPrefix = new ArrayList<>();
    int stackSizeWarningLevel = 64;
    private boolean printedPushWarningAlready = false;
    private boolean printedPopWarningAlready = false;

    private PGraphics pg;
    FolderNode optionsNode;

    private static long lastFrameMillis;
    static final long lastFrameMillisStuckLimit = 1000;


    /**
     * Constructor for the LazyGui object which acts as an entry point to the entire LazyGui library.
     * Meant to be initialized in setup() with <code>new LazyGui(this)</code>.
     * Registers itself at end of the draw() method and displays the GUI whenever draw() ends.
     *
     * @param sketch main processing sketch class to display the GUI on and use keyboard and mouse input from
     */
    public LazyGui(PApplet sketch) {
        GlobalReferences.init(this, sketch);
        if (!app.sketchRenderer().equals(P2D) && !app.sketchRenderer().equals(P3D)) {
            println("The LazyGui library requires the P2D or P3D renderer.");
        }
        NormColorStore.init();
        FontStore.lazyUpdateFont();
        ThemeStore.initSingleton();
        UserInputPublisher.initSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.addWindow(new Window(NodeTree.getRoot(), cell, cell, null));
        createOptionsFolder();
        JsonSaves.loadMostRecentSave();
        lazyFollowSketchResolution();
        registerExitHandler();
        app.registerMethod("draw", this);
    }

    void lazyFollowSketchResolution() {
        if (pg == null || pg.width != app.width || pg.height != app.height) {
            pg = app.createGraphics(app.width, app.height, P2D);
            pg.colorMode(HSB, 1, 1, 1, 1);
//            pg.noSmooth();
            pg.smooth(8);
        }
    }

    /**
     * Utility method for displaying the GUI before draw() ends for the purposes of recording.
     * Does not update the gui, only returns the previous frame's gui canvas.
     * Can be confusing when displayed due to seeing duplicated GUI images with slightly different content.
     * @return previous frame's gui canvas
     */
    public PGraphics getGuiCanvas() {
        return pg;
    }

    /**
     * Updates and draws the GUI on the main processing canvas.
     * Gets called automatically at the end of draw().
     * Must stay public because otherwise this registering won't work: app.registerMethod("draw", this);
     */
    public void draw() {
        draw(app.g);
    }

    /**
     * Updates and draws the GUI on the specified parameter canvas, assuming its size is identical to the main sketch size.
     * Gets called automatically at the end of draw().
     * LazyGui will enforce itself being drawn only once per frame internally, which can be useful for including the gui in a recording.
     * If it does get called manually, it will get drawn when requested and then skip its automatic execution for that frame.
     *
     * @param canvas canvas to draw the GUI on
     */
    public void draw(PGraphics canvas) {
        if(lastFrameCountGuiWasShown == app.frameCount){
            return;
        }
        lastFrameCountGuiWasShown = app.frameCount;
        lazyFollowSketchResolution();
        updateAllNodeValuesRegardlessOfParentWindowOpenness();
        pg.beginDraw();
        pg.clear();
        clearFolder();
        updateOptionsFolder();
        SnapToGrid.displayGuideAndApplyFilter(pg, getWindowBeingDraggedIfAny());
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
        pg.endDraw();
        resetSketchMatrixInAnyRenderer();
        canvas.pushStyle();
        canvas.imageMode(CORNER);
        canvas.image(pg, 0, 0);
        canvas.popStyle();
        takeScreenshotIfRequested();
        updateEndlessLoopDetection();
    }

    static void resetSketchMatrixInAnyRenderer() {
        if (app.sketchRenderer().equals(P3D)) {
            app.camera();
        } else {
            app.resetMatrix();
        }
    }

    private Window getWindowBeingDraggedIfAny() {
        List<AbstractNode> allNodes = getAllNodesAsList();
        for(AbstractNode node : allNodes){
            if(node.type == NodeType.FOLDER){
                FolderNode folder = (FolderNode) node;
                if(folder.window != null && folder.window.isBeingDraggedAround){
                    return folder.window;
                }
            }
        }
        return null;
    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param keyEvent current key event
     */
    @Override
    public void keyPressed(LazyKeyEvent keyEvent) {
        tryHandleHotkeyInteraction(keyEvent);
    }

    /**
     * Utility function to tell if a mouse press collided and interacted with the GUI.
     * For example with a mouse controlled brush you might not want to keep drawing on the main canvas
     * when you just want to adjust its properties in the GUI and you don't expect that to affect the artwork.
     * The GUI cannot make the mousePressed() method stop getting called in the processing sketch,
     * so a utility function is needed to help the library user decide what to do.
     *
     * @return whether you should use this mouse press in the processing sketch
     */
    public boolean mousePressedOutsideGui() {
        return app.mousePressed && UserInputPublisher.mouseFallsThroughThisFrame;
    }

    /**
     * Sets the sketch theme to a custom palette manually from code.
     * Meant to be used once in setup after initializing the LazyGui using a new Theme object with the desired hex colors.
     *
     * @param theme custom theme to be used
     */
    public void setTheme(Theme theme) {
        ThemeStore.currentSelection = ThemeType.CUSTOM;
        ThemeStore.setCustomPalette(theme);
    }

    /**
     * Gets the value of a float slider control element.
     * lazily initializes it if needed and uses a default value of 0.
     *
     * @param path forward slash separated unique path to the control element
     * @return current float value of the slider
     */
    public float slider(String path) {
        return slider(path, 0, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    /**
     * Gets the value of a float slider control element.
     * lazily initializes it if needed and uses a specified default value.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultValue default value to set the slider to
     * @return current float value of the slider
     */
    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    /**
     * Gets the value of a float slider control element.
     * lazily initializes it if needed and uses a default value specified in the parameter.
     * along with enforcing a minimum and maximum of reachable values.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultValue the default value, ideally between min and max
     * @param min the value cannot go below this, min &lt; max must be true
     * @param max the value cannot go above this, max &gt; min must be true
     * @return current float value of the slider
     */
    public float slider(String path, float defaultValue, float min, float max) {
        return slider(path, defaultValue, min, max, true);
    }

    private float slider(String path, float defaultValue, float min, float max, boolean constrained) {
        String fullPath = getFolder() + path;
        SliderNode node = (SliderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, defaultValue, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueFloat;
    }

    private SliderNode createSliderNode(String path, float defaultValue, float min, float max, boolean constrained) {
        FolderNode folder = NodeTree.findParentFolderLazyInitPath(path);
        SliderNode node = new SliderNode(path, folder, defaultValue, min, max, constrained);
        node.initSliderBackgroundShader();
        return node;
    }

    /**
     * Sets the value of a float slider control element manually at runtime without requiring user interaction.
     * Does not block changing the value in the future in any way.
     * Initializes a new float slider at the given path if needed with the value parameter used as a default value and with no constraint on min and max value.
     *
     * @param path forward slash separated unique path to the control element
     * @param value value to set the float slider at the path to
     */
    public void sliderSet(String path, float value){
        String fullPath = getFolder() + path;
        SliderNode node = (SliderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, value, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    /**
     * Adds a float to the value of a float or int slider control element manually at runtime without requiring user interaction.
     * Does not block changing the value in the future in any way.
     * Initializes a new float slider at the given path if needed with default value 0 and with no constraint on min and max value.
     *
     * @param path forward slash separated unique path to the control element
     * @param amountToAdd value to set the float slider at the path to
     */
    public void sliderAdd(String path, float amountToAdd){
        String fullPath = getFolder() + path;
        SliderNode node = (SliderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, 0, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat += amountToAdd;
    }

    /**
     * Gets the value of an integer slider control element.
     * lazily initializes it if needed and uses a default value of 0.
     *
     * @param path forward slash separated unique path to the control element
     * @return current float value of the slider
     */
    public int sliderInt(String path) {
        return sliderInt(path, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    /**
     * Gets the value of an integer slider control element.
     * lazily initializes it if needed and uses the specified default value.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultValue default value to set the slider to
     * @return current float value of the slider
     */
    public int sliderInt(String path, int defaultValue) {
        return sliderInt(path, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    /**
     * Gets the value of an integer slider control element.
     * lazily initializes it if needed and uses a default value specified in the parameter.
     * along with enforcing a minimum and maximum of reachable values.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultValue the default value, ideally between min and max
     * @param min the value cannot go below this, min &lt; max must be true
     * @param max the value cannot go above this, max &gt; min must be true
     * @return current float value of the slider
     */
    public int sliderInt(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    private int sliderInt(String path, int defaultValue, int min, int max, boolean constrained) {
        String fullPath = getFolder() + path;
        SliderIntNode node = (SliderIntNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createSliderIntNode(fullPath, defaultValue, min, max, constrained);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getIntValue();
    }

    private SliderIntNode createSliderIntNode(String path, int defaultValue, int min, int max, boolean constrained) {
        FolderNode folder = NodeTree.findParentFolderLazyInitPath(path);
        SliderIntNode node = new SliderIntNode(path, folder, defaultValue, min, max, constrained);
        node.initSliderBackgroundShader();
        return node;
    }

    /**
     * Sets the value of an integer slider control element manually at runtime without requiring user interaction.
     * Initializes a new float slider at the given path if needed.
     * Does not block changing the value in the future in any way.
     * with the value param used as a default value and with no constraint on min and max value.
     *
     * @param path forward slash separated unique path to the control element
     * @param value value to set the float slider at the path to
     */
    public void sliderIntSet(String path, int value){
        String fullPath = getFolder() + path;
        SliderIntNode node = (SliderIntNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createSliderIntNode(fullPath, value, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    /**
     * Gets the vector value of a 2D control element.
     * Lazily initializes it if needed and sets all values to 0 by default.
     *
     * @param path forward slash separated unique path to the plot control element
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path){
        return plotXYZ(path, null, false);
    }

    /**
     * Gets the vector value of a 2D control element.
     * Lazily initializes it if needed and sets its values to the parameter default.
     *
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXY default xy value
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path, float defaultXY){
        return plotXYZ(path, new PVector(defaultXY, defaultXY), false);
    }

    /**
     * Gets the vector value of a 2D control element.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     *
     * @param path forward slash separated unique path to the plot control element
     * @param defaultX default x value
     * @param defaultY default y value
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path, float defaultX, float defaultY){
        return plotXYZ(path, new PVector(defaultX, defaultY), false);
    }

    /**
     * Gets the vector value of a 2D control element.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXY default xy values, z value is ignored
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path, PVector defaultXY){
        return plotXYZ(path, defaultXY == null ? new PVector() : defaultXY, false);
    }

    /**
     * Gets the vector value of a 2D control element with an extra z slider.
     * Lazily initializes it if needed and sets its xyz values to 0 by default.
     * @param path forward slash separated unique path to the plot control element
     * @return current xyz values
     */
    public PVector plotXYZ(String path){
        return plotXYZ(path, null, true);
    }


    /**
     * Gets the vector value of a 2D control element with an extra z slider.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXYZ default xyz values
     * @return current xyz values
     */
    public PVector plotXYZ(String path, float defaultXYZ){
        return plotXYZ(path, new PVector(defaultXYZ, defaultXYZ, defaultXYZ), true);
    }


    /**
     * Gets the vector value of a 2D control element with an extra z slider.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultX default x value
     * @param defaultY default y value
     * @param defaultZ default z value
     * @return current xyz values
     */
    public PVector plotXYZ(String path, float defaultX, float defaultY, float defaultZ){
        return plotXYZ(path, new PVector(defaultX, defaultY, defaultZ), true);
    }

    /**
     * Gets the vector value of a 2D control element with an extra z slider.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXYZ default xyz values
     * @return current xyz values
     */
    public PVector plotXYZ(String path, PVector defaultXYZ){
        return plotXYZ(path, defaultXYZ == null ? new PVector() : defaultXYZ.copy(), true);
    }

    private PVector plotXYZ(String path, PVector defaultXYZ, boolean useZ){
        String fullPath = getFolder() + path;
        PlotFolderNode node = (PlotFolderNode) NodeTree.findNode(fullPath);
        if(node == null){
            node = createPlotNode(fullPath, defaultXYZ, useZ);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getVectorValue();
    }

    /**
     * TODO javadocs
     * @param path
     * @param xyz
     */
    public void plotSet(String path, float xyz){
        plotSet(path, new PVector(xyz, xyz, xyz));
    }

    public void plotSet(String path, float x, float y){
        plotSet(path, new PVector(x,y));
    }

    public void plotSet(String path, float x, float y, float z){
        plotSet(path, new PVector(x,y,z));
    }

    public void plotSet(String path, PVector valueToSet){
        String fullPath = getFolder() + path;
        PlotFolderNode node = (PlotFolderNode) NodeTree.findNode(fullPath);
        if(node == null){
            node = createPlotNode(fullPath, valueToSet, true);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.setVectorValue(valueToSet.x, valueToSet.y, valueToSet.z);
    }

    private PlotFolderNode createPlotNode(String fullPath, PVector defaultXY, boolean useZ) {
        FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
        return new PlotFolderNode(fullPath, folder, defaultXY, useZ);
    }

    /**
     * Gets the current value of a toggle control element.
     * lazily initializes it if needed and sets its value to false by default.
     *
     * @param path forward slash separated unique path to the control element.
     * @return current value of the toggle
     */
    public boolean toggle(String path) {
        return toggle(path, false);
    }

    /**
     * Gets the current value of a toggle control element.
     * lazily initializes it if needed and sets its value to the specified parameter default.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultValue default value of the toggle
     * @return current value of the toggle
     */
    public boolean toggle(String path, boolean defaultValue) {
        String fullPath = getFolder() + path;
        ToggleNode node = (ToggleNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createToggleNode(fullPath, defaultValue);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

    /**
     * Sets the value of a boolean toggle control element.
     * Does not block changing the value in the future in any way.
     * Lazily initializes the toggle if needed.
     *
     * @param path forward slash separated unique path to the control element
     * @param value current value of the toggle
     */
    public void toggleSet(String path, boolean value) {
        String fullPath = getFolder() + path;
        ToggleNode node = (ToggleNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createToggleNode(fullPath, value);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueBoolean = value;
    }

    private ToggleNode createToggleNode(String path, boolean defaultValue) {
        FolderNode folder = NodeTree.findParentFolderLazyInitPath(path);
        return new ToggleNode(path, folder, defaultValue);
    }

    /**
     * Gets the value of a button control element and sets it to false.
     * Lazily initializes the button if needed.
     * Meant to be used inside draw() like this:
     * <pre>
     * if(gui.button("actions/print")){
     *     println("hello world");
     * }
     * </pre>
     *
     * @param path forward slash separated unique path to the control element
     * @return button value that can only be true once per user interaction
     */
    public boolean button(String path) {
        String fullPath = getFolder() + path;
        ButtonNode node = (ButtonNode) NodeTree.findNode(fullPath);
        if (node == null) {
            node = createButtonNode(fullPath);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getBooleanValueAndSetItToFalse();
    }

    private ButtonNode createButtonNode(String path) {
        FolderNode folder = NodeTree.findParentFolderLazyInitPath(path);
        return new ButtonNode(path, folder);
    }

    /**
     * Gets the currently selected string from a list of options in a gui control element.
     * Lazily initializes the radio element if needed - any later changes in the options parameter will be ignored.
     * Sets the default value to the first value in the list.
     *
     * @param path forward slash separated unique path to the control element
     * @param options list of options to display
     * @return currently selected string
     */
    public String radio(String path, List<String> options) {
        return radio(path, options.toArray(new String[0]), null);
    }

    /**
     * Gets the currently selected string from a list of options in a gui control element.
     * Lazily initializes the radio element if needed - any later changes in the options parameter will be ignored.
     * Sets the default value to the specified parameter value, which must be contained in the options list, or it will be ignored.
     *
     * @param path forward slash separated unique path to the control element
     * @param options list of options to display
     * @param defaultOption default option to select, which must also be found in the options, or it will be ignored
     * @return currently selected string
     */
    public String radio(String path, List<String> options, String defaultOption) {
        return radio(path, options.toArray(new String[0]), defaultOption);
    }

    /**
     * Gets the currently selected string from an array of options in a gui control element.
     * Lazily initializes the radio element if needed - any later changes in the options parameter will be ignored.
     * Sets the default value to the specified parameter value, which must be contained in the options array, or it will be ignored.
     *
     * @param path forward slash separated unique path to the control element
     * @param options list of options to display
     * @return currently selected string
     */
    public String radio(String path, String[] options) {
        return radio(path, options, null);
    }

    /**
     * Gets the currently selected string from an array of options in a gui control element.
     * Lazily initializes the radio element if needed - any later changes in the options parameter will be ignored.
     * Sets the default value to the specified parameter value, which must be contained in the options array, or it will be ignored.
     *
     * @param path forward slash separated unique path to the control element
     * @param options list of options to display
     * @param defaultOption default option to select, must also be found in options, or it is ignored
     * @return currently selected string
     */
    public String radio(String path, String[] options, String defaultOption) {
        String fullPath = getFolder() + path;
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("options parameter must not be null nor empty");
        }
        RadioFolderNode node = (RadioFolderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            FolderNode parentFolder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new RadioFolderNode(fullPath, parentFolder, options, defaultOption);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueString;
    }

    /**
     * Sets the radio element value to a given parameter.
     * Does not lazily initialize the radio element because it doesn't know what all the options should be.
     * If the option to set is not found in the radio's existing list of options - it is ignored and a warning message is printed to console.
     *
     * @param path forward slash separated unique path to the control element
     * @param optionToSet string option to set the radio element to
     */
    public void radioSet(String path, String optionToSet){
        String fullPath = getFolder() + path;
        RadioFolderNode node = (RadioFolderNode) NodeTree.findNode(fullPath);
        if (node != null) {
            List<String> options = node.getOptions();
            if(options.contains(optionToSet)){
                node.selectOption(optionToSet);
            }else{
                println("attempted to set an option: " + optionToSet +
                    " to a radio element at path: " + fullPath +
                    " which does not appear in the options: " + options);
            }
        }
    }

    /**
     * Gets the color value of a color picker control element.
     * Lazily initializes the color picker if needed.
     * Default hsba values are (1,1,0,1) - therefore default color is black.
     *
     * @param path forward slash separated unique path to the control element
     * @return current hex and hsba values in a PickerColor object
     */
    public PickerColor colorPicker(String path) {
        return colorPicker(path, 1, 1, 0, 1);
    }

    /**
     * Gets the color value of a color picker control element.
     * Lazily initializes the color picker if needed with the specified grayNorm brightness in the range [0,1].
     * Default hsba values are (0,0,grayNorm,1).
     *
     * @param path forward slash separated unique path to the control element
     * @param grayNorm default brightness in the range [0,1]
     * @return current hex and hsba values in a PickerColor object
     */
    public PickerColor colorPicker(String path, float grayNorm) {
        return colorPicker(path, 0, 0, grayNorm, 1);
    }

    /**
     * Gets the color value of a color picker control element.
     * Lazily initializes the color picker if needed with the specified defaults.
     *
     * @param path forward slash separated unique path to the control element
     * @param hueNorm default hue in the range [0,1]
     * @param saturationNorm default saturation in the range [0,1]
     * @param brightnessNorm default brightness in the range [0,1]
     * @return current hex and hsba values in a PickerColor object
     */
    public PickerColor colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm) {
        return colorPicker(path, hueNorm, saturationNorm, brightnessNorm, 1);
    }

    /**
     * Gets the color value of a color picker control element.
     * Lazily initializes the color picker if needed with the specified defaults.
     *
     * @param path forward slash separated unique path to the control element
     * @param hueNorm default hue in the range [0,1]
     * @param saturationNorm default saturation in the range [0,1]
     * @param brightnessNorm default brightness in the range [0,1]
     * @param alphaNorm default alpha in the range [0,1]
     * @return current hex and hsba values in a PickerColor object
     */
    public PickerColor colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm) {
        return colorPicker(path, color(hueNorm, saturationNorm, brightnessNorm, alphaNorm));
    }

    /**
     * Gets the color value of a color picker control element.
     * Lazily initializes the color picker if needed with the parameter hex value as default color.
     *
     * @param path forward slash separated unique path to the control element
     * @param hex hex color as an integer like 0xFF123456, also works with processing 'color' type
     * @return hex and hsba values in a PickerColor object
     */
    public PickerColor colorPicker(String path, int hex) {
        String fullPath = getFolder() + path;
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(fullPath, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    /**
     * Sets the color picker to a given hex value.
     * Lazily initializes the color picker if needed with the parameter hex value as default color.
     * Does not block changing the value in the future in any way.
     *
     * @param path forward slash separated unique path to the control element
     * @param hex hex color to set, also works with processing 'color' type
     */
    public void colorPickerSet(String path, int hex) {
        String fullPath = getFolder() + path;
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(fullPath, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        } else {
            node.setHex(hex);
            node.loadValuesFromHex(false);
        }
    }

    /**
     * Adds hue to the color picker, looping it correctly both in both directions.
     * Lazily initializes the color picker if needed with default color 0xFF000000 (full alpha black).
     * Does not block changing the value in the future in any way.
     *
     * @param path forward slash separated unique path to the control element
     * @param hueToAdd hue to add, with the hue value being normalized to the range [0,1]
     */
    public void colorPickerHueAdd(String path, float hueToAdd) {
        String fullPath = getFolder() + path;
        ColorPickerFolderNode node = (ColorPickerFolderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(path, folder, NormColorStore.color(0,1));
            NodeTree.insertNodeAtItsPath(node);
        } else {
            node.setHue(hueToAdd);
        }
    }

    /**
     * Hue values loop at the 1 - 0 border both in the positive and negative direction, just like two pi loops back to 0.
     * @param hue value to transfer to the [0-1] range without changing apparent color value
     * @return hue in the range between 0-1
     */
    public static float hueModulo(float hue){
        if (hue < 0.f){
            return hue % 1f + 1f;
        } else {
            return hue % 1f;
        }
    }

    /**
     * Gets the current value of a text input element.
     * Lazily initializes the text input element if needed with its content set to an empty string.
     *
     * @param path forward slash separated unique path to the control element
     * @return current value of a string input element
     */
    public String text(String path){
        return text(path, "");
    }

    /**
     * Gets the current value of an editable text field element.
     * Lazily initializes the string input if needed with the specified default.
     *
     * @param path forward slash separated unique path to the control element
     * @param content default value for the text content
     * @return current value of a string input element
     */
    public String text(String path, String content){
        return getText(path, content).getStringValue();
    }

    /**
     * Sets the current value of an editable text field element.
     * Lazily initializes the text input element if needed and then sets its value to the specified content value.
     *
     * @param path forward slash separated unique path to the control element
     * @param content default value for the text content
     */
    public void textSet(String path, String content){
        getText(path, content).setStringValue(content);
    }

    public TextNode getText(String path, String content){
        String fullPath = getFolder() + path;
        TextNode node = (TextNode) NodeTree.findNode(fullPath);
        if(node == null){
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new TextNode(fullPath, folder, content);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node;
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed.
     *
     * @param path forward slash separated unique path to the control element
     * @return PGraphics after endDraw() - ready to be displayed as an image
     */
    public PGraphics gradient(String path) {
        return gradient(path, 1);
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed with the alpha parameter as default alpha for all its colors.
     *
     * @param path forward slash separated unique path to the control element
     * @param alpha default alpha of all the colors in the gradient
     * @return PGraphics after endDraw() - ready to be displayed as an image
     */
    public PGraphics gradient(String path, float alpha) {
        String fullPath = getFolder() + path;
        GradientFolderNode node = (GradientFolderNode) NodeTree.findNode(fullPath);
        if (node == null) {
            FolderNode parentFolder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new GradientFolderNode(fullPath, parentFolder, alpha);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getOutputGraphics();
    }


    /**
     * Pushes a folder name to the global path prefix stack.
     * Can be used multiple times in pairs just like pushMatrix and popMatrix().
     * Removes leading and trailing slashes to enforce consistency, but allows slashes to appear either escaped or anywhere else inside of the string.
     * Any GUI control element call will apply all the folders in the stack as a prefix to their own path parameter.
     * This is useful for not repeating the whole path string every time you want to call a control element.
     *
     * @param folderName one folder's name to push to the stack
     * @see LazyGui#getFolder()
     */
    public void pushFolder(String folderName){
        if(pathPrefix.size() >= stackSizeWarningLevel && !printedPushWarningAlready){
            println("Too many calls to pushFolder() - stack size reached " + stackSizeWarningLevel +
                    ", possibly due to runaway recursion");
            printedPushWarningAlready = true;
        }
        String slashSafeFolderName = folderName;
        if(slashSafeFolderName.startsWith("/")){
            // remove leading slash
            slashSafeFolderName = slashSafeFolderName.substring(1);
        }
        if(slashSafeFolderName.endsWith("/") && !slashSafeFolderName.endsWith("\\/")){
            // remove trailing slash if un-escaped
            slashSafeFolderName = slashSafeFolderName.substring(0, slashSafeFolderName.length()-1);
        }
        pathPrefix.add(0, slashSafeFolderName);
    }

    /**
     * Pops the last pushed folder name from the global path prefix stack.
     * Can be used multiple times in pairs just like pushMatrix and popMatrix().
     * Warns once when the stack is empty and popFolder() is attempted.
     * Any GUI control element call will apply all the folders in the stack as a prefix to their own path parameter.
     * This is useful for not repeating the whole path string every time you want to call a control element.
     */
    public void popFolder(){
        if(pathPrefix.isEmpty() && printedPopWarningAlready){
            println("Too many calls to popFolder() - there is nothing to pop");
            printedPopWarningAlready = true;
        }
        if(!pathPrefix.isEmpty()){
            pathPrefix.remove(0);
        }
    }

    /**
     * Clears the global path prefix stack, removing all its elements.
     * Nothing will be prefixed in subsequent calls to control elements.
     * Also happens every time draw() ends and LazyGui.draw() begins,
     * in order for LazyGui to be certain of what the current path is for its own control elements like the options folder
     * and so the library user doesn't have to pop all of their folders, since they get cleared every frame.
     */
    public void clearFolder(){
        pathPrefix.clear();
    }

    /**
     * Gets the current path prefix stack, inserting a forward slash after each folder name in the stack.
     * Mostly used internally by LazyGui, but it can also be useful for debugging.
     *
     * @return entire path prefix stack concatenated to one string
     */
    public String getFolder(){
        if(pathPrefix.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = pathPrefix.size() - 1; i >= 0; i--) {
            String folder = pathPrefix.get(i);
            sb.append(folder);
            sb.append("/");
        }
        return sb.toString();
    }

    /**
     * Gets the main font as currently used by the GUI.
     * @return main gui font
     */
    public PFont getMainFont(){
        return FontStore.getMainFont();
    }

    /**
     * Gets the side font as currently used by the GUI.
     * @return side gui font
     */
    public PFont getSideFont(){
        return FontStore.getSideFont();
    }

    void requestScreenshot(String customFilePath){
        screenshotRequestedOnMainThreadWithCustomPath = true;
        requestedScreenshotCustomFilePath = customFilePath;
    }

    private void updateAllNodeValuesRegardlessOfParentWindowOpenness() {
        List<AbstractNode> allNodes = getAllNodesAsList();
        for(AbstractNode node : allNodes){
            node.updateValuesRegardlessOfParentWindowOpenness();
        }
    }

    /**
     * Should be called at the end of LazyGui.draw().
     * Calling this at the start of draw() would not allow the user to take a screenshot of the gui.
     * When it's called at the end of draw() the user can choose whether to show or to hide the gui overlay before taking the screenshot.
     */
    private void takeScreenshotIfRequested() {
        if (!screenshotRequestedOnMainThread && !screenshotRequestedOnMainThreadWithCustomPath) {
            return;
        }
        String folderPath = getGuiDataFolderPath("/screenshots");
        File folder = new File(folderPath);
        if(!folder.isDirectory()){
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
        String fileName = getNextUnusedIntegerFileNameInFolder(folder);
        String fileType = ".png";
        String filePath = folderPath + "/" + fileName + fileType;

        if(screenshotRequestedOnMainThreadWithCustomPath){
            filePath = requestedScreenshotCustomFilePath;
        }

        app.save(filePath);
        screenshotRequestedOnMainThread = false;
        screenshotRequestedOnMainThreadWithCustomPath = false;
    }

    void createOptionsFolder() {
        String path = "options";
        optionsNode = new FolderNode(path, NodeTree.getRoot());
        NodeTree.insertNodeAtItsPath((optionsNode));
        pushFolder("options");
        NodeTree.insertNodeAtItsPath(new SaveFolderNode(getFolder() + "saves", optionsNode));
        ThemeStore.updateThemePicker();
        popFolder();
    }

    private void updateOptionsFolder() {
        pushFolder(optionsNode.path);
        WindowManager.updateWindowOptions();
        FontStore.updateFontOptions();
        ThemeStore.updateThemePicker();
        SnapToGrid.update();
        ContextLines.update(pg);
        updateHotkeyToggles();
        LayoutStore.keyboardInputAppendCooldownMillis = sliderInt("numpad input (ms)", keyboardInputAppendCooldownMillis, 100, 5000);
        popFolder();
    }

    private void updateHotkeyToggles() {
        pushFolder("hotkeys");
        hotkeyHideActive = toggle("h: hide\\/show gui", true);
        hotkeyCloseAllWindowsActive = toggle("d: close windows", true);

        hotkeyScreenshotActive = toggle("i: screenshot", true);
        // TODO fix
        //  https://github.com/KrabCode/LazyGui/issues/36
//        undoHotkeyActive = toggle("ctrl + z: undo", true);
//        redoHotkeyActive = toggle("ctrl + y: redo", true);
        hotkeySaveActive = toggle("ctrl + s: new save", true);
        hotkeyOpenSketchFolderActive = toggle("k: open sketch folder", true);
        popFolder();
    }

    private void tryHandleHotkeyInteraction(LazyKeyEvent keyEvent) {
        char key = keyEvent.getKeyChar();
        int keyCode = keyEvent.getKeyCode();
        if (key == 'h' && hotkeyHideActive) {
            isGuiHidden = !isGuiHidden;
        }
        screenshotRequestedOnMainThread = (key == 'i' && hotkeyScreenshotActive);
        if(key == 'd' && hotkeyCloseAllWindowsActive){
            WindowManager.closeAllWindows();
        }
        if(key == 'k' && hotkeyOpenSketchFolderActive){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(app.dataPath("")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        if(keyCode == KeyCodes.CTRL_Z && hotkeyUndoActive){
//            LayoutStore.undo();
//        }
//        if(keyCode == KeyCodes.CTRL_Y && hotkeyRedoActive){
//            LayoutStore.redo();
//        }
        if(keyCode == KeyCodes.CTRL_S && hotkeySaveActive){
            JsonSaves.createNewSaveWithRandomName();
        }
    }

    static void updateEndlessLoopDetection() {
        lastFrameMillis = app.millis();
    }

    static boolean isSketchStuckInEndlessLoop() {
        long timeSinceLastFrame = app.millis() - lastFrameMillis;
        return timeSinceLastFrame > lastFrameMillisStuckLimit;
    }

    private static void registerExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(LazyGui::createAutosave));
    }

    static void createAutosave() {
        if (!autosaveEnabled) {
            return;
        }
        if (LazyGui.isSketchStuckInEndlessLoop()) {
            println("NOT autosaving," +
                    " because the last frame took more than " + LazyGui.lastFrameMillisStuckLimit + " ms," +
                    " which looks like the program stopped due to an exception or reached an endless loop");
            return;
        }
        JsonSaves.createTreeSaveFiles("auto");
    }
    public static boolean autosaveEnabled = false;

}
