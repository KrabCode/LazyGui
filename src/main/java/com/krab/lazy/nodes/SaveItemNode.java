package com.krab.lazy.nodes;

import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.windows.WindowManager;
import processing.core.PGraphics;

class SaveItemNode extends AbstractNode {
    final String fileName;
    final SaveFolderNode saveFolderParent;

    SaveItemNode(String path, SaveFolderNode parent, String fileName) {
        super(NodeType.TRANSIENT, path, parent);
        saveFolderParent = parent;
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
        float unmovingWindowX = saveFolderParent.window.posX;
        float unmovingWindowY = saveFolderParent.window.posY;
        JsonSaveStore.loadStateFromFilePath(fileName);
        saveFolderParent.window.posX = unmovingWindowX;
        saveFolderParent.window.posY = unmovingWindowY;
        saveFolderParent.window.closed = false;
        WindowManager.setFocus(saveFolderParent.window);
        onActionEnded();
    }
}
