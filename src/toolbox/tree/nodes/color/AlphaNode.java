package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PApplet.nf;

public class AlphaNode extends ColorValueNode {

    public AlphaNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawRightText(pg, nf(parentColorPickerFolder.color.alpha, 0, colorValueDigitsAfterDot));
    }
}
