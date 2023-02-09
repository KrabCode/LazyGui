package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import lazy.input.LazyMouseEvent;
import lazy.stores.UndoRedoStore;
import lazy.stores.JsonSaveStore;
import processing.core.PGraphics;

import static lazy.stores.LayoutStore.cell;

public class ToggleNode extends AbstractNode {

    @Expose
    public
    boolean valueBoolean;
    protected boolean armed = false;

    public ToggleNode(String path, FolderNode folder, boolean defaultValue) {
        super(NodeType.VALUE, path, folder);
        valueBoolean = defaultValue;
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {

    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawRightBackdrop(pg, cell);
        drawRightToggleHandle(pg, valueBoolean);
    }

    @Override
    public void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        armed = true;
    }

    @Override
    public void mouseReleasedOverNode(float x, float y){
        super.mouseReleasedOverNode(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
            UndoRedoStore.onUndoableActionEnded();
        }
        armed = false;
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {

    }

    public void overwriteState(JsonElement loadedNode) {
        JsonElement booleanElement = loadedNode.getAsJsonObject().get("valueBoolean");
        if(booleanElement != null){
            valueBoolean = booleanElement.getAsBoolean();
        }
    }

    @Override
    public String getConsolePrintableValue() {
        return String.valueOf(valueBoolean);
    }
}
