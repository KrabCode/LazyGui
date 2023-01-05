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
    protected void drawNodeBackground(PGraphics pg) {

    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        super.drawNodeForeground(pg, name);
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, parentColorPickerFolder.hexString, false);
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
