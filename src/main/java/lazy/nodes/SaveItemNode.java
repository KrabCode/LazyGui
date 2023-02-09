package lazy.nodes;

import lazy.stores.UndoRedoStore;
import lazy.stores.JsonSaveStore;
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
        JsonSaveStore.loadStateFromFile(fileName);
        UndoRedoStore.onUndoableActionEnded();
    }
}
