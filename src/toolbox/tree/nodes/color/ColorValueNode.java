package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.ValueNode;

public class ColorValueNode extends ValueNode {
    public ColorPickerFolderNode parentColorPickerFolder;
    public int colorValueDigitsAfterDot = 3;
    public ColorValueNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {

    }
}
