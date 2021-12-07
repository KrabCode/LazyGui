package toolbox.windows.nodes.colorPicker;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.FolderNode;

import static processing.core.PApplet.hex;
import static processing.core.PApplet.unhex;
import static processing.core.PConstants.*;

public class ColorPickerFolderNode extends FolderNode {

    @Expose
    public String hexString;
    private int hex;
    ColorPreviewNode previewNode;
    private final int hueNodeIndex = 1;
    private final int satNodeIndex = 2;
    private final int brNodeIndex = 3;
    private final int alphaNodeIndex = 4;

    public ColorPickerFolderNode(String path, FolderNode parentFolder, int hex) {
        super(path, parentFolder);
        if(hex >= 0 && hex <= 1){
            hex = State.colorProvider.color(hex);
        }
        setHex(hex);
        previewNode = new ColorPreviewNode(path + "/preview", this);
        initNodes();
        loadValuesFromHex(true);
    }

    private void initNodes() {
        children.add(new ColorPreviewNode(path + "/preview", this));
        children.add(new ColorSliderNode.HueNode(path + "/hue", this));
        children.add(new ColorSliderNode.SaturationNode(path + "/sat", this));
        children.add(new ColorSliderNode.BrightnessNode(path + "/br", this));
        children.add(new ColorSliderNode.AlphaNode(path + "/alpha", this));
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
        ((ColorSliderNode) children.get(hueNodeIndex)).valueFloat = colorProvider.hue(hex);
        ((ColorSliderNode) children.get(satNodeIndex)).valueFloat = colorProvider.saturation(hex);
        ((ColorSliderNode) children.get(brNodeIndex)).valueFloat = colorProvider.brightness(hex);
        ((ColorSliderNode) children.get(alphaNodeIndex)).valueFloat = colorProvider.alpha(hex);
        if(setDefaults){
            ((ColorSliderNode) children.get(hueNodeIndex)).valueFloatDefault = colorProvider.hue(hex);
            ((ColorSliderNode) children.get(satNodeIndex)).valueFloatDefault = colorProvider.saturation(hex);
            ((ColorSliderNode) children.get(brNodeIndex)).valueFloatDefault = colorProvider.brightness(hex);
            ((ColorSliderNode) children.get(alphaNodeIndex)).valueFloatDefault = colorProvider.alpha(hex);
        }
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = State.colorProvider;
        setHex(colorProvider.color(
                getValue(hueNodeIndex),
                getValue(satNodeIndex),
                getValue(brNodeIndex),
                getValue(alphaNodeIndex)));
    }

    Color outputColor = new Color();
    public Color getColor() {
        outputColor.hex = hex;
        outputColor.hue = hue();
        outputColor.saturation = saturation();
        outputColor.brightness = brightness();
        outputColor.alpha = alpha();
        return outputColor;
    }

    private float getValue(int index){
        return ((ColorSliderNode)children.get(index)).valueFloat;
    }

    public float hue() {
        return getValue(hueNodeIndex);
    }

    public float saturation() {
        return getValue(satNodeIndex);
    }

    public float brightness() {
        return getValue(brNodeIndex);
    }
    public float alpha() {
        return getValue(alphaNodeIndex);
    }

    public int getHex() {
        return hex;
    }

    public void setHex(int hex) {
        this.hex = hex;
        hexString = hex(hex);
    }

    public void overwriteState(JsonElement loadedNode) {
        setHex(unhex(loadedNode.getAsJsonObject().get("hexString").getAsString()));
    }
}
