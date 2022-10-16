package lazy;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;

class ButtonNode extends AbstractNode {
    ButtonNode(String path, NodeFolder folder) {
        super(NodeType.TRANSIENT, path, folder);
    }

    boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawRightButton(pg);
        boolean mousePressed = State.app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }

    boolean getBooleanValueAndSetItToFalse() {
        boolean result = valueBoolean;
        valueBoolean = false;
        return result;
    }

    @Override
    String getPrintableValue() {
        return "(button)";
    }
}
