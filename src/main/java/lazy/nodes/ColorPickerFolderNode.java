package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import lazy.*;
import lazy.input.LazyKeyEvent;
import lazy.stores.UndoRedoStore;
import lazy.utils.KeyCodes;
import lazy.utils.ClipboardUtils;
import lazy.utils.JsonSaves;
import processing.core.PGraphics;

import static lazy.stores.NormColorStore.getColorStore;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.*;

public class ColorPickerFolderNode extends FolderNode {

    @Expose
    String hexString;
    private int hex;
    @SuppressWarnings("FieldCanBeLocal")
    private final String HEX_NODE_NAME = "hex";
    private final String HUE_NODE_NAME = "hue";
    private final String SAT_NODE_NAME = "sat";
    private final String BR_NODE_NAME = "br";
    private final String ALPHA_NODE_NAME = "alpha";

    public ColorPickerFolderNode(String path, FolderNode parentFolder, int hex) {
        super(path, parentFolder);
        setHex(hex);
        lazyInitNodes();
        idealWindowWidthInCells = 7;
        JsonSaves.overwriteWithLoadedStateIfAny(this);
        loadValuesFromHex(true);
    }

    protected void lazyInitNodes() {
        if (children.size() > 0) {
            return;
        }
        children.add(new ColorPreviewNode(path + "/preview", this));
        children.add(new ColorSliderNode.HueNode(path + "/" + HUE_NODE_NAME, this));
        children.add(new ColorSliderNode.SaturationNode(path + "/" + SAT_NODE_NAME, this));
        children.add(new ColorSliderNode.BrightnessNode(path + "/" + BR_NODE_NAME, this));
        children.add(new ColorSliderNode.AlphaNode(path + "/" + ALPHA_NODE_NAME, this));
        children.add(new ColorPickerHexNode(path + "/" + HEX_NODE_NAME, this));
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {

    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawPreviewRect(pg);
    }

    protected void drawPreviewRect(PGraphics pg){
        strokeForegroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.6f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.fill(hex);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    public void loadValuesFromHex(boolean setDefaults) {
        lazyInitNodes();
        PGraphics colorProvider = getColorStore();
        ((ColorSliderNode) findChildByName(HUE_NODE_NAME)).valueFloat = colorProvider.hue(hex);
        ((ColorSliderNode) findChildByName(SAT_NODE_NAME)).valueFloat = colorProvider.saturation(hex);
        ((ColorSliderNode) findChildByName(BR_NODE_NAME)).valueFloat = colorProvider.brightness(hex);
        ((ColorSliderNode) findChildByName(ALPHA_NODE_NAME)).valueFloat = colorProvider.alpha(hex);
        if (setDefaults) {
            ((ColorSliderNode) findChildByName(HUE_NODE_NAME)).valueFloatDefault = colorProvider.hue(hex);
            ((ColorSliderNode) findChildByName(SAT_NODE_NAME)).valueFloatDefault = colorProvider.saturation(hex);
            ((ColorSliderNode) findChildByName(BR_NODE_NAME)).valueFloatDefault = colorProvider.brightness(hex);
            ((ColorSliderNode) findChildByName(ALPHA_NODE_NAME)).valueFloatDefault = colorProvider.alpha(hex);
        }
    }

    void loadValuesFromHSBA() {
        PGraphics colorProvider = getColorStore();
        setHex(colorProvider.color(
                getValue(HUE_NODE_NAME),
                getValue(SAT_NODE_NAME),
                getValue(BR_NODE_NAME),
                getValue(ALPHA_NODE_NAME)));
    }

    public PickerColor getColor() {
        return new PickerColor(hex, hue(), saturation(), brightness(), alpha());
    }

    private float getValue(String nodeName) {
        ColorSliderNode node = ((ColorSliderNode) findChildByName(nodeName));
        return node.valueFloat;
    }

    float hue() {
        return getValue(HUE_NODE_NAME);
    }

    float saturation() {
        return getValue(SAT_NODE_NAME);
    }

    float brightness() {
        return getValue(BR_NODE_NAME);
    }

    float alpha() {
        return getValue(ALPHA_NODE_NAME);
    }

    String getHexString() {
        return hexString;
    }

    public void setHex(int hex) {
        if (hex == 0) {
            hex = unhex("00010101");
        }
        this.hex = hex;
        hexString = hex(hex);
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("hexString");
        if (loadedString != null) {
            setHex(unhex(loadedString.getAsString()));
            loadValuesFromHex(true);
        }
    }

    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
//        super.keyPressedOverNode(e, x, y);
//        - we don't want to copy the contents of the folder itself - we only want to copy the hex code to clipboard
        if (e.isControlDown() && e.getKeyCode() == KeyCodes.C) {
            ClipboardUtils.setClipboardString(getHexString());
        }
        if (e.isControlDown() && e.getKeyCode() == KeyCodes.V) {
            String pastedString = ClipboardUtils.getClipboardString();
            if(pastedString.length() == 6){
                // ensure full alpha if the pasted hex is without alpha
                pastedString = "FF" + pastedString;
            }
            try {
                int pastedHex = (int) Long.parseLong(pastedString, 16);
                setHex(pastedHex);
                loadValuesFromHex(false);
                UndoRedoStore.onUndoableActionEnded();
            } catch (NumberFormatException nfe) {
                println("Could not parse hex color from input string: \"" + pastedString + "\"");
            }
        }
    }

    public void setHue(float hueToAdd) {
        ColorSliderNode hueSlider = (ColorSliderNode) findChildByName(HUE_NODE_NAME);
        hueSlider.valueFloat = LazyGui.hueModulo(hueSlider.valueFloat + hueToAdd);
        loadValuesFromHSBA();
    }
}
