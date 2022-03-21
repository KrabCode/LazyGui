package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;
import toolbox.global.KeyCodes;

import static processing.core.PApplet.hex;
import static processing.core.PApplet.println;

public class HexNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolder parentFolder) {
        super(NodeType.VALUE_NODE, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, parentColorPickerFolder.hexString);
    }

    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        parentColorPickerFolder.keyPressedOverNode(e, x, y);
    }


    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
