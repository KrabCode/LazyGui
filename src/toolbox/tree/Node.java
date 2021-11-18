package toolbox.tree;

import processing.core.PVector;

public class Node {
    public final NodeType type;
    public final Folder parent;
    public final String path;
    public final String name;
    public final TreeNodeValue value = new TreeNodeValue();
    public PVector pos = new PVector();
    public PVector size = new PVector();
    public boolean isDragged = false;

    public Node(String path, NodeType type, Folder parentFolder) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
        this.parent = parentFolder;
    }

    private String getNameFromPath(String path) {
        if("".equals(path)){
            return "root";
        }
        String[] split = path.split("/");
        if (split.length == 0) {
            return "";
        }
        return split[split.length - 1];
    }
}
