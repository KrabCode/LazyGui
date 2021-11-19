package toolbox.tree.nodes;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

public class SliderNode extends Node {
    public SliderNode(String path, NodeType type, FolderNode parentFolder) {
        super(path, type, parentFolder);
    }


    public float valueFloat;
    public float valueFloatMin;
    public float valueFloatMax;
    public float valueFloatDefault;
    public boolean valueFloatConstrained;
    public float valueFloatPrecision = 1;
    public float valueFloatPrecisionDefault = 1;

    ArrayList<Float> precisionRange = new ArrayList<>();
    HashMap<Float, Integer> precisionRangeDigitsAfterDot = new HashMap<>();
    int currentPrecisionIndex;
    int minimumIntPrecisionIndex;

    public void initSliderPrecision() {
        initPrecision();
        loadPrecisionFromNode();
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
        precisionRange.add(1000.0f);
        precisionRange.add(10000.0f);
        currentPrecisionIndex = 4;
        minimumIntPrecisionIndex = 4;
        precisionRangeDigitsAfterDot.put(0.00001f, 5);
        precisionRangeDigitsAfterDot.put(0.0001f, 4);
        precisionRangeDigitsAfterDot.put(0.001f, 3);
        precisionRangeDigitsAfterDot.put(0.01f, 2);
        precisionRangeDigitsAfterDot.put(0.1f, 1);
        precisionRangeDigitsAfterDot.put(1f, 0);
        precisionRangeDigitsAfterDot.put(10f, 0);
        precisionRangeDigitsAfterDot.put(100f, 0);
        precisionRangeDigitsAfterDot.put(1000f, 0);
        precisionRangeDigitsAfterDot.put(10000f, 0);
    }

    private void loadPrecisionFromNode() {
        float p = valueFloatPrecision;
        for (int i = 0; i < precisionRange.size() - 1; i++) {
            float thisValue = precisionRange.get(i);
            float nextValue = precisionRange.get(i + 1);
            if (thisValue == p) {
                currentPrecisionIndex = i;
                return;
            } else if (
                    thisValue < p && nextValue > p) {
                currentPrecisionIndex = i + 1;
                precisionRange.add(i + 1, p);
                precisionRangeDigitsAfterDot.put(p, String.valueOf(p).length() - 2);
            }
        }
    }

    void updateDrawSliderNode(PGraphics pg) {
        String text = getValueToDisplay().replaceAll(",", ".");
        if (isDragged) {
            updateValue();
            boolean constrainedThisFrame = tryConstrainValue();
            drawBackgroundScroller(pg, constrainedThisFrame);
        }
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(CENTER, CENTER);
        float textMarginX = 5;
        pg.text(text,
                size.x * 0.5f,
                size.y * 0.5f
        );

        if(mouseOver){
            pg.textAlign(RIGHT, CENTER);
            pg.text(getPrecisionToDisplay(),
                    size.x - textMarginX,
                    size.y * 0.5f
            );
        }
    }

    float backgroundScrollX = 0;
    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        int count = 20;
        pg.stroke(Palette.draggedContentStroke);
        pg.strokeWeight(1.99f);
        if(!constrainedThisFrame){
            backgroundScrollX += GlobalState.app.mouseX - GlobalState.app.pmouseX;
        }
        for (int xi = 0; xi < count; xi++) {
            float x = map(xi, 0, count, 0, size.x) + backgroundScrollX;
            while(x < 0){
                x += size.x;
            }
            x = x%size.x;
            pg.line(x, 0, x, cell);
        }

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

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        updateDrawSliderNode(pg);
    }

    @Override
    public void mouseWheelMovedInsideNode(float x, float y, int dir) {
        super.mouseWheelMovedInsideNode(x,y, dir);
        if(dir > 0){
            decreasePrecision();
        }else if(dir < 0){
            increasePrecision();
        }
    }

    private void updateValue() {
        float delta = (GlobalState.app.mouseX - GlobalState.app.pmouseX) * precisionRange.get(currentPrecisionIndex);
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
    public void keyPressedInsideNode(KeyEvent e, float x, float y) {
        super.keyPressedInsideNode(e, x, y);
        if(e.getKeyChar() == 'r'){
            valueFloat = valueFloatDefault;
        }
    }
}
