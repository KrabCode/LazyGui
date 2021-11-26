package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.global.ShaderStore;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PConstants.CORNER;

public class ColorPreviewNode extends ValueNode {

    ColorPickerFolderNode parentColorPickerFolder;
    String checkerboardShader = "checkerboard.glsl";

    public ColorPreviewNode(String path, ColorPickerFolderNode parentColorPickerFolder) {
        super(path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        displayInlineName = false;
        rowCount = 2;
        ShaderStore.lazyInitGetShader(checkerboardShader);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawCheckerboard(pg);
        drawColorPreview(pg);
    }

    private void drawCheckerboard(PGraphics pg) {
        ShaderStore.hotShader(checkerboardShader, pg);
        pg.rectMode(CORNER);
        pg.fill(1);
        pg.noStroke();
        pg.rect(0,0,size.x,size.y);
        pg.resetShader();
    }

    private void drawColorPreview(PGraphics pg) {
        pg.fill(parentColorPickerFolder.getColor().hex);
        pg.noStroke();
        pg.rect(0,0,size.x,size.y);
    }
}
