package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.Palette;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class ToggleNode extends Node {


    public ToggleNode(String path, NodeType toggle, FolderNode folder) {
        super(path,toggle,folder);
    }

    public boolean valueBooleanDefault = false;
    public boolean valueBoolean = false;
    float handlePosNorm = 0;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawToggleHandle(pg);
    }

    private void drawToggleHandle(PGraphics pg) {
        strokeContentBasedOnFocus(pg);
        fillContentBasedOnFocus(pg);
        float handleWidth = size.x * 0.1f;
        float handleXCenter = size.x * 0.9f;
        float handleXLeft = handleXCenter - handleWidth * 0.5f;
        float handleXRight = handleXCenter + handleWidth * 0.5f;
        float lerpAmt = 0.3f;
        if(valueBoolean){
            handlePosNorm = lerp(handlePosNorm,1, lerpAmt);
        }else{
            pg.fill(Palette.standardContentFill);
            pg.stroke(Palette.standardContentStroke);
            handlePosNorm = lerp(handlePosNorm,0, lerpAmt);
        }
        float handleX = map(handlePosNorm, 0, 1, handleXLeft, handleXRight);
        float handleY = size.y * 0.52f;
        float handleDiameter = size.y * 0.5f;
        float handleWeight = 8;
        pg.strokeWeight(handleWeight);
        pg.line(handleXLeft, handleY, handleXRight, handleY);
        pg.noStroke();
        pg.ellipse(handleX, handleY, handleDiameter, handleDiameter);
    }

    boolean armed = false;

    @Override
    public void nodePressed(float x, float y) {
        super.nodePressed(x, y);
        armed = true;
    }

    public void nodeReleased(float x, float y){
        super.nodeReleased(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
        }
        armed = false;
    }
}
