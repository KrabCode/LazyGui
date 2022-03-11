package toolbox.windows.nodes.colorPicker;

import processing.core.PGraphics;
import toolbox.global.InternalShaderStore;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import static processing.core.PConstants.CORNER;

public class ColorPreviewNode extends AbstractNode {

    ColorPickerFolderNode parentColorPickerFolder;
    String checkerboardShader = "checkerboard.glsl";

    public ColorPreviewNode(String path, ColorPickerFolderNode parentColorPickerFolder) {
        super(NodeType.VALUE_ROW, path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        displayInlineName = false;
        heightMultiplier = 2;
        InternalShaderStore.getShader(checkerboardShader);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
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
