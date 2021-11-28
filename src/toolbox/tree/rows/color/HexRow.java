package toolbox.tree.rows.color;

import processing.core.PGraphics;
import toolbox.tree.rows.Row;
import toolbox.tree.rows.RowType;

public class HexRow extends Row {

    ColorPickerFolderRow parentColorPickerFolder;

    public HexRow(String path, ColorPickerFolderRow parentFolder) {
        super(RowType.DISPLAY, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, prettifyHexString(parentColorPickerFolder.hex));
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
