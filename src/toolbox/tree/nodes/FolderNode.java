package toolbox.tree.nodes;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import java.util.ArrayList;

import static processing.core.PConstants.CENTER;

public class FolderNode extends Node {
    public ArrayList<Node> children = new ArrayList<>();
    public FolderWindow window;

    public FolderNode(String path, FolderNode parent) {
        super(NodeType.FOLDER, path, parent);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeForegroundBasedOnFocus(pg);
        pg.fill(0);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        float rectSize = cell * 0.5f;
        pg.translate(1,1);
        pg.rectMode(CENTER);
        pg.rect(0,0,rectSize, rectSize);
        pg.translate(-2,-2);
        pg.noFill();
        pg.rect(0,0,rectSize, rectSize);
    }

    @Override
    public void nodePressed(float x, float y) {
        super.nodePressed(x, y);
        WindowManager.uncoverOrCreateWindow(this, new PVector(pos.x + size.x + cell, pos.x + cell));
    }

}
