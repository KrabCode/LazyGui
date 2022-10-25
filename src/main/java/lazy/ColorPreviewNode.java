package lazy;


import processing.core.PGraphics;

import static processing.core.PConstants.CORNER;

class ColorPreviewNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;
    String checkerboardShader = "checkerboard.glsl";

    ColorPreviewNode(String path, ColorPickerFolder parentColorPickerFolder) {
        super(NodeType.TRANSIENT, path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        displayInlineName = false;
        rowHeightInCells = 3;
        InternalShaderStore.getShader(checkerboardShader);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawCheckerboard(pg);
        drawColorPreview(pg);
    }

    private void drawCheckerboard(PGraphics pg) {
        InternalShaderStore.getShader(checkerboardShader).set("quadPos", pos.x, pos.y);
        InternalShaderStore.shader(checkerboardShader, pg);
        pg.rectMode(CORNER);
        pg.fill(1);
        pg.noStroke();
        pg.rect(0, 0, size.x - 1, size.y);
        pg.resetShader();
    }

    private void drawColorPreview(PGraphics pg) {
        pg.fill(parentColorPickerFolder.getColor().hex);
        pg.noStroke();
        pg.rect(0, 0, size.x, size.y);
    }
}
