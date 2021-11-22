package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PConstants.CENTER;

public class ColorPreviewNode extends ValueNode {

    ColorPickerFolderNode parentColorPickerFolder;

    public ColorPreviewNode(String path, ColorPickerFolderNode parentColorPickerFolder) {
        super(path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.fill(parentColorPickerFolder.getColor().hex);
        strokeContentBasedOnFocus(pg);
        float rectSize = size.y * 0.5f;
        float margin = cell * 0.3f;
        pg.rectMode(CENTER);
        pg.rect(size.x * 0.75f, size.y * 0.5f, size.x * 0.5f - margin, rectSize);
    }
}
