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
        drawToggleHandle(pg);
    }

    private void drawToggleHandle(PGraphics pg) {
        float rectWidth = cell * 0.3f;
        float rectHeight = cell * 0.3f;


        pg.rectMode(CENTER);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        if(isMouseOverNode){
            pg.stroke(PaletteStore.get(FOCUS_FOREGROUND));
        }else{
            pg.stroke(PaletteStore.get(NORMAL_FOREGROUND));
        }
        if(valueBoolean){
            pg.fill(PaletteStore.get(NORMAL_BACKGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(PaletteStore.get(FOCUS_FOREGROUND));
            pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
        }else{
            pg.fill(PaletteStore.get(NORMAL_FOREGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(PaletteStore.get(NORMAL_BACKGROUND));
            pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
        }
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
