package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;
import toolbox.global.KeyCodes;

import static processing.core.PApplet.println;

public class HexNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolder parentFolder) {
        super(NodeType.VALUE_ROW, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, parentColorPickerFolder.hexString);
    }

    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_C) {
            State.clipboardHex = parentColorPickerFolder.getHex();
        }
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            parentColorPickerFolder.setHex(State.clipboardHex);
            parentColorPickerFolder.loadValuesFromHex(false);
        }
    }
}
