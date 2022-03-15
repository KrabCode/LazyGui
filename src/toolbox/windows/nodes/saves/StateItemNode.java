package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.NodeType;

class StateItemNode extends AbstractNode {
    String filename;
    public StateItemNode(String path, NodeFolder parent, String filename) {
        super(NodeType.VALUE_ROW, path, parent);
        this.filename = filename;
    }

    protected void updateDrawInlineNode(PGraphics pg) {

    }

    public void nodeClicked(float x, float y) {
        State.loadStateFromFile(filename);
    }

}
