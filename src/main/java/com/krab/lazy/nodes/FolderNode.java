package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.stores.FontStore;
import com.krab.lazy.utils.KeyCodes;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import com.krab.lazy.utils.ClipboardUtils;
import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.windows.Window;
import com.krab.lazy.windows.WindowManager;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.krab.lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.ceil;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

/**
 * A node that opens a new window with child nodes when clicked.
 */
public class FolderNode extends AbstractNode {

    /**
     * CopyOnWriteArrayList is needed to avoid concurrent modification
     * because the children get drawn by one thread and user input changes the list from another thread
     */
    @Expose
    public final List<AbstractNode> children = new CopyOnWriteArrayList<>();

    @Expose
    public
    Window window;

    public float idealWindowWidthInCells = LayoutStore.defaultWindowWidthInCells;

    public FolderNode(String path, FolderNode parent) {
        super(NodeType.FOLDER, path, parent);
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {

    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        String displayName = getInlineDisplayNameOverridableByContents(name);
        drawLeftText(pg, displayName);
        drawRightBackdrop(pg, cell);
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
        if(isFolderActiveJudgingByContents()){
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }
        pg.rect(0, 0, previewRectSize, miniCell); // handle
        pg.popStyle();
        pg.rect(previewRectSize - miniCell, 0, miniCell, miniCell); // close button
    }

    private String getInlineDisplayNameOverridableByContents(String name) {
        String overridableName = name;
        String desiredClassName = TextNode.class.getSimpleName();
        AbstractNode renamingNode = findChildByName("");
        if(renamingNode == null || !renamingNode.className.contains(desiredClassName)){
            renamingNode = findChildByNameStartsWith("label");
        }
        if(renamingNode == null || !renamingNode.className.contains(desiredClassName)){
            renamingNode = findChildByNameStartsWith("name");
        }
        if(renamingNode != null && renamingNode.className.contains(desiredClassName) && ((TextNode) renamingNode).stringValue.length() > 0){
            overridableName = ((TextNode) renamingNode).stringValue;
        }
        return overridableName;
    }

    boolean isFolderActiveJudgingByContents(){
        String desiredClassName = ToggleNode.class.getSimpleName();
        AbstractNode enabledNode = findChildByName("");
        if(enabledNode == null || !enabledNode.className.contains(desiredClassName)){
            enabledNode = findChildByNameStartsWith("active");
        }
        if(enabledNode == null || !enabledNode.className.contains(desiredClassName)){
            enabledNode = findChildByNameStartsWith("enabled");
        }
        if(enabledNode == null || !enabledNode.className.contains(desiredClassName)){
            enabledNode = findChildByNameStartsWith("visible");
        }
        return enabledNode != null &&
                enabledNode.className.contains(desiredClassName) &&
                ((ToggleNode) enabledNode).valueBoolean;
    }

    @Override
    public void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        WindowManager.setFocus(parent.window);
        WindowManager.uncoverOrCreateWindow(this);
        this.isInlineNodeDragged = false;
    }

    protected AbstractNode findChildByName(String name) {
        if(name.startsWith("/")){
            name = name.substring(1);
        }
        for (AbstractNode node : children) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    protected AbstractNode findChildByNameStartsWith(String nameStartsWith) {
        if(name.startsWith("/")){
            nameStartsWith = name.substring(1);
        }
        for (AbstractNode node : children) {
            if (node.name.startsWith(nameStartsWith)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        // copy + paste whole folders of controls
        if ((e.isControlDown() && e.getKeyCode() == KeyCodes.C)) {
            ClipboardUtils.setClipboardString(JsonSaveStore.getFolderAsJsonString(this));
            e.consume();
        }
        if (e.isControlDown() && e.getKeyCode() == KeyCodes.V) {
            String toPaste = ClipboardUtils.getClipboardString();
            JsonSaveStore.loadStateFromJsonString(toPaste, path);
            e.consume();
        }
    }

    public void overwriteState(JsonElement loadedNode) {
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
                }else if(window != null){
                    window.closed = true;
                }
            }
        }
    }

    public float autosuggestWindowWidthForContents() {
        float maximumSpaceTotal = cell * LayoutStore.defaultWindowWidthInCells;
        if(!LayoutStore.getAutosuggestWindowWidth()){
            return maximumSpaceTotal;
        }
        float spaceForName = cell * 2;
        float spaceForValue = cell * 2;
        float minimumSpaceTotal = spaceForName + spaceForValue;
        float titleTextWidth = findTextWidthRoundedUpToWholeCells(name);
        spaceForName = PApplet.max(spaceForName, titleTextWidth);
        for (AbstractNode child : children) {
            float nameTextWidth = findTextWidthRoundedUpToWholeCells(child.name);
            spaceForName = PApplet.max(spaceForName, nameTextWidth);
            float valueTextWidth = findTextWidthRoundedUpToWholeCells(child.getValueAsString());
            spaceForValue = PApplet.max(spaceForValue, valueTextWidth);
        }
        return PApplet.constrain(spaceForName + spaceForValue, minimumSpaceTotal, maximumSpaceTotal);
    }

    private float findTextWidthRoundedUpToWholeCells(String textToMeasure) {
        PGraphics textWidthProvider = FontStore.getMainFontUtilsProvider();
        float leftTextWidth = textWidthProvider.textWidth(textToMeasure);
        return ceil(leftTextWidth / cell) * cell;
    }
}
