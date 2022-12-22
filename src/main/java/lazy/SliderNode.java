package lazy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;


import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.ArrayList;

import static processing.core.PApplet.*;

class SliderNode extends AbstractNode {

    @Expose
    float valueFloat;
    @Expose
    int currentPrecisionIndex;
    @Expose
    float valueFloatPrecision;
    float valueFloatDefault;
    final float valueFloatMin;
    final float valueFloatMax;
    final boolean valueFloatConstrained;
    float backgroundScrollX = 0;
    float mouseDeltaX, mouseDeltaY;
    boolean verticalMouseMode = false;
    protected String numpadBufferValue = "";
    protected boolean showPercentIndicatorWhenConstrained = true;
    protected final ArrayList<Float> precisionRange = new ArrayListBuilder<Float>()
            .add(0.0001f)
            .add(0.001f)
            .add(0.01f)
            .add(0.1f)
            .add(1f)
            .add(10.0f)
            .add(100.0f).build();

    final ArrayList<Character> numpadChars = new ArrayListBuilder<Character>()
            .add('0','1','2','3','4','5','6','7','8','9')
            .build();
    private int numpadInputAppendLastMillis = -1;
    private boolean wasNumpadInputActiveLastFrame = false;

    private static final String FRACTIONAL_FLOAT_REGEX = "[0-9]*[,.][0-9]*";
    private boolean displayShader = true;
    private final String shaderPath = "sliderBackground.glsl";

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
        setSensiblePrecision(nf(valueFloat, 0, 0));
        UtilSaves.overwriteWithLoadedStateIfAny(this);
    }

    void initSliderBackgroundShader() {
        ShaderStore.getShader(shaderPath);
    }

    private void setSensiblePrecision(String value) {
        if(value.equals("0") || value.equals("0.0")){
            setPrecisionIndexAndValue(precisionRange.indexOf(1f));
            return;
        }
        if(value.matches(FRACTIONAL_FLOAT_REGEX)){
            int fractionalDigitLength = getFractionalDigitLength(value);
            setPrecisionIndexAndValue(4 - fractionalDigitLength);
            return;
        }
        setPrecisionIndexAndValue(precisionRange.indexOf(1f));
    }

    private int getFractionalDigitLength(String value) {
        if(value.contains(".") || value.contains(",")){
            return value.split("[.,]")[1].length();
        }
        return 0;
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        updateDrawSliderNodeValue(pg);
    }

    void updateDrawSliderNodeValue(PGraphics pg) {
        updateNumpad();
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
        PShader shader = ShaderStore.getShader(shaderPath);
        shader.set("scrollX", backgroundScrollX);
        int bgColor = ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND);
        int fgColor = ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND);
        shader.set("colorA", State.red(bgColor), State.green(bgColor), State.blue(bgColor));
        shader.set("colorB", State.red(fgColor), State.green(fgColor), State.blue(fgColor));
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        pg.shader(shader);
    }

    String getValueToDisplay() {
        if(isNumpadInputActive()){
            return numpadBufferValue;
        }
        if (Float.isNaN(valueFloat)) {
            return "NaN";
        }
        String valueToDisplay;
        boolean isFractionalPrecision = valueFloatPrecision % 1f > 0;
        if(isFractionalPrecision){
             valueToDisplay = nf(valueFloat, 0, getFractionalDigitLength(String.valueOf(valueFloatPrecision)));
        }else{
            valueToDisplay = nf(floor(valueFloat), 0, 0);
        }
        // java float literals use . so we also use .
        return valueToDisplay.replaceAll(",", ".");
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

    private void updateNumpad() {
        if(!isNumpadInputActive() && wasNumpadInputActiveLastFrame){
            if(numpadBufferValue.endsWith(".")){
                numpadBufferValue += "0";
            }
            if(trySetValueFloat(numpadBufferValue)){
                setSensiblePrecision(numpadBufferValue);
            }
        }
        wasNumpadInputActiveLastFrame = isNumpadInputActive();
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
            UtilClipboard.setClipboardString(value);
        }
        if (e.getKeyCode() == KeyCodes.CTRL_V) {
            String clipboardString = UtilClipboard.getClipboardString();
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
                setNumpadInputActiveStarted();
                if(numpadBufferValue.isEmpty()){
                    numpadBufferValue += "0";
                }
                if(!numpadBufferValue.endsWith(".")) {
                    numpadBufferValue += ".";
                }
                break;
            case '+':
            case '-':
                if(inReplaceMode){
                    numpadBufferValue = "" + e.getKeyChar();
                }
                setNumpadInputActiveStarted();
                break;
            case '*':
                decreasePrecision();
                break;
            case '/':
                increasePrecision();
                break;
        }
    }

    private void tryAppendNumberInputToValue(Integer input, boolean inReplaceMode) {
        String inputString = String.valueOf(input);
        setNumpadInputActiveStarted();
        if (inReplaceMode) {
            numpadBufferValue = inputString;
            if(input != 0){
                // when I only reset a value to 0 I usually want to keep its old precision
                // when I start typing something other than 0 I usually do want whole number precision
                setWholeNumberPrecision();
            }
            return;
        }
        numpadBufferValue += inputString;
    }

    protected void setNumpadInputActiveStarted(){
        numpadInputAppendLastMillis = State.app.millis();
    }

    protected boolean isNumpadInputActive() {
        return numpadInputAppendLastMillis != -1 &&
                State.app.millis() <= numpadInputAppendLastMillis + State.keyboardInputAppendCooldownMillis;
    }

    private boolean isNumpadInReplaceMode() {
        return numpadInputAppendLastMillis == -1 ||
                State.app.millis() - numpadInputAppendLastMillis > State.keyboardInputAppendCooldownMillis;
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
