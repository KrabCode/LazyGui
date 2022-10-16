package lazy;

import processing.core.PGraphics;

class SaveNode extends AbstractNode {
    String fileName, fullPath;

    SaveNode(String path, NodeFolder parent, String fileName, String fullPath) {
        super(NodeType.TRANSIENT, path, parent);
        this.fileName = fileName;
        this.fullPath = fullPath;
    }

    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, "load");
    }

    void mousePressedOverNode(float x, float y) {
        State.loadStateFromFile(fileName);
    }
}
