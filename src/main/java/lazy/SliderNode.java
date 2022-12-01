package lazy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;


import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

class SliderNode extends AbstractNode {

    ArrayList<Character> numpadChars = new Utils.ArrayListBuilder<Character>()
            .add('0','1','2','3','4','5','6','7','8','9')
            .build();
    private int numpadInputAppendLastFrame = -1;
    protected String numpadBufferValue = "";
    protected boolean showPercentIndicatorWhenConstrained = true;
    boolean verticalMouseMode = false;
    private boolean displayShader = true;

    SliderNode(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained) {
        super(NodeType.VALUE, path, parentFolder);
        valueFloatDefault = defaultValue;
        if (!Float.isNaN(defaultValue)) {
            valueFloat = defaultValue;
        }
        valueFloatMin = min;
        valueFloatMax = max;
        valueFloatConstrained = constrained &&
                max != Float.MAX_VALUE && max != Integer.MAX_VALUE &&
                min != -Float.MAX_VALUE && min != -Integer.MAX_VALUE;
        initSliderPrecisionArrays();
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Expose
    float valueFloat;
    @Expose
    int currentPrecisionIndex;
    @Expose
    float valueFloatPrecision;
    float valueFloatPrecisionMin = 0.00001f;
    float valueFloatDefault;
    float valueFloatMin;
    float valueFloatMax;
    boolean valueFloatConstrained;
    float backgroundScrollX = 0;


    float mouseDeltaX, mouseDeltaY;
    protected ArrayList<Float> precisionRange = new ArrayList<>();
    HashMap<Float, Integer> precisionRangeDigitsAfterDot = new HashMap<>();


    String shaderPath = "sliderBackground.glsl";

    private void initSliderPrecisionArrays() {
        initPrecision();
    }

    void initSliderBackgroundShader() {
        InternalShaderStore.getShader(shaderPath);
    }

    private void initPrecision() {
        precisionRange.add(0.0001f);
        precisionRange.add(0.001f);
        precisionRange.add(0.01f);
        precisionRange.add(0.1f);
        precisionRange.add(1f);
        precisionRange.add(10.0f);
        precisionRange.add(100.0f);
        precisionRangeDigitsAfterDot.put(valueFloatPrecisionMin, 5);
        precisionRangeDigitsAfterDot.put(0.0001f, 4);
        precisionRangeDigitsAfterDot.put(0.001f, 3);
        precisionRangeDigitsAfterDot.put(0.01f, 2);
        precisionRangeDigitsAfterDot.put(0.1f, 1);
        precisionRangeDigitsAfterDot.put(1f, 0);
        precisionRangeDigitsAfterDot.put(10f, 0);
        precisionRangeDigitsAfterDot.put(100f, 0);
        setSensiblePrecision(String.valueOf(valueFloat));
    }

    private void setSensiblePrecision(String value) {
        if(value.equals("0")){
            setPrecisionIndexAndValue(precisionRange.indexOf(1f));
            return;
        }
        if(value.matches("[0-9]*\\.[0-9]*")){
            int fractionalDigitLength = value.split("\\.")[1].length();
            setPrecisionIndexAndValue(4 - fractionalDigitLength);
            return;
        }
        setPrecisionIndexAndValue(2 + value.length());
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        updateDrawSliderNodeValue(pg);
    }

    void updateDrawSliderNodeValue(PGraphics pg) {
        if(numpadInputJustFinished()){
            if(numpadBufferValue.endsWith(".")){
                numpadBufferValue += "0";
            }
            println("setting buffer: ", numpadBufferValue);
            if(trySetValueFloat(numpadBufferValue)){
                setSensiblePrecision(numpadBufferValue);
            }
        }
        if (isDragged || isMouseOverNode) {
            updateValueMouseInteraction();
            boolean constrainedThisFrame = tryConstrainValue();
            drawBackgroundScroller(pg, constrainedThisFrame);
            mouseDeltaX = 0;
            mouseDeltaY = 0;
        }
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, getValueToDisplay() + (isNumpadInputActive() ? "_" : ""));
    }

    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        if (!constrainedThisFrame) {
            backgroundScrollX -= verticalMouseMode ? mouseDeltaY : mouseDeltaX;
        }
        float widthMult = 1f;
        if (valueFloatConstrained && showPercentIndicatorWhenConstrained) {
            widthMult = constrain(norm(valueFloat, valueFloatMin, valueFloatMax), 0, 1);
            backgroundScrollX = 0;
        }
        if(displayShader){
            updateBackgroundShader(pg);
        }
        pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        pg.noStroke();
        pg.rect(1, 0, (size.x - 1) * widthMult, size.y);
        if(displayShader){
            pg.resetShader();
        }
    }

    protected void updateBackgroundShader(PGraphics pg) {
        PShader shader = InternalShaderStore.getShader(shaderPath);
        shader.set("scrollX", backgroundScrollX);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadSize", size.x, size.y);
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        pg.shader(shader);
    }

    String getValueToDisplay() {
        int fractionPadding = precisionRangeDigitsAfterDot.get(valueFloatPrecision);
        if(isNumpadInputActive()){
            return numpadBufferValue;
        }
        if (Float.isNaN(valueFloat)) {
            return "NaN";
        }
        return nf(valueFloat, 0, fractionPadding).replaceAll(",", ".");
    }

    @Override
    void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x, y, dir);
        if (dir > 0) {
            increasePrecision();
        } else if (dir < 0) {
            decreasePrecision();
        }
    }

    private void setWholeNumberPrecision(){
        for (int i = 0; i < precisionRange.size(); i++) {
            if(precisionRange.get(i) >= 1f){
                setPrecisionIndexAndValue(i);
                break;
            }
        }
    }

    void decreasePrecision() {
        setPrecisionIndexAndValue(min(currentPrecisionIndex + 1, precisionRange.size() - 1));
    }

    void increasePrecision() {
        setPrecisionIndexAndValue(max(currentPrecisionIndex - 1, 0));
    }

    protected void setPrecisionIndexAndValue(int newPrecisionIndex) {
        currentPrecisionIndex = constrain(newPrecisionIndex, 0, precisionRange.size() - 1);
        valueFloatPrecision = precisionRange.get(currentPrecisionIndex);
        validatePrecision();
    }

    private void updateValueMouseInteraction() {
        float mouseDelta = verticalMouseMode ? mouseDeltaY : mouseDeltaX;
        if(mouseDelta != 0){
            float delta = mouseDelta * precisionRange.get(currentPrecisionIndex);
            setValueFloat(valueFloat - delta);
        }
    }

    protected boolean tryConstrainValue() {
        boolean constrained = false;
        if (valueFloatConstrained) {
            if (valueFloat > valueFloatMax || valueFloat < valueFloatMin) {
                constrained = true;
            }
            valueFloat = constrain(valueFloat, valueFloatMin, valueFloatMax);
        }
        return constrained;
    }

    @Override
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if (e.getKeyChar() == 'r') {
            if (!Float.isNaN(valueFloatDefault)) {
                setValueFloat(valueFloatDefault);
            }
        }
        tryReadNumpadInput(e);
        if (e.getKeyCode() == KeyCodes.CTRL_C) {
            String value = getValueToDisplay();
            if(value.endsWith(".")){
                value += "0";
            }
            Utils.setClipboardString(value);
        }
        if (e.getKeyCode() == KeyCodes.CTRL_V) {
            String clipboardString = Utils.getClipboardString();
            try {
                float clipboardValue = Float.parseFloat(clipboardString);
                if (!Float.isNaN(clipboardValue)) {
                    setValueFloat(clipboardValue);
                } else {
                    println("Could not parse float from this clipboard string: " + clipboardString);
                }
            } catch (NumberFormatException nfe) {
                println("Could not parse float from this clipboard string: " + clipboardString);
            }
        }
    }

    private void tryReadNumpadInput(LazyKeyEvent e) {
        boolean inReplaceMode = isNumpadInReplaceMode();
        if(numpadChars.contains(e.getKeyChar())){
            tryAppendNumberInputToValue(Integer.valueOf(String.valueOf(e.getKeyChar())), inReplaceMode);
        }
        switch (e.getKeyChar()) {
            case '.':
            case ',':
                numpadInputAppendLastFrame = State.app.frameCount;
                if(numpadBufferValue.isEmpty()){
                    numpadBufferValue += "0";
                }
                if(!numpadBufferValue.endsWith(".")) {
                    numpadBufferValue += ".";
                }
                // set the precision for it to be ready for increasing precision when appending fractions
                break;
            case '+':
                if(valueFloat <= 0){
                    setValueFloat(abs(valueFloat));
                    onValueFloatChanged();
                }
                break;
            case '-':
                if(inReplaceMode){
                    numpadBufferValue = "-";
                }else if(valueFloat >= 0){
                    setValueFloat(-valueFloat);
                    onValueFloatChanged();
                }
                break;
            case '*':
                decreasePrecision();
                break;
            case '/':
                increasePrecision();
                break;
            default: {
                return;
            }
        }
        State.onUndoableActionEnded();
    }

    private void tryAppendNumberInputToValue(Integer input, boolean inReplaceMode) {
        String inputString = String.valueOf(input);
        numpadInputAppendLastFrame = State.app.frameCount;
        if (inReplaceMode) {
            numpadBufferValue = inputString;
            if(!numpadBufferValue.equals("0")){
                // when I only reset a value to 0 I usually want to keep its old precision
                // when I start typing something other than 0 I usually do want whole number precision
                setWholeNumberPrecision();
            }
            return;
        }
        numpadBufferValue += inputString;
    }

    protected boolean isNumpadInputActive() {
        return numpadInputAppendLastFrame != -1 &&
                State.app.frameCount <= numpadInputAppendLastFrame + State.keyboardInputAppendCooldown;
    }

    private boolean isNumpadInReplaceMode() {
        return numpadInputAppendLastFrame == -1 ||
                State.app.frameCount - numpadInputAppendLastFrame > State.keyboardInputAppendCooldown;
    }

    protected boolean numpadInputJustFinished(){
        return numpadInputAppendLastFrame != -1 &&
                State.app.frameCount == numpadInputAppendLastFrame + State.keyboardInputAppendCooldown;
    }

    private boolean trySetValueFloat(String toParseAsFloat) {
        float parsed;
        try{
             parsed = Float.parseFloat(toParseAsFloat);
        }catch (NumberFormatException formatException){
            println(formatException.getMessage(), formatException);
            return false;
        }
        setValueFloat(parsed);
        return true;
    }

    protected void setValueFloat(float floatToSet) {
        valueFloat = floatToSet;
        onValueFloatChanged();
    }

    protected void onValueFloatChanged() {
        tryConstrainValue();
    }

    @Override
    void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        mouseDeltaX = e.getPrevX() - e.getX();
        mouseDeltaY = e.getPrevY() - e.getY();
        e.setConsumed(true);
    }

    @Override
    void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if (json.has("currentPrecisionIndex")) {
            currentPrecisionIndex = json.get("currentPrecisionIndex").getAsInt();
        }
        if (json.has("valueFloatPrecision")) {
            valueFloatPrecision = json.get("valueFloatPrecision").getAsFloat();
        }
        if (json.has("valueFloat")) {
            setValueFloat(json.get("valueFloat").getAsFloat());
        }
    }

    @Override
    String getConsolePrintableValue() {
        return getValueToDisplay();
    }

    public void enableShader() {
        displayShader = true;
    }

    public void disableShader() {
        displayShader = false;
    }
}
