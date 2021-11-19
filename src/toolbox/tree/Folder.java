package toolbox.tree;

import processing.core.PGraphics;
import toolbox.windows.FolderWindow;

import java.util.ArrayList;

public class Folder extends Node {
    public ArrayList<Node> children = new ArrayList<>();
    public FolderWindow window;

    public Folder(String path, Folder parent) {
        super(path, NodeType.FOLDER, parent);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
