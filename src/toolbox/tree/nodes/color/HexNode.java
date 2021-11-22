package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.ValueNode;

public class HexNode extends ValueNode {

    final ColorPickerFolderNode parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
        parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        String value = Integer.toHexString(parentColorPickerFolder.hex);
        if(value.equals("0")){
            value = "000000";
        }
        drawRightText(pg, "#" + value);
    }
}


