package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

public class HexNode extends ColorValueNode {

    public HexNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawRightText(pg, Integer.toHexString(parentColorPickerFolder.color.hex));
    }
}


