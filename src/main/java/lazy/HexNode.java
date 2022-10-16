package lazy;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;


public class HexNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;

    public HexNode(String path, ColorPickerFolder parentFolder) {
        super(NodeType.VALUE, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
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

    @Override
    public String getPrintableValue() {
        return parentColorPickerFolder.hexString;
    }
}
