package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

public class ColorPreviewNode extends ValueNode {

    public ColorPreviewNode(String path, FolderNode parentFolder) {
        super(path, parentFolder);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(RIGHT,CENTER);
        pg.text("preview", size.x * 0.95f, size.y * 0.5f);
    }
}
