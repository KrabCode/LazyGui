package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;


import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.stores.*;
import com.krab.lazy.utils.KeyCodes;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import com.krab.lazy.utils.ArrayListBuilder;
import com.krab.lazy.utils.ClipboardUtils;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.krab.lazy.stores.NormColorStore.*;
import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.*;

public class SliderNode extends AbstractNode {

    @Expose
    public double valueFloat; // named float for backwards compatibility with json saves, but it's a double
    @Expose
    protected float valueFloatPrecision;

    // currentPrecisionIndex used to be @Exposed too, but it carried duplicate information to valueFloatPrecision,
    // and it became misleading when the precisionRange list changed
    protected int currentPrecisionIndex;

    float valueFloatDefault;
    final float valueFloatMin;
    final float valueFloatMax;
    final boolean valueFloatConstrained;
    float backgroundScrollX = 0;
    float mouseDeltaX, mouseDeltaY;
    boolean verticalMouseMode = false;
    protected String numpadBufferValue = "";
    protected boolean showPercentIndicatorWhenConstrained = true;

    // TODO the old precisionRange is just too buggy, let's move to BigDecimal and use it everywhere
    BigDecimal valueBigDecimal = new BigDecimal(0);
    int decimalDigits = 0;

    protected final ArrayList<Float> precisionRange = new ArrayListBuilder<Float>()
            .add(0.000001f)
            .add(0.00001f)
            .add(0.0001f)
            .add(0.001f)
            .add(0.01f)
            .add(0.1f)
            .add(1f)
            .add(10.0f)
            .add(100.0f).build();

    private final Map<Integer, Integer> precisionIndexMappedToDecimalCount = new HashMap<Integer, Integer>(){
        {
            put(0, 6);
            put(1, 5);
            put(2, 4);
            put(3, 3);
            put(4, 2);
            put(5, 1);
            put(6, 0);
            put(7, 0);
            put(8, 0);
        }
    };


    // true by default locally to be overridden by the global setting LayoutStore.shouldDisplaySquigglyEquals() that is false by default
    protected boolean displaySquigglyEquals = true;
    protected static final String SQUIGGLY_EQUALS = "≈ ";

