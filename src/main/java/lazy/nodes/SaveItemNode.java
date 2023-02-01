package lazy.nodes;

import lazy.stores.UndoRedoStore;
import lazy.utils.JsonSaves;
import processing.core.PGraphics;

class SaveItemNode extends AbstractNode {
    final String fileName;

    SaveItemNode(String path, FolderNode parent, String fileName) {
        super(NodeType.TRANSIENT, path, parent);
        this.fileName = fileName;
    }

    protected void drawNodeBackground(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawRightText(pg, "load", true);
    }

    public void mousePressedOverNode(float x, float y) {
        JsonSaves.loadStateFromFile(fileName);
        UndoRedoStore.onUndoableActionEnded();
    }
}
