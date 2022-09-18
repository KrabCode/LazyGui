package lazy.windows.nodes.sliders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;
import lazy.themes.ThemeStore;
import lazy.InternalShaderStore;
import lazy.State;
import lazy.Utils;
import lazy.KeyCodes;
import lazy.themes.ThemeColorType;
import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.NodeType;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

public class SliderNode extends AbstractNode {

    public SliderNode(String path, NodeFolder parentFolder, float defaultValue) {
        super(NodeType.VALUE, path, parentFolder);
        valueFloatDefault = defaultValue;
        valueFloatDefaultOriginal = defaultValue;
        if(!Float.isNaN(defaultValue)){
            valueFloat = defaultValue;
        }
        valueFloatMin = -Float.MAX_VALUE;
        valueFloatMax =  Float.MAX_VALUE;
        valueFloatPrecisionDefault = 0.1f;
        valueFloatPrecision = valueFloatPrecisionDefault;
        valueFloatConstrained = false;
        initSliderPrecisionArrays();
        State.overwriteWithLoadedStateIfAny(this);
    }

    public SliderNode(String path, NodeFolder parentFolder, float defaultValue, float min, float max, float defaultPrecision, boolean constrained) {
        super(NodeType.VALUE, path, parentFolder);
        valueFloatDefault = defaultValue;
        valueFloatDefaultOriginal = defaultValue;
        if(!Float.isNaN(defaultValue)){
            valueFloat = defaultValue;
        }
        valueFloatMin = min;
        valueFloatMax = max;
        valueFloatPrecision = defaultPrecision;
        valueFloatPrecisionDefault = defaultPrecision;
        valueFloatConstrained = constrained;
        initSliderPrecisionArrays();
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Expose
    public float valueFloat;
    @Expose
    public float valueFloatPrecision;
    @Expose
    public final float valueFloatDefaultOriginal;

    public float valueFloatDefault;
    public float valueFloatMin;
    public float valueFloatMax;
    public boolean valueFloatConstrained;
    public float valueFloatPrecisionDefault;
    float backgroundScrollX = 0;


    PVector mouseDelta = new PVector();
    protected ArrayList<Float> precisionRange = new ArrayList<>();
    HashMap<Float, Integer> precisionRangeDigitsAfterDot = new HashMap<>();
    protected int currentPrecisionIndex;

    String shaderPath = "sliderBackground.glsl";

    private void initSliderPrecisionArrays() {
        initPrecision();
        loadPrecisionFromNode();
    }

    public void initSliderBackgroundShader(){
        InternalShaderStore.getShader(shaderPath);
    }

    private void initPrecision() {
        precisionRange.add(0.00001f);
        precisionRange.add(0.0001f);
        precisionRange.add(0.001f);
        precisionRange.add(0.01f);
        precisionRange.add(0.1f);
        precisionRange.add(1.0f);
        precisionRange.add(10.0f);
        precisionRange.add(100.0f);
        currentPrecisionIndex = 4;
        precisionRangeDigitsAfterDot.put(0.00001f, 5);
        precisionRangeDigitsAfterDot.put(0.0001f, 4);
        precisionRangeDigitsAfterDot.put(0.001f, 3);
        precisionRangeDigitsAfterDot.put(0.01f, 2);
        precisionRangeDigitsAfterDot.put(0.1f, 1);
        precisionRangeDigitsAfterDot.put(1f, 0);
        precisionRangeDigitsAfterDot.put(10f, 0);
        precisionRangeDigitsAfterDot.put(100f, 0);
    }

    private void loadPrecisionFromNode() {
        float p = valueFloatPrecision;
        for (int i = 0; i < precisionRange.size() - 1; i++) {
            float thisValue = precisionRange.get(i);
            float nextValue = precisionRange.get(i + 1);
            if (thisValue == p) {
                currentPrecisionIndex = i;
                return;
            } else if (thisValue < p && nextValue > p) {
                currentPrecisionIndex = i + 1;
                precisionRange.add(i + 1, p);
                precisionRangeDigitsAfterDot.put(p, String.valueOf(p).length() - 2);
            }
        }
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        updateDrawSliderNodeValue(pg);
    }

    void updateDrawSliderNodeValue(PGraphics pg) {
        String valueText = getPrintableValue();
        if (isDragged || isMouseOverNode) {
            updateValue();
            boolean constrainedThisFrame = tryConstrainValue();
            drawBackgroundScroller(pg, constrainedThisFrame);
            mouseDelta.x = 0;
            mouseDelta.y = 0;
        }
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, valueText);
    }

