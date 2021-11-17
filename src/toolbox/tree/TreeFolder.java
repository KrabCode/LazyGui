package toolbox.tree;

import java.util.ArrayList;

public class TreeFolder extends TreeNode{
    public ArrayList<TreeNode> children = new ArrayList<>();

    public TreeFolder(String path, TreeFolder parent) {
        super(path, NodeType.FOLDER, parent);
    }
}
