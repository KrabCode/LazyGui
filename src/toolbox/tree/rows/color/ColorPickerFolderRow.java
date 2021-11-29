package toolbox.tree.rows.color;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.tree.rows.FolderRow;

import static processing.core.PConstants.*;

public class ColorPickerFolderRow extends FolderRow {

    public int hex;
    ColorPreviewRow previewRow;
    private int hueRowIndex = 1, saturationRowIndex = 2, brightnessRowIndex = 3, alphaRowIndex = 4;

    public ColorPickerFolderRow(String path, FolderRow parentFolder, int hex) {
        super(path, parentFolder);
        this.hex = hex;
        previewRow = new ColorPreviewRow(path + "/preview", this);
        initRows();
        loadValuesFromHex(true);
    }

    private void initRows() {
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1, 1);
        children.add(new ColorPreviewRow(path + "/preview", this));
        children.add(new HueRow(path + "/hue", this, colorProvider.hue(hex)));
        children.add(new SaturationRow(path + "/sat", this, colorProvider.saturation(hex)));
        children.add(new BrightnessRow(path + "/br", this, colorProvider.brightness(hex)));
        children.add(new AlphaRow(path + "/alpha", this, 1));
        children.add(new HexRow(path + "/hex", this));
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
        ((ColorSliderRow) children.get(hueRowIndex)).valueFloat = colorProvider.hue(hex);
        ((ColorSliderRow) children.get(saturationRowIndex)).valueFloat = colorProvider.saturation(hex);
        ((ColorSliderRow) children.get(brightnessRowIndex)).valueFloat = colorProvider.brightness(hex);
        ((ColorSliderRow) children.get(alphaRowIndex)).valueFloat = colorProvider.alpha(hex);
        if(setDefaults){
            ((ColorSliderRow) children.get(hueRowIndex)).valueFloatDefault = colorProvider.hue(hex);
            ((ColorSliderRow) children.get(saturationRowIndex)).valueFloatDefault = colorProvider.saturation(hex);
            ((ColorSliderRow) children.get(brightnessRowIndex)).valueFloatDefault = colorProvider.brightness(hex);
            ((ColorSliderRow) children.get(alphaRowIndex)).valueFloatDefault = colorProvider.alpha(hex);
        }
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1,1);
        hex = colorProvider.color(getValue(hueRowIndex), getValue(saturationRowIndex), getValue(brightnessRowIndex),getValue(alphaRowIndex));
    }

    Color outputColor = new Color();
    public Color getColor() {
        outputColor.hex = hex;
        return outputColor;
    }

    private float getValue(int index){
        return ((ColorSliderRow)children.get(index)).valueFloat;
    }

    public float hue() {
        return getValue(hueRowIndex);
    }

    public float saturation() {
        return getValue(saturationRowIndex);
    }

    public float brightness() {
        return getValue(brightnessRowIndex);
    }
    public float alpha() {
        return getValue(alphaRowIndex);
    }
}
