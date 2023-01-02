package lazy.nodes;



import lazy.input.LazyKeyEvent;
import processing.core.PGraphics;


class ColorPickerHexNode extends AbstractNode {

    final ColorPickerFolderNode parentColorPickerFolder;

    ColorPickerHexNode(String path, ColorPickerFolderNode parentFolder) {
        super(NodeType.VALUE, path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, parentColorPickerFolder.hexString);
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        parentColorPickerFolder.keyPressedOverNode(e, x, y);
    }

    @Override
    public String getConsolePrintableValue() {
        return parentColorPickerFolder.hexString;
    }
}
