package toolbox.tree.valueNodes;

import processing.core.PGraphics;
import toolbox.tree.Folder;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

public class ToggleNode extends Node {
    public ToggleNode(String path, NodeType toggle, Folder folder) {
        super(path,toggle,folder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
