package lazy.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import lazy.State;

public class ToggleNode extends AbstractNode {

    @Expose
    public boolean valueBoolean;
    public boolean valueBooleanDefault;
    protected boolean armed = false;
    public float handlePosNorm;

    public ToggleNode(String path, NodeFolder folder, boolean defaultValue) {
        super(NodeType.VALUE, path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeInner(PGraphics pg) {
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
        }
        armed = false;
    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }

    public void overwriteState(JsonElement loadedNode) {
        JsonElement booleanElement = loadedNode.getAsJsonObject().get("valueBoolean");
        if(booleanElement != null){
            valueBoolean = booleanElement.getAsBoolean();
            valueBooleanDefault = valueBoolean;
        }
    }

    @Override
    public String getPrintableValue() {
        return String.valueOf(valueBoolean);
    }
}
