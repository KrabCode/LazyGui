package toolbox;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import toolbox.global.palettes.PaletteStore;
import toolbox.global.State;
import toolbox.global.NodeTree;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.colorPicker.Color;
import toolbox.windows.nodes.colorPicker.ColorPickerFolder;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;
import toolbox.windows.nodes.gradient.GradientFolder;
import toolbox.windows.nodes.imagePicker.ImagePickerFolder;
import toolbox.windows.nodes.select.SelectStringFolder;
import toolbox.windows.nodes.sliders.SliderIntNode;
import toolbox.windows.nodes.sliders.SliderNode;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Gui implements UserInputSubscriber {
    public static boolean isGuiHidden = false;
    public PGraphics pg;
    PApplet app;

    public Gui(PApplet p, boolean isGuiVisibleByDefault) {
        isGuiHidden = !isGuiVisibleByDefault;
        new Gui(p);
    }

    public Gui(PApplet p) {
        this.app = p;
        if(!app.sketchRenderer().equals(P2D) && !app.sketchRenderer().equals(P3D)){
            println("The Toolbox library requires the P2D or P3D renderer");
        }
        State.init(this, app);
        State.loadMostRecentSave();
        PaletteStore.initSingleton();
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
        if(keyEvent.getKeyChar() == 'p'){
            PaletteStore.setNextPalette();
        }
    }

    public float slider(String path) {
        return slider(path, 0, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    public float slider(String path, float defaultValue) {
        return slider(path, defaultValue, Float.MAX_VALUE, -Float.MAX_VALUE, false);
    }

    public float slider(String path, float defaultValue, float min, float max){
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

    private String selectString(String path, ArrayList<String> options){
        return selectString(path, options.toArray(new String[0]));
    }

    public String selectString(String path, String... options){
        if(options == null || options.length == 0){
            throw new IllegalArgumentException("SelectString() options parameter must not be null and have length > 0");
        }
        SelectStringFolder node = (SelectStringFolder) NodeTree.findNode(path);
        if(node == null){
            NodeFolder parentFolder = NodeTree.findParentFolderLazyInitPath(path);
            node = new SelectStringFolder(path, parentFolder, options);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.valueString;
    }

    public void guiPalettePicker(){
        // TODO
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
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            int hex = State.normalizedColorProvider.color(hueNorm, saturationNorm, brightnessNorm, 1);
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

    public Color colorPicker(String path, int hex) {
        ColorPickerFolder node = (ColorPickerFolder) NodeTree.findNode(path);
        if (node == null) {
            NodeFolder folder = NodeTree.findParentFolderLazyInitPath(path);
            node = new ColorPickerFolder(path, folder, hex);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node.getColor();
    }

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
