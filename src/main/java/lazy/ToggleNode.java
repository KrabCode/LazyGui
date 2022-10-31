package lazy;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import processing.core.PGraphics;

class ToggleNode extends AbstractNode {

    @Expose
    boolean valueBoolean;
    boolean valueBooleanDefault;
    protected boolean armed = false;
    float handlePosNorm;

    ToggleNode(String path, FolderNode folder, boolean defaultValue) {
        super(NodeType.VALUE, path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawRightToggleHandle(pg, valueBoolean);
    }


    @Override
    void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        armed = true;
    }

    @Override
    void mouseReleasedOverNode(float x, float y){
        super.mouseReleasedOverNode(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
        }
        armed = false;
    }

    @Override
    void mouseDragNodeContinue(LazyMouseEvent e) {

    }

    void overwriteState(JsonElement loadedNode) {
        JsonElement booleanElement = loadedNode.getAsJsonObject().get("valueBoolean");
        if(booleanElement != null){
            valueBoolean = booleanElement.getAsBoolean();
            valueBooleanDefault = valueBoolean;
        }
    }

    @Override
    String getPrintableValue() {
        return String.valueOf(valueBoolean);
    }
}
