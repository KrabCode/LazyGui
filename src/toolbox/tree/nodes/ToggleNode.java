package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.global.Palette;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;
import static processing.core.PConstants.CORNER;

public class ToggleNode extends ValueNode {

    public boolean valueBooleanDefault;
    public boolean valueBoolean;
    boolean armed = false;
    public float handlePosNorm;

    public ToggleNode(String path, FolderNode folder, boolean defaultValue) {
        super(path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawToggleHandle(pg);
    }

    private void drawToggleHandle(PGraphics pg) {
        float handleWidth = cell * 0.75f;
        float handleXCenter = size.x - cell * 0.8f;
        float handleXLeft = handleXCenter - handleWidth * 0.5f;
        float handleXRight = handleXCenter + handleWidth * 0.5f;
        float lerpAmt = 0.3f;
        if(valueBoolean){
            handlePosNorm = lerp(handlePosNorm,1, lerpAmt);
        }else{
            handlePosNorm = lerp(handlePosNorm,0, lerpAmt);
        }
        float handleX = map(handlePosNorm, 0, 1, handleXLeft, handleXRight);
        float handleY = size.y * 0.5f;
        float handleDiameter = cell * 0.4f;
        float handleHeight = 5;
        pg.fill(valueBoolean ? Palette.windowBorder : Palette.focusBackground);
        if(mouseOver){
            pg.fill(Palette.normalForeground);
        }
        pg.rectMode(CORNER);
        pg.rect(handleXLeft, handleY-handleHeight / 2f, handleWidth, handleHeight, 8);
        pg.noStroke();
        pg.fill(valueBoolean ? Palette.normalForeground : Palette.windowBorder);
        if(mouseOver && valueBoolean){
            pg.fill(Palette.focusForeground);
        }
        pg.ellipse(handleX, handleY, handleDiameter, handleDiameter);
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
}
