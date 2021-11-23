package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.ValueNode;

import static processing.core.PConstants.CORNER;

public class ColorPreviewNode extends ValueNode {

    ColorPickerFolderNode parentColorPickerFolder;

    public ColorPreviewNode(String path, ColorPickerFolderNode parentColorPickerFolder) {
        super(path, parentColorPickerFolder);
        this.parentColorPickerFolder = parentColorPickerFolder;
        displayInlineName = false;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawAlphaCheckerboard(pg);
        pg.fill(parentColorPickerFolder.getColor().hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0,0,size.x,size.y);
        drawRightText(pg, prettifyHexString(parentColorPickerFolder.hex));
    }

    private void drawAlphaCheckerboard(PGraphics pg) {
        // TODO
    }

    String prettifyHexString(int hexCode){
        StringBuilder sb = new StringBuilder(Integer.toHexString(hexCode));
        while(sb.length() < 8){
            sb.append("0");
        }
        return sb.substring(0, 2) +
                " " +
                sb.substring(2, 4) +
                " " +
                sb.substring(4, 6) +
                " " +
                sb.substring(6, 8);
    }
}
