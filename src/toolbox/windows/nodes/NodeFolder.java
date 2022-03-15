package toolbox.windows.nodes;

import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import java.util.concurrent.CopyOnWriteArrayList;

import static processing.core.PApplet.println;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

public class NodeFolder extends AbstractNode {

    /**
     * CopyOnWriteArrayList is needed to avoid concurrent modification
     * because the children get drawn by one thread and user input changes the list from another thread
     */
    @Expose
    public CopyOnWriteArrayList<AbstractNode> children = new CopyOnWriteArrayList<>();

    public FolderWindow window;

    protected final float previewRectSize = cell * 0.6f;

    // TODO don't allow children to be added to special folders that need the children to be well defined
    //  OR use a second arraylist in children and allow anything to be added anywhere
    public boolean allowAddingChildren = true;

    public NodeFolder(String path, NodeFolder parent) {
        super(NodeType.FOLDER, path, parent);
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawMiniatureWindowIcon(pg);
    }

    private void drawMiniatureWindowIcon(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);
        fillBackgroundBasedOnMouseOver(pg);
        float previewRectSize = cell * 0.6f;
        float miniCell = cell * 0.18f;
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        pg.rectMode(CENTER);
        pg.rect(0, 0, previewRectSize, previewRectSize); // window border
        pg.rectMode(CORNER);
        pg.translate(-previewRectSize*0.5f, -previewRectSize*0.5f);
        pg.rect(0,0,previewRectSize, miniCell); // handle
        pg.rect(previewRectSize-miniCell, 0, miniCell, miniCell); // close button
    }

    @Override
    public void nodeClicked(float x, float y) {
        super.nodeClicked(x, y);
        WindowManager.uncoverOrCreateWindow(this);
        this.isDragged = false;

    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }

    protected AbstractNode findChildByName(String name){
        for(AbstractNode node : children){
            if(node.name.equals(name)){
                return node;
            }
        }
        return null;
    }
}
