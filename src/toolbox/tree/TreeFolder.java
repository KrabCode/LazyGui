package toolbox.tree;

import java.util.ArrayList;

public class TreeFolder extends TreeNode{
    public final TreeFolder parent;
    public ArrayList<TreeNode> children = new ArrayList<TreeNode>();

    public TreeFolder(String path, TreeFolder parent) {
        super(path, NodeType.FOLDER);
        this.parent = parent;
    }
}
