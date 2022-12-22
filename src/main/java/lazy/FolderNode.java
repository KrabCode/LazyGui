package lazy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import processing.core.PGraphics;

import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.State.cell;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

/**
 * A node that opens a new window with child nodes when clicked.
 */
class FolderNode extends AbstractNode {

    /**
     * CopyOnWriteArrayList is needed to avoid concurrent modification
     * because the children get drawn by one thread and user input changes the list from another thread
     */
    @Expose
    final CopyOnWriteArrayList<AbstractNode> children = new CopyOnWriteArrayList<>();

    @Expose
    Window window;

    protected float idealWindowWidthInCells = State.defaultWindowWidthInCells;
    protected boolean isWindowResizable = true;

    FolderNode(String path, FolderNode parent) {
        super(NodeType.FOLDER, path, parent);
        UtilSaves.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    void updateValuesRegardlessOfParentWindowOpenness() {
        // if child count changed since last frame
        // re-assess window width in cells, make it the minimum that can snugly fit
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawMiniatureWindowIcon(pg);
    }

    private void drawMiniatureWindowIcon(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);
        fillBackgroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.6f;
        float miniCell = cell * 0.18f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.rect(0, 0, previewRectSize, previewRectSize); // window border
        pg.rectMode(CORNER);
        pg.translate(-previewRectSize * 0.5f, -previewRectSize * 0.5f);
        pg.pushStyle();
        AbstractNode enabledNode = findChildByName("enabled");
        if(enabledNode == null){
            enabledNode = findChildByName("active");
        }
        if(enabledNode == null){
            enabledNode = findChildByName("visible");
        }
        if (enabledNode != null &&
                enabledNode.className.contains("ToggleNode") &&
                ((ToggleNode) enabledNode).valueBoolean) {
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }
        pg.rect(0, 0, previewRectSize, miniCell); // handle
        pg.popStyle();
        pg.rect(previewRectSize - miniCell, 0, miniCell, miniCell); // close button
    }

    @Override
    void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        WindowManager.setFocus(parent.window);
        WindowManager.uncoverOrCreateWindow(this);
        this.isDragged = false;

    }

    protected AbstractNode findChildByName(String name) {
        for (AbstractNode node : children) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    @Override
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        // copy + paste whole folders of controls
        if (e.getKeyCode() == KeyCodes.CTRL_C) {
            UtilClipboard.setClipboardString(UtilSaves.getFolderAsJsonString(this));
        }
        if (e.getKeyCode() == KeyCodes.CTRL_V) {
            String toPaste = UtilClipboard.getClipboardString();
            UtilSaves.loadStateFromJsonString(toPaste, path);
        }
    }


    void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonObject wholeObject = loadedNode.getAsJsonObject();
        if (wholeObject.has("window")) {
            JsonObject winObject = wholeObject.getAsJsonObject("window");
            if (winObject.has("closed") && winObject.has("posX") && winObject.has("posY") && winObject.has("windowSizeX")) {
                boolean isClosed = winObject.get("closed").getAsBoolean();
                float posX = winObject.get("posX").getAsFloat();
                float posY = winObject.get("posY").getAsFloat();
                float sizeX = winObject.get("windowSizeX").getAsFloat();
                if (!isClosed) {
                    WindowManager.uncoverOrCreateWindow(this, false, posX, posY, sizeX);
                    // open it at this spot
                }
            }
        }
    }
}
