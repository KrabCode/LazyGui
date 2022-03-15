package toolbox.windows.nodes.sliders;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;
import toolbox.global.palettes.PaletteStore;
import toolbox.global.InternalShaderStore;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.global.KeyCodes;
import toolbox.global.palettes.PaletteColorType;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.NodeType;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

public class SliderNode extends AbstractNode {

    public SliderNode(String path, NodeFolder parentFolder, float defaultValue) {
        super(NodeType.VALUE_NODE, path, parentFolder);
        valueFloatDefault = defaultValue;
        valueFloat = valueFloatDefault;
        valueFloatMin = -Float.MAX_VALUE;
        valueFloatMax =  Float.MAX_VALUE;
        valueFloatPrecisionDefault = 0.1f;
        valueFloatPrecision = valueFloatPrecisionDefault;
        valueFloatConstrained = false;
        initSliderPrecisionArrays();
        State.overwriteWithLoadedStateIfAny(this);
    }

    public SliderNode(String path, NodeFolder parentFolder, float defaultValue, float min, float max, float defaultPrecision, boolean constrained) {
        super(NodeType.VALUE_NODE, path, parentFolder);
        if(defaultPrecision == 0){
            println("default precision for slider at path " + path + " should not be 0");
        }
        valueFloatDefault = defaultValue;
        valueFloat = defaultValue;
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
    public float valueFloatMin;
    public float valueFloatMax;
    public float valueFloatDefault;
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
    protected void updateDrawInlineNode(PGraphics pg) {
        updateDrawSliderNodeValue(pg);
    }

    void updateDrawSliderNodeValue(PGraphics pg) {
        String valueText = getValueToDisplay().replaceAll(",", ".");
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


    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        if(!constrainedThisFrame){
            backgroundScrollX -= mouseDelta.x;
        }
        updateDrawBackgroundShader(pg);
        pg.fill(PaletteStore.get(PaletteColorType.NORMAL_BACKGROUND));
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
        valueFloat += delta;
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
            valueFloat = valueFloatDefault;
            valueFloatPrecision = valueFloatPrecisionDefault;
            currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
            onValueResetToDefault();
        }
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_C) {
            Utils.setClipboardString(Float.toString(valueFloat));
        }

        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            try{
                valueFloat = Float.parseFloat(Utils.getClipboardString());
            }catch(NumberFormatException nfe){
                println("Could not parse float from this clipboard string: " + Utils.getClipboardString());
            }
        }
    }

    protected void onValueResetToDefault() {

    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragNodeContinue(e, x, y, px, py);
        if(isDragged){
            mouseDelta.x = px - x;
            mouseDelta.y = py - y;
        }

    }


    @Override
    public void overwriteState(JsonElement loadedNode) {
        valueFloatDefault = loadedNode.getAsJsonObject().get("valueFloat").getAsFloat();
        if(loadedNode.getAsJsonObject().has("valueFloatPrecision")){
            valueFloatPrecision = loadedNode.getAsJsonObject().get("valueFloatPrecision").getAsFloat();
        }
        valueFloat = valueFloatDefault;
    }
}
