package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.MathUtils;
import toolbox.Palette;
import toolbox.tree.Folder;
import toolbox.tree.Node;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;
import static processing.core.PConstants.*;
import static processing.core.PConstants.CENTER;

// GUI element that lets the user see nodes incl. folders to click on and alter
public class FolderWindow extends Window {
    public final Folder folder;

    public FolderWindow(PVector pos, PVector size, Folder folder, boolean closeable) {
        super(pos, size, folder, closeable);
        this.folder = folder;
        folder.window = this;
    }

    float nodeHeight = cell;

    @Override
    protected void drawContent(PGraphics pg) {
        drawFolder(pg);
    }

    public void drawFolder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
//        pg.translate(0, cell); // translate under title bar
        for (int i = 0; i < folder.children.size(); i++) {
            Node node = folder.children.get(i);
            pg.translate(0, nodeHeight);
            node.updateNodeCoordinates(pg.screenX(0,0), pg.screenY(0,0), size.x, nodeHeight);
            node.drawNode(pg);
        }
        pg.popMatrix();
    }


    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
        if(isPointInsideTitleBar(x,y)){
            return;
        }
        if(isPointInsideContent(x,y)){
            Node clickedNode = tryFindChildNode(x,y);
            if(clickedNode!=null){
                nodePressed(clickedNode);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for(Node node : folder.children){
            node.isDragged = false;
        }
    }

    private void nodePressed(Node clickedNode) {
        switch(clickedNode.type){
            case FOLDER:
                WindowManager.uncoverOrAddWindow(new FolderWindow(new PVector(pos.x + size.x + cell, pos.x + cell), new PVector(cell * 8, cell * 8), (Folder) clickedNode, true));
                break;
            case TOGGLE:
            case BUTTON:
            case SLIDER_INT_X:
            case SLIDER_X:
                clickedNode.isDragged = true;
                break;
            case PLOT_XY:
            case GRADIENT_PICKER:
            case COLOR_PICKER:
            case PLOT_XYZ:
                break;
        }
    }

    private Node tryFindChildNode(float x, float y) {
        for (Node node : folder.children) {
            if(MathUtils.isPointInRect(x,y,node.pos.x,node.pos.y, node.size.x, node.size.y)){
                return node;
            }
        }
        return null;
    }
}
