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
        fillTextColorBasedOnFocus(pg);
        String status = "off";
        if(valueBoolean){
            status = "on";
        }
        pg.textAlign(RIGHT, CENTER);
        pg.text(status, size.x * 0.95f, size.y * 0.5f);
    }

    private void drawToggleHandle(PGraphics pg) {
        strokeContentBasedOnFocus(pg);
        fillContentBasedOnFocus(pg);
        float handleWidth = size.x * 0.1f;
        float handleXLeft = size.x * 0.5f - handleWidth * 0.5f;
        float handleXRight = size.x * 0.5f + handleWidth * 0.5f;
        if(valueBoolean){
            handlePosNorm = lerp(handlePosNorm,1, 0.1f);
        }else{
            pg.fill(Palette.standardContentFill);
            pg.stroke(Palette.standardContentStroke);
            handlePosNorm = lerp(handlePosNorm,0, 0.1f);
        }
        float handleX = map(handlePosNorm, 0, 1, handleXLeft, handleXRight);
        float handleY = size.y * 0.5f;
        float handleDiameter = size.y * 0.5f;
        pg.strokeWeight(5);
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
