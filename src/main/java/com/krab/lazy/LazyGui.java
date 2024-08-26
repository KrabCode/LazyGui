package com.krab.lazy;


import com.krab.lazy.input.HotkeySubscriber;
import com.krab.lazy.input.InputWatcherBackend;
import com.krab.lazy.input.UserInputPublisher;
import com.krab.lazy.nodes.*;
import com.krab.lazy.stores.*;
import com.krab.lazy.themes.ThemeStore;
import com.krab.lazy.utils.ContextLines;
import com.krab.lazy.utils.MouseHiding;
import com.krab.lazy.utils.SnapToGrid;
import com.krab.lazy.windows.Window;
import com.krab.lazy.windows.WindowManager;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.krab.lazy.stores.GlobalReferences.app;
import static com.krab.lazy.stores.GlobalReferences.gui;
import static com.krab.lazy.stores.JsonSaveStore.getGuiDataFolderPath;
import static com.krab.lazy.stores.JsonSaveStore.getNextUnusedIntegerFileNameInFolder;
import static com.krab.lazy.stores.NodeTree.*;
import static com.krab.lazy.stores.NormColorStore.color;
import static processing.core.PApplet.*;

/**
 * Main class for controlling the GUI from a processing sketch.
 * Should be initialized as a global variable in processing setup() function with new LazyGui(this).
 * Registers itself at end of the draw() method and displays the GUI whenever draw() ends.
 * Allows the library user to get the value of a gui control element at any time inside draw(), even repeatedly inside loops.
 * If the control element does not exist yet at the time its value is requested it gets newly created just in time.
 * Please note that only one LazyGui object is expected to exist in any given sketch, due to internal static classes.
 */
@SuppressWarnings("unused")
public class LazyGui  {
    private static int lastFrameCountGuiWasShown = -1;

    final ArrayList<String> pathPrefix = new ArrayList<>();
    final int stackSizeWarningLevel = 64;
    private boolean printedPushWarningAlready = false;
    private boolean printedPopWarningAlready = false;
    private PGraphics guiCanvas;

    private static LazyGui singleton;

    /**
     * Main constructor for the LazyGui object which acts as a central hub for all GUI related methods.
     * Meant to be initialized once in setup() with <code>new LazyGui(this)</code>.
     * Registers itself at end of the draw() method and displays the GUI whenever draw() ends.
     *
     * @param sketch the sketch that uses this gui, should be 'this' from the calling side
     */
    public LazyGui(PApplet sketch){
        new LazyGui(sketch, new LazyGuiSettings());
    }

    /**
     * Constructor for the LazyGui object which acts as a central hub for all GUI related methods.
     * Meant to be initialized once in setup() with <code>new LazyGui(this)</code>.
     * Registers itself at end of the draw() method and displays the GUI whenever draw() ends.
     *
     * @param sketch main processing sketch class to display the GUI on and use keyboard and mouse input from
     * @param settings settings to apply (loading a save on startup will overwrite them)
     * @see LazyGuiSettings
     */
    public LazyGui(PApplet sketch, LazyGuiSettings settings) {
        if(singleton != null && singleton != this){
            throw new IllegalStateException("You already initialized a LazyGui object, please don't create any more with 'new LazyGui(this)'." +
                    "\n It's meant to work similar to a singleton, there cannot be more than 1 instance running in any given program," +
                    " because it breaks mouse and key events." +
                    "\n The control element nesting and grouping you may be looking for can probably be done using the built in sub-folders " +
                    "\n  - either by using the '/' character in your control element path like gui.slider(\"hello/world\")" +
                    "\n  - or by calling gui.pushFolder(\"myFolderName\"); <nested control elements> gui.popFolder();" +
                    "\n");
        }
        singleton = this;
        if (!sketch.sketchRenderer().equals(P2D) && !sketch.sketchRenderer().equals(P3D)) {
            throw new IllegalArgumentException("The LazyGui library requires the P2D or P3D renderer. Please set the sketch renderer to P2D or P3D before initializing LazyGui.");
        }
        GlobalReferences.init(this, sketch);
        NormColorStore.init();
        if(settings == null){
            settings = new LazyGuiSettings();
        }
        settings.applyEarlyStartupSettings();
        ThemeStore.init();
        FontStore.lazyUpdateFont();
        InputWatcherBackend.initSingleton();
        UserInputPublisher.initSingleton();
        HotkeySubscriber.initSingleton();
        createOptionsFolder();
        createSavesFolder();
        WindowManager.addRootWindow();
        loadGuiStateFromExistingFiles(settings);
        JsonSaveStore.registerExitHandler();
        lazyFollowSketchResolution();
        registerDrawListener();
        settings.applyLateStartupSettings();
    }

