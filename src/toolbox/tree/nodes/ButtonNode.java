package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class ButtonNode extends Node {
    public ButtonNode(String path, FolderNode folder) {
        super(NodeType.VALUE, path, folder);
    }

    public boolean valueBoolean = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(CENTER,CENTER);
        pg.text("button", size.x * 0.5f, size.y * 0.5f);
        if(mouseOver){
            String status = "ready";
            if(armed){
                status = "pressed";
            }
            pg.textAlign(RIGHT, CENTER);
            pg.text(status, size.x * 0.95f, size.y * 0.5f);
        }
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

    public void nodeReleased(float x, float y){
        super.nodeReleased(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
        }
        armed = false;
    }

}
