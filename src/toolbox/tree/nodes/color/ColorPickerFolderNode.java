package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.GlobalState;
import toolbox.tree.nodes.FolderNode;

import static processing.core.PConstants.*;

public class ColorPickerFolderNode extends FolderNode {

    public int hex;
    ColorPreviewNode previewNode;
    HexNode hexNode;
    HueNode hueNode;
    SaturationNode saturationNode;
    BrightnessNode brightnessNode;
    AlphaNode alphaNode;

    public ColorPickerFolderNode(String path, FolderNode parentFolder, int hex) {
        super(path, parentFolder);
        this.hex = hex;
        previewNode = new ColorPreviewNode(path + "/preview", this);
        PGraphics colorProvider = GlobalState.colorProvider;
        hexNode = new HexNode(path + "/hex", this);
        hueNode = new HueNode(path + "/hue", this, colorProvider.hue(hex));
        saturationNode = new SaturationNode(path + "/sat", this, colorProvider.saturation(hex));
        brightnessNode = new BrightnessNode(path + "/br", this, colorProvider.brightness(hex));
        alphaNode = new AlphaNode(path + "/alpha", this, colorProvider.alpha(hex));
        children.add(previewNode);
        children.add(hexNode);
        children.add(hueNode);
        children.add(saturationNode);
        children.add(brightnessNode);
        children.add(alphaNode);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeContentBasedOnFocus(pg);
        float previewRectSize = cell * 0.5f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.noFill();
        pg.translate(1, 1);
        pg.rect(0, 0, previewRectSize, previewRectSize);
        pg.fill(hex);
        pg.translate(-2, -2);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = GlobalState.colorProvider;
        colorProvider.colorMode(HSB,1,1,1,1);
        hex = colorProvider.color(hueNode.valueFloat, saturationNode.valueFloat, brightnessNode.valueFloat,alphaNode.valueFloat);
    }

    Color outputColor = new Color();
    public Color getColor() {
        outputColor.hex = hex;
        return outputColor;
    }
}
