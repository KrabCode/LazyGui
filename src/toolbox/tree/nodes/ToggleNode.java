package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class ToggleNode extends Node {
    public ToggleNode(String path, NodeType toggle, FolderNode folder) {
        super(path,toggle,folder);
    }

    public boolean valueBooleanDefault = false;
    public boolean valueBoolean = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(CENTER,CENTER);
        pg.text("toggle", size.x * 0.5f, size.y * 0.5f);

        String status = "off";
        if(valueBoolean){
            status = "on";
        }
        pg.textAlign(RIGHT, CENTER);
        pg.text(status, size.x * 0.95f, size.y * 0.5f);
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
