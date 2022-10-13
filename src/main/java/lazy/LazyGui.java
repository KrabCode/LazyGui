package lazy;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import lazy.themes.Theme;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import lazy.themes.ThemeType;
import lazy.windows.nodes.*;
import lazy.windows.nodes.colorPicker.PickerColor;
import lazy.windows.nodes.colorPicker.ColorPickerFolder;
import lazy.userInput.UserInputPublisher;
import lazy.userInput.UserInputSubscriber;
import lazy.windows.FolderWindow;
import lazy.windows.WindowManager;
import lazy.windows.nodes.gradient.GradientFolder;
import lazy.windows.nodes.saves.SaveNodeFolder;
import lazy.windows.nodes.stringPicker.StringPickerFolder;
import lazy.windows.nodes.sliders.SliderIntNode;
import lazy.windows.nodes.sliders.SliderNode;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.*;


public class LazyGui implements UserInputSubscriber {
    public static boolean isGuiHidden = false;
    private static boolean screenshotRequestedOnMainThread = false;
    private static boolean screenshotRequestedOnMainThreadWithCustomPath = false;
    private static String requestedScreenshotCustomPath = "";
    private static boolean hotkeyHideActive, undoHotkeyActive, redoHotkeyActive, hotkeyScreenshotActive, hotkeyCloseAllWindowsActive, saveHotkeyActive;
    public static boolean drawPathTooltips = false;
    private PGraphics pg;
    NodeFolder toolbar;
    PApplet app;

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

    public void draw() {
        draw(State.app.g);
    }

    public void draw(PGraphics canvas) {
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

    private void updateAllNodeValuesRegardlessOfParentWindowOpenness() {
        List<AbstractNode> allNodes = NodeTree.getAllNodesAsList();
        for(AbstractNode node : allNodes){
            node.updateValues();
        }
    }

    public void requestScreenshot(String customPath){
        screenshotRequestedOnMainThreadWithCustomPath = true;
        requestedScreenshotCustomPath = customPath;
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
        try {
            // println("Saved screenshot to: " + new File(filePath).getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        screenshotRequestedOnMainThread = false;
        screenshotRequestedOnMainThreadWithCustomPath = false;
    }


    public void createOptionsFolder() {
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

    private void hotkeyInteraction(KeyEvent keyEvent) {
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

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isAutoRepeat()) {
            return;
        }
        hotkeyInteraction(keyEvent);
    }

    public boolean mousePressedOutsideGui() {
        return State.app.mousePressed && UserInputPublisher.mouseFallsThroughThisFrame;
    }

    @SuppressWarnings("unused")
    public float slider(String path) {
        return slider(path, 0, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    @SuppressWarnings("unused")
    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    @SuppressWarnings("unused")
    public float slider(String path, float defaultValue, float min, float max) {
        return slider(path, defaultValue, min, max, true);
    }

    @SuppressWarnings("unused")
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

    public void sliderSet(String path, float value){
        SliderNode node = (SliderNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderNode(path, value, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    @SuppressWarnings("unused")
    public int sliderInt(String path) {
        return sliderInt(path, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    @SuppressWarnings("unused")
    public int sliderInt(String path, int defaultValue) {
        return sliderInt(path, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    @SuppressWarnings("unused")
    public int sliderInt(String path, int defaultValue, int min, int max) {
        return sliderInt(path, defaultValue, min, max, true);
    }

    @SuppressWarnings("unused")
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

    public void sliderIntSet(String path, int value){
        SliderIntNode node = (SliderIntNode) NodeTree.findNode(path);
        if (node == null) {
            node = createSliderIntNode(path, value, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
            NodeTree.insertNodeAtItsPath(node);
        }
        node.valueFloat = value;
    }

    @SuppressWarnings("unused")
    public boolean toggle(String path) {
        return toggle(path, false);
    }

    public boolean toggle(String path, boolean defaultValue) {
        ToggleNode node = (ToggleNode) NodeTree.findNode(path);
        if (node == null) {
            node = createToggleNode(path, defaultValue);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueBoolean;
    }

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

    @SuppressWarnings("unused")
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

    public String stringPicker(String path, ArrayList<String> options) {
        return stringPicker(path, options.toArray(new String[0]), null);
    }

    public String stringPicker(String path, ArrayList<String> options, String defaultOption) {
        return stringPicker(path, options.toArray(new String[0]), defaultOption);
    }

    public String stringPicker(String path, String[] options) {
        return stringPicker(path, options, null);
    }

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

    public void setTheme(Theme theme) {
        ThemeStore.currentSelection = ThemeType.CUSTOM;
        ThemeStore.setCustomPalette(theme);
    }

    @SuppressWarnings("unused")
    public PickerColor colorPicker(String path) {
        return colorPicker(path, 1, 1, 0, 1);
    }

    @SuppressWarnings("unused")
    public PickerColor colorPicker(String path, float grayNorm) {
        return colorPicker(path, grayNorm, grayNorm, grayNorm, 1);
    }

    @SuppressWarnings("unused")
    public PickerColor colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm) {
        return colorPicker(path, hueNorm, saturationNorm, brightnessNorm, 1);
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public PickerColor colorPicker(String path, int hex) {
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public PGraphics gradient(String path) {
        return gradient(path, 1);
    }

    public PGraphics gradient(String path, float alpha) {
        GradientFolder node = (GradientFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new GradientFolder(path, parentFolder, alpha);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getOutputGraphics();
    }
}
