package toolbox.tree.nodes.color;

import processing.core.PGraphics;

import static processing.core.PConstants.CENTER;

public class ColorPreviewNode extends ColorValueNode {


    public ColorPreviewNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.fill(parentColorPickerFolder.color.hex);
        strokeContentBasedOnFocus(pg);
        float rectSize = size.y * 0.5f;
        float margin = cell * 0.3f;
        pg.rectMode(CENTER);
        pg.rect(size.x * 0.75f, size.y * 0.5f, size.x * 0.5f - margin, rectSize);
    }
}
