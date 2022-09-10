package lazy.windows.nodes.saves;

import processing.core.PGraphics;
import lazy.global.State;
import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.NodeType;
import processing.core.PImage;

import static processing.core.PConstants.CORNER;


class SaveNode extends AbstractNode {
    String fileName, fullPath;
    PImage preview;

    public SaveNode(String path, NodeFolder parent, String fileName, String fullPath) {
        super(NodeType.TRANSIENT, path, parent);
        this.fileName = fileName;
        this.fullPath = fullPath;

        // TODO remove this if-statement after solving how to do autosave screenshots
        if(!fullPath.endsWith("\\auto.json")){
            preview = State.app.loadImage(fullPath.replaceAll(".json", ".jpg"));
        }
    }

    protected void updateDrawInlineNodeInner(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, "load");
    }

    public void mousePressedOverNode(float x, float y) {
        State.loadStateFromFile(fileName);
    }

    @Override
    public void drawTooltipAbsolutePos(PGraphics pg, int mouseX, int mouseY) {
        if(preview != null){
            pg.translate(mouseX,mouseY);
            pg.imageMode(CORNER);
            pg.tint(State.normalizedColorProvider.color(1,0.75f));
            pg.image(preview, 0,0,preview.width * 0.1f, preview.height*0.1f);
        }
    }
}
