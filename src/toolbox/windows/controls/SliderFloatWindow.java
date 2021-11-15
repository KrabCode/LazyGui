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

import static processing.core.PApplet.*;

@SuppressWarnings("DuplicatedCode")
public class SliderFloatWindow extends Window {

    // TODO lock mouse in place when drag
    ArrayList<Float> precisionRange;

    int currentPrecisionIndex;

    public SliderFloatWindow(Node node, PVector pos) {

//      TODO use long to represent value and precision for excellent in-built rounding for the cost of converting it to float
        super(node, pos, new PVector(GlobalState.cell * 10, GlobalState.cell * 2));
        precisionRange = new ArrayList<>() {{
            add(0.001f);
            add(0.01f);
            add(0.1f);
            add(1.0f);
            add(10.0f);
            add(100.0f);
            add(1000.0f);
        }};
        currentPrecisionIndex = 3;
        tryLoadPrecisionFromNode();
    }

    private void tryLoadPrecisionFromNode() {
        for (int i = 0; i < precisionRange.size() - 1; i++) {
            float thisValue = precisionRange.get(i);
            float nextValue = precisionRange.get(i + 1);
            if (thisValue == node.valueFloatPrecision) {
                currentPrecisionIndex = i;
                return;
            } else if (
                    thisValue < node.valueFloatPrecision &&
                            nextValue > node.valueFloatPrecision) {
                currentPrecisionIndex = i + 1;
                precisionRange.add(i + 1, node.valueFloatPrecision);
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
        return String.valueOf(node.valueFloat);
    }

    private String getPrecisionToDisplay() {
        float p = node.valueFloatPrecision;
        if (p >= 1) {
            p = floor(p);
        }
        return nf(p);
    }

    @Override
    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
        super.reactToMouseDraggedInsideWithoutDrawing(x, y, px, py);
        node.valueFloat += (x - px) * node.valueFloatPrecision;
        validateValue();
    }


    void validateValue() {
        if (node.valueFloatConstrained) {
            node.valueFloat = constrain(node.valueFloat, node.valueFloatMin, node.valueFloatMax);
        }
    }

    @Override
    public void mouseWheelMoved(MouseEvent e, int dir, float x, float y) {
        super.mouseWheelMoved(e, dir, x, y);
        if (isPointInsideWindow(x, y) || isDraggedInside) {
            if (dir > 0) {
                increasePrecision();
            } else if (dir < 0) {
                decreasePrecision();
            }
        }
        // don't want to consume the event because it needs to work on mouse over,
        // so it should be handled in every window that cares
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        if (keyEvent.getKeyChar() == 'r' && isThisFocused()) {
            node.valueFloat = node.valueFloatDefault;
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
    }
}
