package lazy;


import processing.core.PGraphics;
import processing.opengl.PShader;

import static processing.core.PConstants.CORNER;

class ColorPreviewNode extends AbstractNode {

    ColorPickerFolderNode parentColorPickerFolder;
    String checkerboardShaderPath = "checkerboard.glsl";

    ColorPreviewNode(String path, ColorPickerFolderNode parentColorPickerFolder) {
        super(NodeType.TRANSIENT, path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        shouldDrawLeftNameText = false;
        rowHeightInCells = 3;
        InternalShaderStore.getShader(checkerboardShaderPath);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        drawCheckerboard(pg);
        drawColorPreview(pg);
    }

    private void drawCheckerboard(PGraphics pg) {
        PShader checkerboardShader = InternalShaderStore.getShader(checkerboardShaderPath);
        checkerboardShader.set("quadPos", pos.x, pos.y);
        pg.shader(checkerboardShader);
        pg.rectMode(CORNER);
        pg.fill(1);
        pg.noStroke();
        pg.rect(1, 1, size.x - 1, size.y-1);
        pg.resetShader();
    }

    private void drawColorPreview(PGraphics pg) {
        pg.fill(parentColorPickerFolder.getColor().hex);
        pg.noStroke();
        pg.rect(1, 1, size.x - 1, size.y-1);
    }

    @Override
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        parentColorPickerFolder.keyPressedOverNode(e, x, y);
    }
}
