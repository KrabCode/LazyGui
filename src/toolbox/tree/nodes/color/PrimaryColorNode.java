package toolbox.tree.nodes.color;

import processing.core.PGraphics;

import static processing.core.PApplet.nf;

public class PrimaryColorNode extends ColorValueNode {

    private final PrimaryColorType primaryColorType;

    public PrimaryColorNode(String path, ColorPickerFolderNode parentFolder, PrimaryColorType primaryColorType)
    {
        super(path, parentFolder);
        this.primaryColorType = primaryColorType;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        float value = 0;
        switch(primaryColorType){
            case RED:
                pg.fill(1,0.3f,0.3f);
                value = parentColorPickerFolder.color.r;
                break;
            case GREEN:
                pg.fill(0.35f,0.3f,0.3f);
                value = parentColorPickerFolder.color.g;
                break;
            case BLUE:
                pg.fill(0.6f,0.3f,0.3f);
                value = parentColorPickerFolder.color.b;
                break;
        }

        pg.rect(0,0,size.x,size.y);
        drawRightText(pg, nf(value, 0, colorValueDigitsAfterDot));
    }
}
