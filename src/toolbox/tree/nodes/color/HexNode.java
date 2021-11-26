package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.Node;
import toolbox.tree.nodes.NodeType;

public class HexNode extends Node {

    ColorPickerFolderNode parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolderNode parentFolder) {
        super(NodeType.DISPLAY, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
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
