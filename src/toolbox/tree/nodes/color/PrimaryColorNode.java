package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

public class PrimaryColorNode extends ValueNode {

    private final PrimaryColor primaryColor;

    public PrimaryColorNode(String path, FolderNode parentFolder, PrimaryColor primaryColor)
    {
        super(path, parentFolder);
        this.primaryColor = primaryColor;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        switch(primaryColor){
            case RED:
                pg.fill(1,1,0.3f);
                break;
            case GREEN:
                pg.fill(0.35f,1,0.3f);
                break;
            case BLUE:
                pg.fill(0.6f,1,0.3f);
                break;
        }
        pg.rect(0,0,size.x,size.y);
    }
}