    private void registerDrawListener() {
        app.registerMethod("draw", this);
    }

    private void loadGuiStateFromExistingFiles(LazyGuiSettings settings) {
        if(settings.getSpecificSaveToLoadOnStartupOnce() != null && JsonSaveStore.getSaveFileList().isEmpty()){
            JsonSaveStore.loadStateFromFilePath(settings.getSpecificSaveToLoadOnStartupOnce());
        }
        if(settings.getSpecificSaveToLoadOnStartup() != null){
            JsonSaveStore.loadStateFromFilePath(settings.getSpecificSaveToLoadOnStartup());
        }else if(settings.getShouldLoadLatestSaveOnStartup()){
            JsonSaveStore.loadLatestSave();
        }
    }

    void lazyFollowSketchResolution() {
        if (guiCanvas == null || guiCanvas.width != app.width || guiCanvas.height != app.height) {
            guiCanvas = app.createGraphics(app.width, app.height, P2D);
            guiCanvas.colorMode(HSB, 1, 1, 1, 1);
            int smoothValue = LayoutStore.getSmoothingValue();
            if(smoothValue == 0){
                guiCanvas.noSmooth();
            }else{
                guiCanvas.smooth(smoothValue);
            }

            // dummy draw workaround for processing P2D PGraphics first draw loop bug where the canvas is unusable
            guiCanvas.beginDraw();
            guiCanvas.endDraw();
        }
    }

    /**
     * Utility method for displaying the GUI before draw() ends for the purposes of recording.
     * Does not update the gui, only returns the previous frame's gui canvas.
     * Can be confusing when displayed due to seeing duplicated GUI images with slightly different content.
     * @return previous frame's gui canvas
     */
    public PGraphics getGuiCanvas() {
        return guiCanvas;
    }

    /**
     * Updates and draws the GUI on the main processing canvas.
     * Gets called automatically at the end of draw() by default, but can also be called manually to display the GUI at a better time during the frame.
     * The GUI will not draw itself multiple times per one frame, so the automatic execution is skipped when this is called manually.
     * Must stay public because otherwise this registering won't work: app.registerMethod("draw", this);
     * Calls {@link LazyGui#draw(PGraphics) draw(PGraphics)} internally with the default sketch PGraphics.
     * @see LazyGui#draw(PGraphics)
     */
    public void draw() {
        draw(app.g);
    }

