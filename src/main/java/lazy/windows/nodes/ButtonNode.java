package lazy.windows.nodes;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import lazy.global.State;

public class ButtonNode extends AbstractNode {
    public ButtonNode(String path, NodeFolder folder) {
        super(NodeType.TRANSIENT, path, folder);
    }

    public boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawRightButton(pg);
        boolean mousePressed = State.app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }

    public boolean getBooleanValueAndSetItToFalse() {
        boolean result = valueBoolean;
        valueBoolean = false;
        return result;
    }

    @Override
    public String getPrintableValue() {
        return "(button)";
    }
}
