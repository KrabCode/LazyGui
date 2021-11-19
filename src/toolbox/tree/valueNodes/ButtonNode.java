package toolbox.tree.valueNodes;

import processing.core.PGraphics;
import toolbox.tree.Folder;
import toolbox.tree.Node;
import toolbox.tree.NodeType;

public class ButtonNode extends Node {
    public ButtonNode(String path, NodeType button, Folder folder) {
        super(path,button,folder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
