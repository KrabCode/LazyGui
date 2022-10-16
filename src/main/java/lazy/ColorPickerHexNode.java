package lazy;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;


class ColorPickerHexNode extends AbstractNode {

    ColorPickerFolder parentColorPickerFolder;

    ColorPickerHexNode(String path, ColorPickerFolder parentFolder) {
        super(NodeType.VALUE, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, parentColorPickerFolder.hexString);
    }

    @Override
    void keyPressedOverNode(KeyEvent e, float x, float y) {
        parentColorPickerFolder.keyPressedOverNode(e, x, y);
    }


    @Override
    void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }

    @Override
    String getPrintableValue() {
        return parentColorPickerFolder.hexString;
    }
}
