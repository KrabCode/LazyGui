package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.PaletteStore;

import static processing.core.PConstants.CENTER;
import static toolbox.global.palettes.PaletteColorType.*;

public class ToggleNode extends AbstractNode {

    @Expose
    public boolean valueBoolean;
    public boolean valueBooleanDefault;
    boolean armed = false;
    public float handlePosNorm;

    public ToggleNode(String path, FolderNode folder, boolean defaultValue) {
        super(NodeType.VALUE_ROW, path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawToggleHandle(pg, valueBoolean);
    }


    @Override
    public void nodePressed(float x, float y) {
        super.nodePressed(x, y);
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
        this.valueBoolean = loadedNode.getAsJsonObject().get("valueBoolean").getAsBoolean();
    }
}
