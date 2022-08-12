package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.NodeType;


class SaveNode extends AbstractNode {
    String fileName, fullPath;

    public SaveNode(String path, NodeFolder parent, String fileName, String fullPath) {
        super(NodeType.VALUE_NODE, path, parent);
        this.fileName = fileName;
        this.fullPath = fullPath;
    }

    protected void updateDrawInlineNode(PGraphics pg) {

    }


    public void mousePressedOverNode(float x, float y) {
        State.loadStateFromFile(fileName);
    }


}
