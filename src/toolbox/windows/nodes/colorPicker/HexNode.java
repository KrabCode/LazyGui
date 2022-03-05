package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import static processing.core.PApplet.println;
import static toolbox.global.KeyCodes.KEY_CODE_CTRL_C;
import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class HexNode extends AbstractNode {

    ColorPickerFolderNode parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolderNode parentFolder) {
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
        if(e.getKeyCode() == KEY_CODE_CTRL_C) {
            State.clipboardHex = parentColorPickerFolder.getHex();
        }
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.setHex(State.clipboardHex);
            parentColorPickerFolder.loadValuesFromHex(false);
        }
    }
}
