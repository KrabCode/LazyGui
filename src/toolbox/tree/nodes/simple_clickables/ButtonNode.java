package toolbox.tree.nodes.simple_clickables;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class ButtonNode extends ValueNode {
    public ButtonNode(String path, FolderNode folder) {
        super(path, folder);
    }

    public boolean valueBoolean = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnFocus(pg);
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

    public void mouseReleasedOverNode(float x, float y){
        super.mouseReleasedOverNode(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
        }
        armed = false;
    }

}
