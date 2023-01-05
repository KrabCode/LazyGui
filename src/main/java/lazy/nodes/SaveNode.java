package lazy.nodes;

import lazy.utils.JsonSaves;
import processing.core.PGraphics;

class SaveNode extends AbstractNode {
    final String fileName;

    SaveNode(String path, FolderNode parent, String fileName) {
        super(NodeType.TRANSIENT, path, parent);
        this.fileName = fileName;
    }

    protected void drawNodeBackground(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, "load", true);
    }

    public void mousePressedOverNode(float x, float y) {
        JsonSaves.loadStateFromFile(fileName);
    }
}
