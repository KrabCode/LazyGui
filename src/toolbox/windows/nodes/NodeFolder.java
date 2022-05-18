package toolbox.windows.nodes;

import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.palettes.ThemeColorType;
import toolbox.global.palettes.ThemeStore;
import toolbox.windows.FolderWindow;
import toolbox.windows.WindowManager;

import java.util.concurrent.CopyOnWriteArrayList;

import static processing.core.PApplet.println;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

/**
 * A node that opens a new window with child nodes when clicked.
 */
public class NodeFolder extends AbstractNode {

    /**
     * CopyOnWriteArrayList is needed to avoid concurrent modification
     * because the children get drawn by one thread and user input changes the list from another thread
     */
    @Expose
    public CopyOnWriteArrayList<AbstractNode> children = new CopyOnWriteArrayList<>();

    public FolderWindow window;

    protected final float previewRectSize = cell * 0.6f;

    public float idealWindowWidth = State.defaultWindowWidthInPixels;

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
        pg.pushStyle();
        AbstractNode enabledNode = findChildByName("enabled");
        if(enabledNode != null &&
                enabledNode.className.contains("ToggleNode") &&
                ((ToggleNode) enabledNode).valueBoolean){
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }
        pg.rect(0,0,previewRectSize, miniCell); // handle
        pg.popStyle();
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
