package lazy.windows.nodes.colorPicker;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import lazy.InternalShaderStore;
import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.NodeType;

import static processing.core.PConstants.CORNER;

public class ColorPreviewNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;
    String checkerboardShader = "checkerboard.glsl";

    public ColorPreviewNode(String path, ColorPickerFolder parentColorPickerFolder) {
        super(NodeType.TRANSIENT, path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        displayInlineName = false;
        rowHeightInCells = 3;
        InternalShaderStore.getShader(checkerboardShader);
    }

    @Override
    protected void updateDrawInlineNodeInner(PGraphics pg) {
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

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
