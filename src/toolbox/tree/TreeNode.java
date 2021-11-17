package toolbox.tree;

public class TreeNode {
    public final NodeType type;
    public final TreeFolder parent;
    public final String path;
    public final String name;
    public final TreeNodeValue value = new TreeNodeValue();

    public TreeNode(String path, NodeType type, TreeFolder parentFolder) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
        this.parent = parentFolder;
    }

    private String getNameFromPath(String path) {
        String[] split = path.split("/");
        if (split.length == 0) {
            return "";
        }
        return split[split.length - 1];
    }
}
