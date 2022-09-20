package lazy.windows.nodes.colorPicker;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import lazy.State;
import lazy.Utils;
import lazy.windows.nodes.NodeFolder;
import lazy.KeyCodes;

import static processing.core.PApplet.*;

public class ColorPickerFolder extends NodeFolder {

    @Expose
    public String hexString;
    private int hex;
    private String hueNodeName, satNodeName, brNodeName , alphaNodeName;
    private final PickerColor outputColor = new PickerColor();

    public ColorPickerFolder(String path, NodeFolder parentFolder, int hex) {
        super(path, parentFolder);
        setHex(hex);
        lazyInitNodes();
        State.overwriteWithLoadedStateIfAny(this);
        loadValuesFromHex(true);
        idealWindowWidth = cell * 7;
    }

    protected void lazyInitNodes() {
        if(children.size() > 0){
            return;
        }
        hueNodeName = "hue";
        satNodeName = "sat";
        brNodeName = "br";
        alphaNodeName = "a";
        children.add(new ColorPreviewNode(path + "/preview", this));
        children.add(new ColorSliderNode.HueNode(path + "/" + hueNodeName, this));
        children.add(new ColorSliderNode.SaturationNode(path + "/" + satNodeName, this));
        children.add(new ColorSliderNode.BrightnessNode(path + "/" + brNodeName, this));
        children.add(new ColorSliderNode.AlphaNode(path + "/" + alphaNodeName, this));
        children.add(new HexNode(path + "/hex", this));
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

    public void loadValuesFromHex(boolean setDefaults) {
        lazyInitNodes();
        PGraphics colorProvider = State.normalizedColorProvider;
        ((ColorSliderNode) findChildByName(hueNodeName)).valueFloat = colorProvider.hue(hex);
        ((ColorSliderNode) findChildByName(satNodeName)).valueFloat = colorProvider.saturation(hex);
        ((ColorSliderNode) findChildByName(brNodeName)).valueFloat = colorProvider.brightness(hex);
        ((ColorSliderNode) findChildByName(alphaNodeName)).valueFloat = colorProvider.alpha(hex);
        if(setDefaults){
            ((ColorSliderNode) findChildByName(hueNodeName)).valueFloatDefault = colorProvider.hue(hex);
            ((ColorSliderNode) findChildByName(satNodeName)).valueFloatDefault = colorProvider.saturation(hex);
            ((ColorSliderNode) findChildByName(brNodeName)).valueFloatDefault = colorProvider.brightness(hex);
            ((ColorSliderNode) findChildByName(alphaNodeName)).valueFloatDefault = colorProvider.alpha(hex);
        }
    }

    public void loadValuesFromHSBA(){
        PGraphics colorProvider = State.normalizedColorProvider;
        setHex(colorProvider.color(
                getValue(hueNodeName),
                getValue(satNodeName),
                getValue(brNodeName),
                getValue(alphaNodeName)));
    }

    public PickerColor getColor() {
        outputColor.hex = hex;
        outputColor.hue = hue();
        outputColor.saturation = saturation();
        outputColor.brightness = brightness();
        outputColor.alpha = alpha();
        return outputColor;
    }

    private float getValue(String nodeName){
        return ((ColorSliderNode) findChildByName(nodeName)).valueFloat;
    }

    public float hue() {
        return getValue(hueNodeName);
    }

    public float saturation() {
        return getValue(satNodeName);
    }

    public float brightness() {
        return getValue(brNodeName);
    }
    public float alpha() {
        return getValue(alphaNodeName);
    }

    public String getHexString() {
        return hexString;
    }

    public int getHex() {
        return hex;
    }

    public void setHex(int hex) {
        if(hex == 0){
            hex = unhex("00010101");
        }
        this.hex = hex;
        hexString = hex(hex);
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("hexString");
        if(loadedString != null){
            setHex(unhex(loadedString.getAsString()));
            loadValuesFromHex(true);
        }
    }

    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_C) {
            Utils.setClipboardString(getHexString());
        }
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            String pastedString = Utils.getClipboardString();
            try{
                int pastedHex = (int)Long.parseLong(pastedString, 16);
                setHex(pastedHex);
                loadValuesFromHex(false);
            }catch(NumberFormatException nfe){
                println("Could not parse hex color from input string: \"" + pastedString + "\"");
            }
        }
    }

}
