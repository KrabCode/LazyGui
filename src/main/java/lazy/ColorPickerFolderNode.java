package lazy;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import processing.core.PGraphics;

import static lazy.State.cell;
import static processing.core.PApplet.*;

class ColorPickerFolderNode extends FolderNode {

    @Expose
    String hexString;
    private int hex;
    @SuppressWarnings("FieldCanBeLocal")
    private final String HEX_NODE_NAME = "hex";
    private final String HUE_NODE_NAME = "hue";
    private final String SAT_NODE_NAME = "sat";
    private final String BR_NODE_NAME = "br";
    private final String ALPHA_NODE_NAME = "alpha";

    ColorPickerFolderNode(String path, FolderNode parentFolder, int hex) {
        super(path, parentFolder);
        setHex(hex);
        lazyInitNodes();
        idealWindowWidthInCells = 7;
        State.overwriteWithLoadedStateIfAny(this);
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
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.6f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.fill(hex);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    void loadValuesFromHex(boolean setDefaults) {
        lazyInitNodes();
        PGraphics colorProvider = State.getColorStore();
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
        PGraphics colorProvider = State.getColorStore();
        setHex(colorProvider.color(
                getValue(HUE_NODE_NAME),
                getValue(SAT_NODE_NAME),
                getValue(BR_NODE_NAME),
                getValue(ALPHA_NODE_NAME)));
    }

    PickerColor getColor() {
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

    int getHex() {
        return hex;
    }

    void setHex(int hex) {
        if (hex == 0) {
            hex = unhex("00010101");
        }
        this.hex = hex;
        hexString = hex(hex);
    }

    @Override
    void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("hexString");
        if (loadedString != null) {
            setHex(unhex(loadedString.getAsString()));
            loadValuesFromHex(true);
        }
    }

    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
//        super.keyPressedOverNode(e, x, y);
//        - we don't want to copy the contents of the folder itself... let this ColorPickerFolderNode handle it
        if (e.getKeyCode() == KeyCodes.CTRL_C) {
            Utils.setClipboardString(getHexString());
        }
        if (e.getKeyCode() == KeyCodes.CTRL_V) {
            String pastedString = Utils.getClipboardString();
            if(pastedString.length() == 6){
                // ensure full alpha if the pasted hex is without alpha
                pastedString = "FF" + pastedString;
            }
            try {
                int pastedHex = (int) Long.parseLong(pastedString, 16);
                setHex(pastedHex);
                loadValuesFromHex(false);
                State.onUndoableActionEnded();
            } catch (NumberFormatException nfe) {
                println("Could not parse hex color from input string: \"" + pastedString + "\"");
            }
        }
    }

    void setHue(float hueToAdd) {
        ColorSliderNode hueSlider = (ColorSliderNode) findChildByName(HUE_NODE_NAME);
        hueSlider.valueFloat = Utils.hueModulo(hueSlider.valueFloat + hueToAdd);
        loadValuesFromHSBA();
    }
}
