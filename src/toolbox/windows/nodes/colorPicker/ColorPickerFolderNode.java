package toolbox.windows.nodes.colorPicker;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.FolderNode;

import static processing.core.PConstants.*;

public class ColorPickerFolderNode extends FolderNode {

    public int hex;
    ColorPreviewNode previewNode;
    private int hueNodeIndex = 1, saturationNodeIndex = 2, brightnessNodeIndex = 3, alphaNodeIndex = 4;

    public ColorPickerFolderNode(String path, FolderNode parentFolder, int hex) {
        super(path, parentFolder);
        this.hex = hex;
        previewNode = new ColorPreviewNode(path + "/preview", this);
        initNodes();
        loadValuesFromHex(true);
    }

    private void initNodes() {
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1, 1);
        children.add(new ColorPreviewNode(path + "/preview", this));
        children.add(new HueNode(path + "/hue", this, colorProvider.hue(hex)));
        children.add(new SaturationNode(path + "/sat", this, colorProvider.saturation(hex)));
        children.add(new BrightnessNode(path + "/br", this, colorProvider.brightness(hex)));
        children.add(new AlphaNode(path + "/alpha", this, 1));
        children.add(new HexNode(path + "/hex", this));
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.6f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.fill(hex);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    public void loadValuesFromHex(boolean setDefaults) {
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1, 1);
        ((ColorSliderNode) children.get(hueNodeIndex)).valueFloat = colorProvider.hue(hex);
        ((ColorSliderNode) children.get(saturationNodeIndex)).valueFloat = colorProvider.saturation(hex);
        ((ColorSliderNode) children.get(brightnessNodeIndex)).valueFloat = colorProvider.brightness(hex);
        ((ColorSliderNode) children.get(alphaNodeIndex)).valueFloat = colorProvider.alpha(hex);
        if(setDefaults){
            ((ColorSliderNode) children.get(hueNodeIndex)).valueFloatDefault = colorProvider.hue(hex);
            ((ColorSliderNode) children.get(saturationNodeIndex)).valueFloatDefault = colorProvider.saturation(hex);
            ((ColorSliderNode) children.get(brightnessNodeIndex)).valueFloatDefault = colorProvider.brightness(hex);
            ((ColorSliderNode) children.get(alphaNodeIndex)).valueFloatDefault = colorProvider.alpha(hex);
        }
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1,1);
        hex = colorProvider.color(getValue(hueNodeIndex), getValue(saturationNodeIndex), getValue(brightnessNodeIndex),getValue(alphaNodeIndex));
    }

    Color outputColor = new Color();
    public Color getColor() {
        outputColor.hex = hex;
        return outputColor;
    }

    private float getValue(int index){
        return ((ColorSliderNode)children.get(index)).valueFloat;
    }

    public float hue() {
        return getValue(hueNodeIndex);
    }

    public float saturation() {
        return getValue(saturationNodeIndex);
    }

    public float brightness() {
        return getValue(brightnessNodeIndex);
    }
    public float alpha() {
        return getValue(alphaNodeIndex);
    }
}
