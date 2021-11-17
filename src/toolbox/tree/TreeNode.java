package toolbox.tree;

import processing.core.PVector;
import toolbox.GlobalState;

public class TreeNode {
    public final NodeType type;
    public final String path;
    public final String name;
    public final TreeNodeValue value = new TreeNodeValue();


    public TreeNode(String path, NodeType type) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
    }

    private String getNameFromPath(String path) {
        String[] split = path.split("/");
        if (split.length == 0) {
            return "";
        }
        return split[split.length - 1];
    }
}
