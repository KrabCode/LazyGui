package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

public class SaturationNode extends ValueNode {


    public SaturationNode(String path, FolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
