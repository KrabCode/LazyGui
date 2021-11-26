package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.global.Palette;

import static processing.core.PConstants.CENTER;

public class ButtonNode extends ValueNode {
    public ButtonNode(String path, FolderNode folder) {
        super(path, folder);
    }

    public boolean valueBoolean = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x * 0.9f, size.y * 0.5f);
        if(mouseOver){
            if (armed){
                pg.fill(Palette.focusForeground);
            }else{
                pg.fill(Palette.normalForeground);
            }
        }
        pg.stroke(Palette.normalForeground);
        pg.rectMode(CENTER);
        pg.rect(0,0, cell * 0.75f, cell * 0.5f);
        if(valueBoolean){
            valueBoolean = false;
        }
    }

    boolean armed = false;

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
