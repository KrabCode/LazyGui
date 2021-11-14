package toolbox.windows.controls;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.Window;

import static processing.core.PApplet.*;

@SuppressWarnings("DuplicatedCode")
public class SliderFloatWindow extends Window {

    // TODO lock mouse in place when drag
    float[] precisionRange = new float[]{
            0.001f,
            0.01f,
            0.1f,
            1.0f,
            10.0f,
            100.0f,
            1000.0f,
            10000.0f,
    };

    int currentPrecisionIndex = 3;

    public SliderFloatWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 10, GlobalState.cell * 2));
        setPrecision();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void drawContent(PGraphics pg) {
        String text = getValueToDisplay();
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, CENTER);
        float textMarginX = 5;
        pg.text(text,
                textMarginX,
                pg.height * 0.5f + GlobalState.font.getSize() * 0.5f
        );

        if (!isThisFocused()) {
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
        float p = node.precision;
        if (p >= 1) {
            p = floor(p);
        }
        return nf(p);
    }

    @Override
    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
        super.reactToMouseDraggedInsideWithoutDrawing(x, y, px, py);
        node.valueFloat += (x - px) * node.precision;
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
        e.setConsumed(true);
    }

    private void increasePrecision() {
        currentPrecisionIndex++;
        currentPrecisionIndex = min(precisionRange.length - 1, currentPrecisionIndex);
        setPrecision();
    }

    private void decreasePrecision() {
        currentPrecisionIndex--;
        currentPrecisionIndex = max(0, currentPrecisionIndex);
        setPrecision();
    }

    protected void setPrecision() {
        node.precision = precisionRange[currentPrecisionIndex];
    }
}
