package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PApplet.nf;

public class HueNode extends ColorValueNode {
    public HueNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawRightText(pg, nf(parentColorPickerFolder.color.hue, 0, colorValueDigitsAfterDot));
    }
}
