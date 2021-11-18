package toolbox.tree;

import java.util.ArrayList;

public class Folder extends Node {
    public ArrayList<Node> children = new ArrayList<>();

    public Folder(String path, Folder parent) {
        super(path, NodeType.FOLDER, parent);
    }
}
