package lazy;



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
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        parentColorPickerFolder.keyPressedOverNode(e, x, y);
    }

    @Override
    String getPrintableValue() {
        return parentColorPickerFolder.hexString;
    }
}
