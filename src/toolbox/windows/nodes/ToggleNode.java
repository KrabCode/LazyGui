package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.State;

public class ToggleNode extends AbstractNode {

    @Expose
    public boolean valueBoolean;
    public boolean valueBooleanDefault;
    boolean armed = false;
    public float handlePosNorm;

    public ToggleNode(String path, NodeFolder folder, boolean defaultValue) {
        super(NodeType.VALUE_NODE, path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawToggleHandle(pg, valueBoolean);
    }


    @Override
    public void nodeClicked(float x, float y) {
        super.nodeClicked(x, y);
        armed = true;
    }

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
        valueBooleanDefault = loadedNode.getAsJsonObject().get("valueBoolean").getAsBoolean();
        valueBoolean = valueBooleanDefault;
    }
}
