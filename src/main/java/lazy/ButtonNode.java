package lazy;


import processing.core.PGraphics;

import static lazy.Globals.app;

class ButtonNode extends AbstractNode {
    ButtonNode(String path, FolderNode folder) {
        super(NodeType.TRANSIENT, path, folder);
    }

    boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawRightButton(pg);
        boolean mousePressed = app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    void mouseDragNodeContinue(LazyMouseEvent e) {

    }

    boolean getBooleanValueAndSetItToFalse() {
        boolean result = valueBoolean;
        valueBoolean = false;
        return result;
    }

    @Override
    String getConsolePrintableValue() {
        return "(button)";
    }
}
