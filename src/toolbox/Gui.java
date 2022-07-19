package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import toolbox.global.themes.Theme;
import toolbox.global.themes.ThemeColorType;
import toolbox.global.themes.ThemeStore;
import toolbox.global.State;
import toolbox.global.NodeTree;
import toolbox.global.themes.ThemeType;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.colorPicker.Color;
import toolbox.windows.nodes.colorPicker.ColorPickerFolder;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;
import toolbox.windows.nodes.gradient.GradientFolder;
import toolbox.windows.nodes.imagePicker.ImagePickerFolder;
import toolbox.windows.nodes.select.StringPickerFolder;
import toolbox.windows.nodes.sliders.SliderIntNode;
import toolbox.windows.nodes.sliders.SliderNode;

import java.util.ArrayList;

import static processing.core.PApplet.*;


public class Gui implements UserInputSubscriber {
    public static boolean isGuiHidden = false;
    public PGraphics pg;
    PApplet app;

    // TODO class cast exception explanation when e.g. a slider path collides with an existing folder

    public Gui(PApplet sketch) {
        this.app = sketch;
        if (!app.sketchRenderer().equals(P2D) && !app.sketchRenderer().equals(P3D)) {
            println("The Toolbox library requires the P2D or P3D renderer");
        }
        State.init(this, app);
        State.loadMostRecentSave();
        ThemeStore.initSingleton();
        UserInputPublisher.createSingleton();
        UserInputPublisher.subscribe(this);
        WindowManager.createSingleton();
        float cell = State.cell;
        FolderWindow rootFolder = new FolderWindow(
                new PVector(cell, cell),
                NodeTree.getRoot(),
                false
        );
        rootFolder.createStateListFolderNode();
        WindowManager.addWindow(rootFolder);
        lazyFollowSketchResolution();
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
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.clear();
        if (!isGuiHidden) {
            WindowManager.updateAndDrawWindows(pg);
        }
        pg.endDraw();
        resetMatrixInAnyRenderer();
        canvas.pushStyle();
        canvas.imageMode(CORNER);
        canvas.image(pg, 0, 0);
        canvas.popStyle();
        State.updateSketchFreezeDetection();
    }

    private void resetMatrixInAnyRenderer() {
        if (State.app.sketchRenderer().equals(P3D)) {
            State.app.camera();
        } else {
            State.app.resetMatrix();
        }
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

    public boolean mousePressedOutsideGui(){
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
        return node.valueBoolean;
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
            throw new IllegalArgumentException("SelectString() options parameter must not be null and have length > 0");
        }
        StringPickerFolder node = (StringPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new StringPickerFolder(path, parentFolder, options, defaultOption);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueString;
    }

    public void setTheme(Theme theme){
        ThemeStore.currentSelection = ThemeType.CUSTOM;
        ThemeStore.setCustomPalette(theme);
    }

    public void themePicker() {
        themePicker(ThemeType.getPalette(ThemeStore.currentSelection), null);
    }

    public void themePicker(Theme defaultTheme) {
        themePicker(defaultTheme, ThemeType.getName(ThemeType.CUSTOM));
    }

    private void themePicker(Theme defaultTheme, String defaultPaletteName) {
        String basePath = "/themes";
        String userSelection = State.gui.stringPicker(basePath + "/preset", ThemeType.getAllNames(), defaultPaletteName);

        if (!userSelection.equals(ThemeType.getName(ThemeStore.currentSelection))) {
            ThemeStore.currentSelection = ThemeType.getValue(userSelection);
        }
        String customDefinitionPath = basePath + "/custom";
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

    @SuppressWarnings("unused")
    public Color colorPicker(String path) {
        return colorPicker(path, 1, 1, 0, 1);
    }

    @SuppressWarnings("unused")
    public Color colorPicker(String path, float grayNorm) {
        return colorPicker(path, grayNorm, grayNorm, grayNorm, 1);
    }

    @SuppressWarnings("unused")
    public Color colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm) {
        return colorPicker(path, hueNorm, saturationNorm, brightnessNorm, 1);
    }

    @SuppressWarnings("unused")
    public Color colorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm) {
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
    public Color colorPicker(String path, int hex) {
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

    @SuppressWarnings("unused")
    public PImage imagePicker(String path) {
        return imagePicker(path, "");
    }

    public PImage imagePicker(String path, String defaultFilePath) {
        ImagePickerFolder node = (ImagePickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ImagePickerFolder(path, parentFolder, defaultFilePath);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getOutputImage();
    }
}
