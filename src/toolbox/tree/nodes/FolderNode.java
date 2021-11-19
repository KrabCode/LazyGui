package toolbox.tree.nodes;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.Node;
import toolbox.tree.NodeType;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import java.util.ArrayList;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class FolderNode extends Node {
    public ArrayList<Node> children = new ArrayList<>();
    public FolderWindow window;

    public FolderNode(String path, FolderNode parent) {
        super(path, NodeType.FOLDER, parent);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(CENTER,CENTER);
        pg.text("folder", size.x * 0.5f, size.y * 0.5f);
        if(mouseOver){
            pg.textAlign(RIGHT, CENTER);
            pg.text("open", size.x * 0.95f, size.y * 0.5f);
        }
    }

    @Override
    public void nodePressed(float x, float y) {
        super.nodePressed(x, y);
        WindowManager.uncoverOrAddWindow(new FolderWindow(new PVector(pos.x + size.x + cell, pos.x + cell), new PVector(cell * 8, cell * 3), this, true));
    }

}
