package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.StringConstants;
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
    public final CopyOnWriteArrayList<AbstractNode> children = new CopyOnWriteArrayList<>();

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
            renamingNode = findChildByNameStartsWith(StringConstants.FOLDER_AUTODETECT_LABEL);
        }
        if(renamingNode == null || !renamingNode.className.contains(desiredClassName)){
            renamingNode = findChildByNameStartsWith(StringConstants.FOLDER_AUTODETECT_NAME);
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
            enabledNode = findChildByNameStartsWith(StringConstants.FOLDER_AUTODETECT_ACTIVE);
        }
        if(enabledNode == null || !enabledNode.className.contains(desiredClassName)){
            enabledNode = findChildByNameStartsWith(StringConstants.FOLDER_AUTODETECT_ENABLED);
        }
        if(enabledNode == null || !enabledNode.className.contains(desiredClassName)){
            enabledNode = findChildByNameStartsWith(StringConstants.FOLDER_AUTODETECT_VISIBLE);
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

    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        boolean shouldRestoreWindows = JsonSaveStore.shouldLoadingRestoreWindows(path);
        if(!shouldRestoreWindows){
            return;
        }
        if(StringConstants.FOLDER_PATH_SAVES.equals(path)){
            // saves folder should be immune to changes caused by loading saves from it
            return;
        }
        if(!loadedNode.getAsJsonObject().has("window")){
            if(window != null){
                // the window object may have never been created and opened before the save was made
                // and therefore no "window" object even exists in the json because there was nothing to serialize,
                // and now the user loaded a save which doesn't say it should be open, so it needs to be closed
                window.closed = true;
            }
            // no window state to load other than that it should be closed
            return;
        }
        JsonObject windowJson = loadedNode.getAsJsonObject().get("window").getAsJsonObject();
        if(window == null){
            // creating a raw new Window() here to have a non-null object to overwrite with the loaded state,
            // bypassing the usual WindowManager.uncoverOrCreateWindow() since we don't need any
            // of the automatic window width autosuggestions or good window placement
            window = new Window(this, 0, 0, null);
            // but then we need to manually add this window to the WindowManager's window list
            WindowManager.addWindow(window);
        }
        if(windowJson.has("posX")){
            window.posX = windowJson.get("posX").getAsFloat();
        }
        if(windowJson.has("posY")){
            window.posY = windowJson.get("posY").getAsFloat();
        }
        if(windowJson.has("windowSizeX")){
            window.windowSizeX = windowJson.get("windowSizeX").getAsFloat();
        }
        if(windowJson.has("closed")){
            window.closed = windowJson.get("closed").getAsBoolean();
        }
    }
}
