package toolbox.windows.controls;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;
import toolbox.windows.Window;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

@SuppressWarnings("DuplicatedCode")
public class SliderFloatWindow extends Window {

    // TODO lock mouse in place when drag
    ArrayList<Float> precisionRange = new ArrayList<>();
    HashMap<Float, Integer> precisionRangeDigitsAfterDot = new HashMap<Float, Integer>();
    int currentPrecisionIndex;
    int minimumIntPrecisionIndex;

    public SliderFloatWindow(Node node, PVector pos) {

//      TODO use long to represent value and precision for excellent in-built rounding for the cost of converting it to float
        super(node, pos, new PVector(GlobalState.cell * 8, GlobalState.cell * 2));
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
        precisionRangeDigitsAfterDot.put(1f,0);
        precisionRangeDigitsAfterDot.put(10f, 0);
        precisionRangeDigitsAfterDot.put(100f, 0);
        precisionRangeDigitsAfterDot.put(1000f, 0);
        precisionRangeDigitsAfterDot.put(10000f, 0);
    }

    private void loadPrecisionFromNode() {
        float p = node.valueFloatPrecision;
        for (int i = 0; i < precisionRange.size() - 1; i++) {
            float thisValue = precisionRange.get(i);
            float nextValue = precisionRange.get(i + 1);
            if (thisValue == p) {
                currentPrecisionIndex = i;
                return;
            } else if (
                    thisValue < p &&
                            nextValue > p) {
                currentPrecisionIndex = i + 1;
                precisionRange.add(i + 1, p);
                precisionRangeDigitsAfterDot.put(p, String.valueOf(p).length() - 2);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void drawContent(PGraphics pg) {
        String text = getValueToDisplay();
        if (isDraggedInside) {
            pg.noStroke();
            pg.fill(Palette.draggedContentFill);
            pg.rect(0, cell, windowSize.x, windowSize.y - cell);
        }
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, CENTER);
        float textMarginX = 5;
        pg.text(text,
                textMarginX,
                pg.height * 0.5f + GlobalState.font.getSize() * 0.5f
        );

        boolean isMouseInsideContent = isPointInsideContent(GlobalState.app.mouseX, GlobalState.app.mouseY);
        if (!isThisFocused() && !isMouseInsideContent) {
            return;
        }
        pg.textAlign(RIGHT, CENTER);
        pg.text(getPrecisionToDisplay(),
                pg.width - textMarginX,
                pg.height * 0.5f + GlobalState.font.getSize() * 0.5f
        );
    }

    protected String getValueToDisplay() {
        int fractionPadding = precisionRangeDigitsAfterDot.get(node.valueFloatPrecision);
        if(abs(node.valueFloat) > 1000){
            return String.valueOf(floor(node.valueFloat));
        }
        return nf(node.valueFloat, 0, fractionPadding);
    }

    private String getPrecisionToDisplay() {
        float p = node.valueFloatPrecision;
        if (p >= 1) {
            p = floor(p);
        }
        if(abs(p - 0.0001f) < 0.00001f){
            return "0.0001";
        }
        if(abs(p - 0.00001f) < 0.000001f){
            return "0.00001";
        }
        if(abs(p - 0.000001f) < 0.0000001f){
            return "0.000001";
        }
        return nf(p);
    }

    @Override
    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
        super.reactToMouseDraggedInsideWithoutDrawing(x, y, px, py);
        node.valueFloat += (x - px) * node.valueFloatPrecision;
        validateValue();
    }



    @Override
    public void mouseWheelMoved(MouseEvent e, int dir, float x, float y) {
        super.mouseWheelMoved(e, dir, x, y);
        if (isPointInsideWindow(x, y) || isDraggedInside) {
            if (dir > 0) {
                decreasePrecision();
            } else if (dir < 0) {
                increasePrecision();
            }
        }
        // don't want to consume the event because it needs to work on mouse over,
        // so it should be handled in every window that cares
        // this allows the user to interact with multiple windows at once, which may be a problem
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        if (keyEvent.getKeyChar() == 'r' && isThisFocused()) {
            node.valueFloat = node.valueFloatDefault;
            node.valueFloatPrecision = node.valueFloatPrecisionDefault;
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
        node.valueFloatPrecision = precisionRange.get(currentPrecisionIndex);
        validatePrecision();
    }

    protected void validateValue() {
        if (node.valueFloatConstrained) {
            node.valueFloat = constrain(node.valueFloat, node.valueFloatMin, node.valueFloatMax);
        }
    }

    protected void validatePrecision() {

    }
}