    final ArrayList<Character> numpadChars = new ArrayListBuilder<Character>()
            .add('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            .build();
    private int numpadInputAppendLastMillis = -1;
    private boolean wasNumpadInputActiveLastFrame = false;


    public static final String REGEX_ZERO_OR_ONE_MINUS_SIGN = "[-*]?";
    private static final String REGEX_FRACTION_SEPARATOR = "[.,]";
    private static final String REGEX_ANY_NUMBER_SERIES = "[0-9]*";
    private static final String FRACTIONAL_FLOAT_REGEX = REGEX_ZERO_OR_ONE_MINUS_SIGN
                                                        + REGEX_ANY_NUMBER_SERIES
                                                        + REGEX_FRACTION_SEPARATOR
                                                        + REGEX_ANY_NUMBER_SERIES;
    private final String shaderPath = "sliderBackground.glsl";
    protected int maximumFloatPrecisionIndex = -1;
    protected int minimumFloatPrecisionIndex = -1;
    private String valueStringWhenMouseDragStarted = null;

    public SliderNode(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained, boolean displaySquigglyEquals){
        this(path, parentFolder, defaultValue, min, max, constrained);
        this.displaySquigglyEquals = displaySquigglyEquals;
    }

    public SliderNode(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained) {
        super(NodeType.VALUE, path, parentFolder);
        valueFloatDefault = defaultValue;
        if (!Float.isNaN(defaultValue)) {
            valueFloat = defaultValue;
        }
        valueFloatMin = min;
        valueFloatMax = max;
        valueFloatConstrained = constrained &&
                max != Float.MAX_VALUE &&
                min != -Float.MAX_VALUE;
        setSensiblePrecision(nf((float) valueFloat, 0, 0));
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
    }

    public void initSliderBackgroundShader() {
        ShaderStore.getShader(shaderPath);
    }

    private void setSensiblePrecision(String value) {
        if(valueFloatConstrained && (valueFloatMax - valueFloatMin) <= 1){
            setPrecisionIndexAndValue(precisionRange.indexOf(0.01f));
            return;
        }
        if (value.equals("0") || value.equals("0.0")) {
            setPrecisionIndexAndValue(precisionRange.indexOf(0.1f));
            return;
        }
        if (value.matches(FRACTIONAL_FLOAT_REGEX)) {
            int fractionalDigitLength = value.split(REGEX_FRACTION_SEPARATOR)[1].length();
            setPrecisionIndexAndValue(precisionRange.indexOf(1f) - fractionalDigitLength);
            return;
        }
        setPrecisionIndexAndValue(precisionRange.indexOf(1f));
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        boolean constrainedThisFrame = tryConstrainValue();
        if (isInlineNodeDragged || isMouseOverNode) {
            drawBackgroundScroller(pg, constrainedThisFrame);
        }
        mouseDeltaX = 0;
        mouseDeltaY = 0;
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        if (isInlineNodeDragged || isMouseOverNode) {
            updateValueMouseInteraction();
        }
        updateNumpad();
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        fillForegroundBasedOnMouseOver(pg);
        drawLeftText(pg, name);
        drawRightText(pg, getValueToDisplay() + (isNumpadInputActive() ? "_" : ""), true);
    }

    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        if (!constrainedThisFrame) {
            backgroundScrollX -= verticalMouseMode ? mouseDeltaY : mouseDeltaX;
        }
        float percentIndicatorNorm = 1f;
        boolean shouldShowPercentIndicator = valueFloatConstrained && showPercentIndicatorWhenConstrained;
        if (shouldShowPercentIndicator) {
            percentIndicatorNorm = constrain(norm((float) valueFloat, valueFloatMin, valueFloatMax), 0, 1);
            backgroundScrollX = 0;
        }

        updateBackgroundShader(pg);
        pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        pg.noStroke();
        pg.rect(1, 0, (size.x - 1) * percentIndicatorNorm, size.y);
        pg.resetShader();

        if (shouldShowPercentIndicator) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
            pg.strokeWeight(2);
            float lineX = (size.x - 1) * percentIndicatorNorm;
            pg.line(lineX, 0, lineX, size.y);
        }
    }

    protected void updateBackgroundShader(PGraphics pg) {
        PShader shader = ShaderStore.getShader(shaderPath);
        shader.set("scrollX", backgroundScrollX);
        int bgColor = ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND);
        int fgColor = ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND);
        shader.set("colorA", red(bgColor), green(bgColor), blue(bgColor));
        shader.set("colorB", red(fgColor), green(fgColor), blue(fgColor));
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        pg.shader(shader);
    }

    protected String getValueToDisplay() {
        // the numpadBufferValue flickers back to the old valueFloat for one frame if we just rely on "isNumpadActive()"
        // so we keep displaying the buffer for 1 more frame with "wasNumpadInputActiveLastFrame"
        if (isNumpadInputActive() || wasNumpadInputActiveLastFrame) {
            return numpadBufferValue;
        }
        if (Double.isNaN(valueFloat)) {
            return "NaN";
        }
        String valueToDisplay;
        boolean isFractionalPrecision = valueFloatPrecision % 1f > 0;
        if (isFractionalPrecision) {
            int digitsAfterDecimal = precisionIndexMappedToDecimalCount.get(currentPrecisionIndex);
            valueToDisplay = nf((float) valueFloat, 0, max(0, digitsAfterDecimal));
        } else {
            valueToDisplay = nf(Math.round((float) valueFloat), 0, 0);
        }
        if (displaySquigglyEquals && LayoutStore.shouldDisplaySquigglyEquals()) {
            String valueWithoutRounding = nf((float) valueFloat, 0, 0);
            boolean precisionRoundingHidesInformation = valueToDisplay.length() < valueWithoutRounding.length();
            if (precisionRoundingHidesInformation) {
                valueToDisplay = SQUIGGLY_EQUALS + valueToDisplay;
            }
        }
        // java float literals use . so we also use . to be consistent
        valueToDisplay = valueToDisplay.replaceAll(",", ".");
        return valueToDisplay;
    }

    @Override
    public void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x, y, dir);
        if(!HotkeyStore.isHotkeyMouseWheelActive()){
            return;
        }
        if (dir > 0) {
            increasePrecision();
        } else if (dir < 0) {
            decreasePrecision();
        }
    }

    private void setWholeNumberPrecision() {
        for (int i = 0; i < precisionRange.size(); i++) {
            if (precisionRange.get(i) >= 1f) {
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
        if (!validatePrecision(newPrecisionIndex)) {
            return;
        }
        currentPrecisionIndex = constrain(newPrecisionIndex, 0, precisionRange.size() - 1);
        valueFloatPrecision = precisionRange.get(currentPrecisionIndex);
    }

    protected boolean validatePrecision(int newPrecisionIndex) {
        return (maximumFloatPrecisionIndex == -1 || newPrecisionIndex <= maximumFloatPrecisionIndex) &&
                (minimumFloatPrecisionIndex == -1 || newPrecisionIndex >= minimumFloatPrecisionIndex);
    }

    private void updateValueMouseInteraction() {
        double mouseDelta = verticalMouseMode ? mouseDeltaY : mouseDeltaX;
        if (mouseDelta != 0) {
            double delta = mouseDelta * (double) precisionRange.get(currentPrecisionIndex);
            setValueFloat(valueFloat - delta);
            mouseDeltaX = 0;
            mouseDeltaY = 0;
        }
    }

    protected boolean tryConstrainValue() {
        boolean constrained = false;
        if (valueFloatConstrained) {
            if (valueFloat > valueFloatMax || valueFloat < valueFloatMin) {
                constrained = true;
            }
            valueFloat = constrain((float) valueFloat, valueFloatMin, valueFloatMax);
        }
        return constrained;
    }

    private void updateNumpad() {
        if (!isNumpadInputActive() && wasNumpadInputActiveLastFrame) {
            if (numpadBufferValue.endsWith(".")) {
                numpadBufferValue += "0";
            }
            if (tryParseAndSetValueFloat(numpadBufferValue)) {
                setSensiblePrecision(numpadBufferValue);
            }
        }
        wasNumpadInputActiveLastFrame = isNumpadInputActive();
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if (e.getKey() == 'r') {
            if (!Float.isNaN(valueFloatDefault)) {
                setValueFloat(valueFloatDefault);
            }
            e.consume();
        }
        tryReadNumpadInput(e);
        if (e.isControlDown() && e.getKeyCode() == KeyCodes.C) {
            String value = getValueToDisplay().replaceAll(SQUIGGLY_EQUALS, "");
            if (value.endsWith(".")) {
                value += "0";
            }
            ClipboardUtils.setClipboardString(value);
            e.consume();
        }
        if (e.isControlDown() && e.getKeyCode() == KeyCodes.V) {
            String clipboardString = ClipboardUtils.getClipboardString();

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
            e.consume();
        }
    }

    @Override
    public void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        valueStringWhenMouseDragStarted = getValueAsString();
    }

    @Override
    public void mouseReleasedAnywhere(LazyMouseEvent e) {
        super.mouseReleasedAnywhere(e);
        if(valueStringWhenMouseDragStarted != null && !valueStringWhenMouseDragStarted.equals(getValueAsString())){
            onValueChangingActionEnded();
        }
        valueStringWhenMouseDragStarted = null;
    }

    private void tryReadNumpadInput(LazyKeyEvent e) {
        boolean inReplaceMode = isNumpadInReplaceMode();
        if (numpadChars.contains(e.getKey())) {
            tryAppendNumberInputToBufferValue(Integer.valueOf(String.valueOf(e.getKey())), inReplaceMode);
            e.consume();
        }
        switch (e.getKey()) {
            case '.':
            case ',':
                setNumpadInputActiveStarted();
                if (numpadBufferValue.isEmpty()) {
                    numpadBufferValue += "0";
                }
                if (!numpadBufferValue.endsWith(".")) {
                    numpadBufferValue += ".";
                }
                e.consume();
                break;
            case '+':
            case '-':
                if (inReplaceMode) {
                    numpadBufferValue = "" + e.getKey();
                }
                setNumpadInputActiveStarted();
                e.consume();
                break;
            case '*':
                decreasePrecision();
                e.consume();
                break;
            case '/':
                increasePrecision();
                e.consume();
                break;
        }
    }

    private void tryAppendNumberInputToBufferValue(Integer input, boolean inReplaceMode) {
        String inputString = String.valueOf(input);
        setNumpadInputActiveStarted();
        if (inReplaceMode) {
            numpadBufferValue = inputString;
            if (input != 0) {
                // when I only reset a value to 0 I usually want to keep its old precision
                // when I start typing something other than 0 I usually do want whole number precision
                setWholeNumberPrecision();
            }
            return;
        }
        numpadBufferValue += inputString;
    }

    protected void setNumpadInputActiveStarted() {
        numpadInputAppendLastMillis = app.millis();
    }

    protected boolean isNumpadInputActive() {
        return numpadInputAppendLastMillis != -1 &&
                app.millis() <= numpadInputAppendLastMillis + DelayStore.getKeyboardBufferDelayMillis();
    }

    private boolean isNumpadInReplaceMode() {
        return numpadInputAppendLastMillis == -1 ||
                app.millis() - numpadInputAppendLastMillis > DelayStore.getKeyboardBufferDelayMillis();
    }

    private boolean tryParseAndSetValueFloat(String toParseAsFloat) {
        float parsed;
        try {
            parsed = Float.parseFloat(toParseAsFloat);
        } catch (NumberFormatException formatException) {
            println(formatException.getMessage(), formatException);
            return false;
        }
        setValueFloat(parsed);
        onValueChangingActionEnded();
        return true;
    }

    protected void setValueFloat(double floatToSet) {
        valueFloat = floatToSet;
        onValueFloatChanged();
    }

    protected void onValueFloatChanged() {
        tryConstrainValue();
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        mouseDeltaX = e.getPrevX() - e.getX();
        mouseDeltaY = e.getPrevY() - e.getY();
        e.setConsumed(true);
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if (json.has("valueFloatPrecision")) {
            valueFloatPrecision = json.get("valueFloatPrecision").getAsFloat();
            try {
                currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
            }catch(Exception e){
                println("Could not find precision index for valueFloatPrecision: "
                        + valueFloatPrecision + " in " + precisionRange + " for slider " + path);
            }
        }
        if (json.has("valueFloat")) {
            setValueFloat(json.get("valueFloat").getAsFloat());
        }
    }

    @Override
    public String getValueAsString() {
        return getValueToDisplay();
    }

}
