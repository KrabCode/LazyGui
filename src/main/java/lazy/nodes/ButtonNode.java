package lazy.nodes;


import lazy.input.LazyMouseEvent;
import processing.core.PGraphics;

import static lazy.stores.GlobalReferences.app;

public class ButtonNode extends AbstractNode {
    public ButtonNode(String path, FolderNode folder) {
        super(NodeType.TRANSIENT, path, folder);
    }

    boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        boolean mousePressed = app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        super.drawNodeForeground(pg, name);
        drawRightButton(pg);
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {

    }

    public boolean getBooleanValueAndSetItToFalse() {
        boolean result = valueBoolean;
        valueBoolean = false;
        return result;
    }

    @Override
    public String getConsolePrintableValue() {
        return "(button)";
    }
}
