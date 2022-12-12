package lazy;

import processing.core.PGraphics;

class SaveNode extends AbstractNode {
    final String fileName;

    SaveNode(String path, FolderNode parent, String fileName) {
        super(NodeType.TRANSIENT, path, parent);
        this.fileName = fileName;
    }

    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, "load");
    }

    void mousePressedOverNode(float x, float y) {
        State.loadStateFromFile(fileName);
    }
}