    /**
     * Updates and draws the GUI on the specified parameter canvas, assuming its size is identical to the main sketch size.
     * Gets called automatically at the end of draw().
     * LazyGui will enforce itself being drawn only once per frame internally, which can be useful for including the gui in a recording.
     * If it does get called manually, it will get drawn when requested and then skip its automatic execution for that frame.
     * <p>
     *  Resets any potential hint(DISABLE_DEPTH_TEST) to the default hint(ENABLE_DEPTH_TEST) when done,
     *  because it needs the DISABLE_DEPTH_TEST to draw the GUI over 3D scenes and has currently no way to save or query the original hint state.
     *
     * @param targetCanvas canvas to draw the GUI on
     */
    public void draw(PGraphics targetCanvas) {
        if(lastFrameCountGuiWasShown == app.frameCount){
            // we are at the end of the user's sketch draw(), but the gui has already been displayed this frame
            gui.clearFolder();
            return;
        }
        lastFrameCountGuiWasShown = app.frameCount;
        if(app.frameCount == 1){
            UndoRedoStore.init();
            FolderNode root = getRoot();
            root.window.windowSizeX = root.autosuggestWindowWidthForContents();
        }
        lazyFollowSketchResolution();
        updateAllNodeValuesRegardlessOfParentWindowOpenness();
        guiCanvas.beginDraw();
        guiCanvas.clear();
        gui.clearFolder();
        updateOptionsFolder();
        if (!LayoutStore.isGuiHidden()) {
            SnapToGrid.displayGuideAndApplyFilter(guiCanvas, getWindowBeingDraggedIfAny());
            ContextLines.drawLines(guiCanvas);
            WindowManager.updateAndDrawWindows(guiCanvas);
        }
        guiCanvas.endDraw();
        resetSketchMatrixInAnyRenderer();
        targetCanvas.hint(DISABLE_DEPTH_TEST);
        targetCanvas.pushStyle();
        targetCanvas.imageMode(CORNER);
        targetCanvas.tint(0xFFFFFFFF); // reset tint to draw the gui without it
        targetCanvas.image(guiCanvas, 0, 0);
        targetCanvas.popStyle(); // pop back to any user-defined tint() or imageMode()
        targetCanvas.hint(ENABLE_DEPTH_TEST);
        takeScreenshotIfRequested();
        JsonSaveStore.updateEndlessLoopDetection();
        ChangeListener.onFrameFinished();
    }

    static void resetSketchMatrixInAnyRenderer() {
        if (app.sketchRenderer().equals(P3D)) {
            resetPerspective();
            app.camera();
            app.noLights();
        } else {
            app.resetMatrix();
        }
    }

