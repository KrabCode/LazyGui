package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import static processing.core.PApplet.println;
import static toolbox.global.KeyCodes.KEY_CODE_CTRL_C;
import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class HexNode extends AbstractNode {

    ColorPickerFolderNode parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolderNode parentFolder) {
        super(NodeType.NODE, path, parentFolder);
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

    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        if(e.getKeyCode() == KEY_CODE_CTRL_C) {
            State.clipboardHex = parentColorPickerFolder.hex;
        }

        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.hex = State.clipboardHex;
            parentColorPickerFolder.loadValuesFromHex(false);
        }
    }
}
