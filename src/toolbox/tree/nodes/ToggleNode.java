package toolbox.tree.nodes;

import processing.core.PGraphics;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

public class ToggleNode extends Node {
    public ToggleNode(String path, NodeType toggle, FolderNode folder) {
        super(path,toggle,folder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