    @Override
    public String getPrintableValue() {
        return getValueToDisplay().replaceAll(",", ".");
    }

    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        if(!constrainedThisFrame){
            backgroundScrollX -= mouseDelta.x;
        }
        updateDrawBackgroundShader(pg);
        pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        pg.noStroke();
        pg.rect(0,0, size.x, size.y);
        pg.resetShader();
    }

    protected void updateDrawBackgroundShader(PGraphics pg) {
        PShader shader = InternalShaderStore.getShader(shaderPath);
        shader.set("scrollX", backgroundScrollX);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadSize", size.x, size.y);
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        InternalShaderStore.shader(shaderPath, pg);
    }

    public String getValueToDisplay() {
        int fractionPadding = precisionRangeDigitsAfterDot.get(valueFloatPrecision);
        if (fractionPadding == 0) {
            return String.valueOf(floor(valueFloat));
        }
        if(Float.isNaN(valueFloat)){
            return "NaN";
        }
        return nf(valueFloat, 0, fractionPadding);
    }

    public String getPrecisionToDisplay() {
        float p = valueFloatPrecision;
        if (p >= 1) {
            p = floor(p);
        }
        if (abs(p - 0.0001f) < 0.00001f) {
            return "0.0001";
        }
        if (abs(p - 0.00001f) < 0.000001f) {
            return "0.00001";
        }
        if (abs(p - 0.000001f) < 0.0000001f) {
            return "0.000001";
        }
        return nf(p);
    }

    @Override
    public void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x,y, dir);
        if(dir > 0){
            decreasePrecision();
        }else if(dir < 0){
            increasePrecision();
        }
    }



    private void increasePrecision() {
        currentPrecisionIndex = min(currentPrecisionIndex + 1, precisionRange.size() - 1);
        setPrecisionToNode();
    }

    private void decreasePrecision() {
        currentPrecisionIndex = max(currentPrecisionIndex - 1, 0);
        setPrecisionToNode();
    }

    protected void setPrecisionToNode() {
        valueFloatPrecision = precisionRange.get(currentPrecisionIndex);
        validatePrecision();
    }

    private void updateValue() {
        float delta = mouseDelta.x * precisionRange.get(currentPrecisionIndex);
        valueFloat -= delta;
    }

    protected boolean tryConstrainValue() {
        boolean constrained = false;
        if (valueFloatConstrained) {
            if(valueFloat > valueFloatMax || valueFloat < valueFloatMin){
                constrained = true;
            }
            valueFloat = constrain(valueFloat, valueFloatMin, valueFloatMax);
        }
        return constrained;
    }

    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if(e.getKeyChar() == 'r'){
            if(Float.isNaN(valueFloatDefault)){
                valueFloat = valueFloatDefault;
            }
            valueFloatPrecision = valueFloatPrecisionDefault;
            currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
            onValueChangedFromOutside();
        }
        tryReadNumpadInput(e);
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_C) {
            Utils.setClipboardString(Float.toString(valueFloat));
        }

        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            try{
                float clipboardValue = Float.parseFloat(Utils.getClipboardString());
                if(!Float.isNaN(clipboardValue)){
                    valueFloat = clipboardValue;
                }
            }catch(NumberFormatException nfe){
                println("Could not parse float from this clipboard string: " + Utils.getClipboardString());
            }
        }
    }

    private void tryReadNumpadInput(KeyEvent e) {
        switch(e.getKeyChar()){
            case '0': valueFloat = 0; break;
            case '1': valueFloat = 1; break;
            case '2': valueFloat = 2; break;
            case '3': valueFloat = 3; break;
            case '4': valueFloat = 4; break;
            case '5': valueFloat = 5; break;
            case '6': valueFloat = 6; break;
            case '7': valueFloat = 7; break;
            case '8': valueFloat = 8; break;
            case '9': valueFloat = 9; break;
            case '+': valueFloat += valueFloatPrecision; break;
            case '-': valueFloat -= valueFloatPrecision; break;
            case '*': increasePrecision(); break;
            case '/': decreasePrecision(); break;
            default: {
                return;
            }
        }
        State.onUndoableActionEnded();
        onValueChangedFromOutside();
    }

    // TODO just use a setter in the color pickers instead of this inheritance madness :<
    protected void onValueChangedFromOutside() {

    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragNodeContinue(e, x, y, px, py);
        if(isDragged){
            mouseDelta.x = px - x;
            mouseDelta.y = py - y;
            e.setConsumed(true);
        }

    }


    @Override
    public void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if(json.has("valueFloatPrecision")){
            valueFloatPrecision = json.get("valueFloatPrecision").getAsFloat();
        }
        if (json.has("valueFloat") &&
                json.has("valueFloatDefaultOriginal") &&
                json.get("valueFloatDefaultOriginal").getAsFloat() == valueFloatDefault) {
            valueFloat = json.get("valueFloat").getAsFloat();
        }
    }
}