    private static void resetPerspective() {
        float cameraFOV = PI / 3f;
        float cameraAspect = (float) app.width / (float) app.height;
        float cameraY = app.height / 2.0f;
        float cameraZ = cameraY / tan(PI*60/360);
        float cameraNear = cameraZ / 10;
        float cameraFar = cameraZ * 10;
        app.perspective(cameraFOV, cameraAspect, cameraNear, cameraFar);
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
     * Utility function to tell if the latest mouse event collided and interacted with the GUI.
     * For example with a mouse controlled brush you might not want to draw on the main canvas
     * when you only want to adjust brush properties in the GUI and you don't expect that to affect the artwork.
     *
     * @return whether you should use this mouse press in your processing sketch
     */
    public boolean isMouseOutsideGui() {
        return UserInputPublisher.doesMouseFallThroughGuiThisFrame();
    }

    /**
     * Utility function to tell if the latest mouse event collided and interacted with the GUI.
     * Inverse of isMouseOutsideGui().
     *
     * @return whether you should ignore this mouse press in your processing sketch
     */
    public boolean isMouseOverGui(){
        return !UserInputPublisher.doesMouseFallThroughGuiThisFrame();
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
        if(isPathTakenByUnexpectedType(fullPath, SliderNode.class)){
            return defaultValue;
        }
        SliderNode node = (SliderNode) findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, defaultValue, min, max, constrained);
            insertNodeAtItsPath(node);
        }
        return (float) node.valueFloat;
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
     * Can be used as a non-interactive value display if you set the value every frame.
     * Initializes a new float slider at the given path if needed with the value parameter used as a default value and with no constraint on min and max value.
     *
     * @param path forward slash separated unique path to the control element
     * @param value value to set the float slider at the path to
     */
    public void sliderSet(String path, float value){
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, SliderNode.class)){
            return;
        }
        SliderNode node = (SliderNode) findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, value, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, SliderNode.class)){
            return;
        }
        SliderNode node = (SliderNode) findNode(fullPath);
        if (node == null) {
            node = createSliderNode(fullPath, 0, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, SliderNode.class)){
            return defaultValue;
        }
        SliderIntNode node = (SliderIntNode) findNode(fullPath);
        if (node == null) {
            node = createSliderIntNode(fullPath, defaultValue, min, max, constrained);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, SliderNode.class)){
            return;
        }
        SliderIntNode node = (SliderIntNode) findNode(fullPath);
        if (node == null) {
            node = createSliderIntNode(fullPath, value, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
            insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    /**
     * Gets the vector value of a 2D grid control element.
     * Lazily initializes it if needed and sets all values to 0 by default.
     *
     * @param path forward slash separated unique path to the plot control element
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path){
        return plotXYZ(path, null, false);
    }

    /**
     * Gets the vector value of a 2D grid control element.
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
     * Gets the vector value of a 2D grid control element.
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
     * Gets the vector value of a 2D grid control element.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXY default xy values, z value is ignored
     * @return current xy value with z always set to 0
     */
    public PVector plotXY(String path, PVector defaultXY){
        return plotXYZ(path, defaultXY == null ? new PVector() : defaultXY, false);
    }

    /**
     * Gets the vector value of a 2D grid control element with an extra z slider.
     * Lazily initializes it if needed and sets its xyz values to 0 by default.
     * @param path forward slash separated unique path to the plot control element
     * @return current xyz values
     */
    public PVector plotXYZ(String path){
        return plotXYZ(path, null, true);
    }


    /**
     * Gets the vector value of a 2D grid control element with an extra z slider.
     * Lazily initializes it if needed and sets its values to the parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param defaultXYZ default xyz values
     * @return current xyz values
     */
    public PVector plotXYZ(String path, float defaultXYZ){
        return plotXYZ(path, new PVector(defaultXYZ, defaultXYZ, defaultXYZ), true);
    }


    /**
     * Gets the vector value of a 2D grid control element with an extra z slider.
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
     * Gets the vector value of a 2D grid control element with an extra z slider.
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
        if(isPathTakenByUnexpectedType(fullPath, PlotFolderNode.class)){
            return defaultXYZ == null ? new PVector() : defaultXYZ.copy();
        }
        PlotFolderNode node = (PlotFolderNode) findNode(fullPath);
        if(node == null){
            node = createPlotNode(fullPath, defaultXYZ, useZ);
            insertNodeAtItsPath(node);
        }
        return node.getVectorValue();
    }

    /**
     * Sets the vector value of a 2D grid control element with an extra z slider.
     * Lazily initializes it if needed and sets each of its x,y,z values to the parameter default.
     * @param path forward slash separated unique path to the plot control element
     * @param xyz value to set to each axis
     */
    public void plotSet(String path, float xyz){
        plotSet(path, new PVector(xyz, xyz, xyz), true);
    }

    /**
     * Sets the vector value of a 2D control element.
     * Lazily initializes it if needed and sets its x,y values to the parameter defaults.
     * The extra z slider will not be shown if the plot is initialized in this way.
     * @param path forward slash separated unique path to the plot control element
     * @param x x value to set
     * @param y y value to set
     */
    public void plotSet(String path, float x, float y){
        plotSet(path, new PVector(x,y), false);
    }

    /**
     * Sets the vector value of a 2D grid control element with an extra z slider.
     * Lazily initializes it if needed and sets each of its x,y,z values to the separate parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param x x value to set
     * @param y y value to set
     * @param z z value to set
     */
    public void plotSet(String path, float x, float y, float z){
        plotSet(path, new PVector(x,y,z), true);
    }

    /**
     * Sets the vector value of a 2D grid control element with an extra z slider.
     * Lazily initializes it if needed and sets each of its x,y,z values to the separate parameter defaults.
     * @param path forward slash separated unique path to the plot control element
     * @param valueToSet vector value to set
     */
    public void plotSet(String path, PVector valueToSet){
        plotSet(path, valueToSet, true);
    }

    private void plotSet(String path, PVector valueToSet, boolean useZ){
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, PlotFolderNode.class)){
            return;
        }
        PlotFolderNode node = (PlotFolderNode) findNode(fullPath);
        if(node == null){
            node = createPlotNode(fullPath, valueToSet, useZ);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, ToggleNode.class)){
            return defaultValue;
        }
        ToggleNode node = (ToggleNode) findNode(fullPath);
        if (node == null) {
            node = createToggleNode(fullPath, defaultValue);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, ToggleNode.class)){
            return;
        }
        ToggleNode node = (ToggleNode) findNode(fullPath);
        if (node == null) {
            node = createToggleNode(fullPath, value);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, ButtonNode.class)){
            return false;
        }
        ButtonNode node = (ButtonNode) findNode(fullPath);
        if (node == null) {
            node = createButtonNode(fullPath);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, RadioFolderNode.class)){
            return defaultOption == null ? options[0] : defaultOption;
        }
        RadioFolderNode node = (RadioFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode parentFolder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new RadioFolderNode(fullPath, parentFolder, options, defaultOption);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, RadioFolderNode.class)){
            return;
        }
        RadioFolderNode node = (RadioFolderNode) findNode(fullPath);
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
     * Sets the radio options to a given list of strings, overwriting any existing list.
     * @param path forward slash separated unique path to the control element
     * @param optionsToSet list of options to set
     */
    public void radioSetOptions(String path, List<String> optionsToSet){
        if(optionsToSet == null){
            radioSetOptions(path, new String[0]);
            return;
        }
        radioSetOptions(path, optionsToSet.toArray(new String[0]));
    }

    /**
     * Sets the radio options to a given array of strings, overwriting any existing list.
     * @param path forward slash separated unique path to the control element
     * @param optionsToSet list of options to set
     */
    public void radioSetOptions(String path, String[] optionsToSet){
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, RadioFolderNode.class)){
            return;
        }
        RadioFolderNode node = (RadioFolderNode) findNode(fullPath);
        if (node != null) {
            node.setOptions(optionsToSet, null);
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
        if(isPathTakenByUnexpectedType(fullPath, ColorPickerFolderNode.class)){
            return new PickerColor(hex);
        }
        ColorPickerFolderNode node = (ColorPickerFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(fullPath, folder, hex);
            insertNodeAtItsPath(node);
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
        if(isPathTakenByUnexpectedType(fullPath, ColorPickerFolderNode.class)){
            return;
        }
        ColorPickerFolderNode node = (ColorPickerFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(fullPath, folder, hex);
            insertNodeAtItsPath(node);
        } else {
            node.setHex(hex);
            node.loadValuesFromHex(false);
        }
    }

    /**
     * Adds hue to the color picker, looping it correctly in both directions at the [0,1] boundary.
     * Lazily initializes the color picker if needed with default color 0xFF000000 (full alpha black).
     * Does not block changing the value in the future in any way.
     *
     * @param path forward slash separated unique path to the control element
     * @param hueToAdd hue to add, with the hue value being normalized to the range [0,1]
     */
    public void colorPickerHueAdd(String path, float hueToAdd) {
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, ColorPickerFolderNode.class)){
            return;
        }
        ColorPickerFolderNode node = (ColorPickerFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new ColorPickerFolderNode(path, folder, NormColorStore.color(0,1));
            insertNodeAtItsPath(node);
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
        return getTextNodeValue(path, content);
    }

    private String getTextNodeValue(String path, String content){
        String fullPath = getFolder() + path;
        if(NodeTree.isPathTakenByUnexpectedType(fullPath, TextNode.class)){
            return content;
        }
        TextNode node = (TextNode) findNode(fullPath);
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new TextNode(fullPath, folder, content);
            insertNodeAtItsPath(node);
        }
        return node.getStringValue();
    }

    /**
     * Sets the current value of an editable text field element.
     * Lazily initializes the text input element if needed and then sets its value to the specified content value.
     * Setting it every frame will result in the text field effectively being read-only.
     *
     * @param path forward slash separated unique path to the control element
     * @param content default value for the text content
     */
    public void textSet(String path, String content){
        setTextNodeContent(path, content);
    }

    private void setTextNodeContent(String path, String content){
        String fullPath = getFolder() + path;
        TextNode node = (TextNode) findNode(fullPath);
        if(NodeTree.isPathTakenByUnexpectedType(fullPath, TextNode.class)){
            return;
        }
        if (node == null) {
            FolderNode folder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new TextNode(fullPath, folder, content);
            insertNodeAtItsPath(node);
        }
        node.setStringValue(content);
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed.
     *
     * @param path forward slash separated unique path to the control element
     * @return PGraphics after endDraw() - ready to be displayed as an image
     */
    public PGraphics gradient(String path) {
        return gradient(path, null);
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed and uses the same default color for every color stop.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultHexColor the same default color for the 4 default color stops
     * @return PGraphics after endDraw() - ready to be displayed as an image
     */
    public PGraphics gradient(String path, int defaultHexColor) {
        return gradient(path, new int[]{defaultHexColor, defaultHexColor, defaultHexColor, defaultHexColor});
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed with the alpha parameter as default alpha for all its colors.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultColors default color array for all the colors in the gradient
     *                      if you're pasting colors in the format of #ef8e38 try replacing # with 0xFF
     * @return PGraphics ready to be displayed as an image
     */
    public PGraphics gradient(String path, int[] defaultColors) {
        return gradient(path, defaultColors, null);
    }

    /**
     * Gets a gradient as an image with size matching the main processing sketch size.
     * Lazily initializes the gradient picker if needed with the alpha parameter as default alpha for all its colors.
     *
     * @param path forward slash separated unique path to the control element
     * @param defaultColors default color array for all the colors in the gradient
     *                      if you're pasting colors in the format of #ef8e38 try replacing # with 0xFF
     * @param defaultPositions default position array for all the colors in the gradient - this array must be the same length as defaultColors and must contain values in the range [0,1]
     * @return PGraphics ready to be displayed as an image
     */
    public PGraphics gradient(String path, int[] defaultColors, float[] defaultPositions) {
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, GradientPickerFolderNode.class)){
            return null;
        }
        GradientPickerFolderNode node = (GradientPickerFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode parentFolder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new GradientPickerFolderNode(fullPath, parentFolder, defaultColors, defaultPositions);
            insertNodeAtItsPath(node);
        }
        return node.getOutputGraphics();
    }


    /**
     * Gets a single color from a gradient at the specified position in the range [0, 1].
     * Lazily initializes the gradient picker if needed with autogenerated grayscale colors.
     * Faster than get(x,y) by keeping a copy of the gradient in a lookup table in memory.
     *
     * @param path forward slash separated unique path to the control element
     * @param position normalized position in the [0, 1] range you'd like to know the color of
     * @return PickerColor color at the specified position in the gradient
     */
    public PickerColor gradientColorAt(String path, float position) {
        String fullPath = getFolder() + path;
        if(isPathTakenByUnexpectedType(fullPath, GradientPickerFolderNode.class)){
            return null;
        }
        GradientPickerFolderNode node = (GradientPickerFolderNode) findNode(fullPath);
        if (node == null) {
            FolderNode parentFolder = NodeTree.findParentFolderLazyInitPath(fullPath);
            node = new GradientPickerFolderNode(fullPath, parentFolder, null, null);
            insertNodeAtItsPath(node);
        }
        return node.getGradientColorAt(position);
    }


    /**
     * Returns whether the current folder or any of its recursively nested children changed last frame.
     * Only true for exactly one frame after the change.
     * Calling this function does not change the boolean value it returns, only the next draw() call will reset it.
     * Equivalent to passing an empty string parameter to the overloaded hasChanged(String path), i.e. `hasChanged("")`.
     * When called outside any pushFolder() and popFolder() calls, it will return true if anything in the whole gui has changed.
     *
     * @return true if the control element value has changed this frame, false otherwise
     */
    public boolean hasChanged(){
        String fullPath = getFolder();
        return ChangeListener.hasChangeFinishedLastFrame(fullPath);
    }

    /**
     * Returns whether a control element value, folder or any of its recursively nested children changed last frame.
     * Only true for exactly one frame after the change.
     * Calling this function does not change the boolean value it returns, only the next draw() call will reset it.
     * Prepends the current path stack to the path.
     *
     * @param path forward slash separated unique path to the control element
     * @return true if the control element value has changed this frame, false otherwise
     */
    public boolean hasChanged(String path){
        String fullPath = getFolder() + path;
        return ChangeListener.hasChangeFinishedLastFrame(fullPath);
    }

    /**
     * Pushes a folder name to the global path prefix stack.
     * Can be used multiple times in pairs just like pushMatrix() and popMatrix().
     * Removes leading and trailing slashes to enforce consistency, but allows slashes to appear either escaped or anywhere else inside the string.
     * Any GUI control element call will apply all the folders in the stack as a prefix to their own path parameter.
     * This is useful for not repeating the whole path string every time you want to call a control element.
     *
     * @param folderName one folder's name to push to the stack
     * @see LazyGui#getFolder()
     */
    public void pushFolder(String folderName){
        if(pathPrefix.size() >= stackSizeWarningLevel && !printedPushWarningAlready){
            println("Too many calls to pushFolder() - stack size reached the warning limit of " + stackSizeWarningLevel +
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
     * Can be used multiple times in pairs just like pushMatrix() and popMatrix().
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
     * Hide any chosen element or folder except the root window. Hides both the row and any affected opened windows under that node.
     * The GUI then skips it while drawing, but still returns its values and allows interaction from code as if it was still visible.
     * Can be called once in `setup()` or repeatedly every frame, the result is the same.
     * Does not initialize a control and has no effect on controls that have not been initialized yet.
     * @param path path to the control or folder being hidden - it will get prefixed by the current path prefix stack to get the full path
     */
    public void hide(String path){
        if("".equals(path) || "/".equals(path)){
            hideCurrentFolder();
            return;
        }
        String fullPath = getFolder() + path;
        NodeTree.hideAtFullPath(fullPath);
    }

    /**
     * Hides the folder at the current path prefix stack.
     * See {@link #hide hide(String path)}
     */
    public void hideCurrentFolder(){
        String fullPath = getFolder();
        if(fullPath.endsWith("/")){
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        NodeTree.hideAtFullPath(fullPath);
    }

    /**
     * Makes any control element or folder visible again if it has been hidden by the hide() function.
     * Has no effect on visible elements. Does not reveal any windows except for those that were open and then hidden by the hide() function.
     * Does not initialize a control and has no effect on controls that have not been initialized yet.
     * @param path path to the control element or folder being hidden - it will get prefixed by the current path prefix stack to get the full path
     */
    public void show(String path){
        if("".equals(path) || "/".equals(path)){
            showCurrentFolder();
            return;
        }
        String fullPath = getFolder() + path;
        NodeTree.showAtFullPath(fullPath);
    }

    /**
     * Shows the folder at the current path prefix stack.
     * See {@link #show show(String path)}
     */
    public void showCurrentFolder(){
        String fullPath = getFolder();
        if(fullPath.endsWith("/")){
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        NodeTree.showAtFullPath(fullPath);
    }

    /**
     * Creates a new, sequentially numbered save file in the gui save folder inside the sketch data folder.
     * Works the same way as clicking the "Create new save" gui button inside the built-in 'saves' window.
     */
    public void createSave(){
        JsonSaveStore.createNextSaveInGuiFolder();
    }

    /**
     * Creates a new save file or overwrites an existing one while auto-detecting whether the parameter is a relative or an absolute path to it on disk.
     * Relative paths get saved inside the standard gui save folder and are immediately accessible from the 'saves' window.
     * Absolute paths get saved wherever the absolute path leads.
     * Uses java.nio.file.Paths.get(fileName).isAbsolute() for its auto-detection.
     * @param path name of the new save file or its entire absolute path, ".json" file type suffix is optional and will be appended if missing
     */
    public void createSave(String path){
        JsonSaveStore.createSaveAtRelativeOrAbsolutePath(path);
    }

    /**
     * Loads a save file from disk, overwriting the current values in the running gui whose gui paths match with the saved elements.
     * Accepts both relative and absolute file paths. Attempts to first find the save file inside the standard gui save folder,
     * and if it doesn't find it there then it tries to assume the String parameter is an absolute path and tries to load that.
     * @param path name of the existing save file inside the save folder or its entire absolute path, ".json" file type suffix is optional and will be appended if missing
     */
    public void loadSave(String path){
        JsonSaveStore.loadStateFromFilePath(path);
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

    /**
     * Hides the GUI completely and skips displaying it. Has no effect if the gui is already hidden.
     * Reveal the gui again with showGui() or pressing 'h'.
     */
    public void hideGui(){
        LayoutStore.setIsGuiHidden(true);
    }

    /**
     * Hides the gui if it was visible. Shows the gui if it was hidden.
     */
    public void hideGuiToggle(){
        LayoutStore.hideGuiToggle();
    }

    /**
     * Shows the GUI if it was hidden with 'h' or hideGui(). Has no effect if the gui is already visible.
     */
    public void showGui(){
        LayoutStore.setIsGuiHidden(false);
    }

    /**
     * Returns the current version of the library as a string in the format v{major}.{minor}.{patch} according to semantic versioning.
     * Example: v1.6.0
     * @return "prettyVersion" value from library.properties
     */
    public String getVersion(){
        Properties prop = new Properties();
        String fileName = "library.properties";
        InputStream resourceStream = LazyGui.class.getClassLoader().getResourceAsStream(fileName);
        try {
            prop.load(resourceStream);
            return prop.getProperty("prettyVersion");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
     * When it's called at the end of draw() the user can choose whether to show or to hide the gui overlay inside the sketch before taking the screenshot.
     */
    private void takeScreenshotIfRequested() {
        if (!HotkeyStore.isScreenshotRequestedOnMainThread()) {
            return;
        }
        String folderPath = getGuiDataFolderPath("screenshots");
        File folder = new File(folderPath);
        if(!folder.isDirectory()){
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
        String fileName = getNextUnusedIntegerFileNameInFolder(folder);
        String fileType = ".png";
        String filePath = Paths.get(folderPath, fileName + fileType).toString();
        app.save(filePath);
        println("Screenshot saved to: " + filePath);
        HotkeyStore.setScreenshotRequestedOnMainThread(false);
    }

    private void createOptionsFolder() {
        gui.pushFolder(StringConstants.FOLDER_PATH_OPTIONS);
        ThemeStore.updateThemePicker();
        gui.popFolder();
    }

    private void createSavesFolder(){
        insertNodeAtItsPath(new SaveFolderNode(StringConstants.FOLDER_PATH_SAVES, NodeTree.getRoot()));
    }

    private void updateOptionsFolder() {
        gui.pushFolder(StringConstants.FOLDER_PATH_OPTIONS);
        LayoutStore.updateWindowOptions();
        FontStore.updateFontOptions();
        ThemeStore.updateThemePicker();
        SnapToGrid.updateSettings();
        ContextLines.updateSettings();
        HotkeyStore.updateHotkeyToggles();
        DelayStore.updateInputDelay();
        MouseHiding.updateSettings();
        gui.popFolder();
    }

}
