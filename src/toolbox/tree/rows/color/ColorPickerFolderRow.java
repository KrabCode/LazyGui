package toolbox.tree.rows.color;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.tree.rows.FolderRow;

import static processing.core.PConstants.*;

public class ColorPickerFolderRow extends FolderRow {

    public int hex;
    ColorPreviewRow previewRow;
    HueRow hueRow;
    SaturationRow saturationRow;
    BrightnessRow brightnessRow;
    AlphaRow alphaRow;
    HexRow hexRow;

    public ColorPickerFolderRow(String path, FolderRow parentFolder, int hex) {
        super(path, parentFolder);
        this.hex = hex;
        previewRow = new ColorPreviewRow(path + "/preview", this);
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1, 1);
        hueRow = new HueRow(path + "/hue", this, colorProvider.hue(hex));
        saturationRow = new SaturationRow(path + "/sat", this, colorProvider.saturation(hex));
        brightnessRow = new BrightnessRow(path + "/br", this, colorProvider.brightness(hex));
        alphaRow = new AlphaRow(path + "/alpha", this, 1);
        hexRow = new HexRow(path + "/hex", this);
        children.add(previewRow);
        children.add(hueRow);
        children.add(saturationRow);
        children.add(brightnessRow);
        children.add(alphaRow);
        children.add(hexRow);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.65f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.fill(hex);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = State.colorProvider;
        colorProvider.colorMode(HSB,1,1,1,1);
        hex = colorProvider.color(hueRow.valueFloat, saturationRow.valueFloat, brightnessRow.valueFloat,alphaRow.valueFloat);
    }

    Color outputColor = new Color();
    public Color getColor() {
        outputColor.hex = hex;
        return outputColor;
    }

    public float hue() {
        return hueRow.valueFloat;
    }

    public float saturation() {
        return saturationRow.valueFloat;
    }

    public float brightness() {
        return brightnessRow.valueFloat;
    }
    public float alpha() {
        return alphaRow.valueFloat;
    }
}
