package toolbox.tree;

import java.util.ArrayList;

public class TreeNode {
    private final String path;
    ArrayList<TreeNode> children = new ArrayList<TreeNode>();

    public TreeNode(String path) {
        this.path = path;
    }
}
