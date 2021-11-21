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
import static toolbox.GlobalState.cell;

public class FolderNode extends Node {
    public ArrayList<Node> children = new ArrayList<>();
    public FolderWindow window;

    public FolderNode(String path, FolderNode parent) {
        super(path, NodeType.FOLDER, parent);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeContentBasedOnFocus(pg);
        pg.noFill();

        pg.pushMatrix();
        pg.translate(size.x * 0.95f, size.y * 0.5f);
        float rectSize = size.y * 0.25f;
        pg.translate(-1,-1);
        pg.rect(-rectSize*0.5f,-rectSize*0.5f,rectSize, rectSize);
        pg.translate(2,2);
        pg.rect(-rectSize*0.5f,-rectSize*0.5f,rectSize, rectSize);
        pg.popMatrix();
    }

    @Override
    public void nodePressed(float x, float y) {
        super.nodePressed(x, y);
        WindowManager.uncoverOrCreateWindow(this, new PVector(pos.x + size.x + cell, pos.x + cell));
    }

}
