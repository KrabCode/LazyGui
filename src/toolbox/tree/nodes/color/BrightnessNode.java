package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

public class BrightnessNode extends ValueNode {

    public BrightnessNode(String path, FolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
