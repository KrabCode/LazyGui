package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.tree.NodeType;
import toolbox.tree.nodes.FolderNode;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RGB;

public class ColorPickerNode extends FolderNode {

    public Color color;

    public ColorPickerNode(String path, FolderNode parentFolder) {
        super(NodeType.FOLDER, path, parentFolder);
        color = new Color();
//        children.add();
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeContentBasedOnFocus(pg);
        float previewRectSize = cell * 0.5f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.noFill();
        pg.translate(1, 1);
        pg.rect(0, 0, previewRectSize, previewRectSize);
        pg.fill(color.hex);
        pg.translate(-2, -2);
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    public Color getColor() {
        return color;
    }

    public void initWithRGB(float r, float g, float b) {
        color.r = r;
        color.g = g;
        color.b = b;
        buildColorFromRGB();
    }

    void buildColorFromRGB(){
        PGraphics colorProvider = GlobalState.colorProvider;
        colorProvider.colorMode(RGB,1,1,1,1);
        int hex = colorProvider.color(color.r, color.g, color.b);
        color.hex = hex;
        color.hue = colorProvider.hue(hex);
        color.sat = colorProvider.saturation(hex);
        color.br = colorProvider.brightness(hex);
        color.alpha = 1;
    }
}
