package toolbox.tree.nodes;

public abstract class ValueNode extends Node {
    public ValueNode(String path, FolderNode parentFolder) {
        super(NodeType.VALUE, path, parentFolder);
    }
}
