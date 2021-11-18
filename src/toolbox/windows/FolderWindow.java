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
    }

    float nodeHeight = cell;

    @Override
    protected void drawContent(PGraphics pg) {
        drawFolder(pg);
    }

    public void drawFolder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.translate(0, cell); // translate under title bar
        for (int i = 0; i < folder.children.size(); i++) {
            Node node = folder.children.get(i);
            pg.translate(0, nodeHeight);
            updateNodeCoordinates(node, pg.screenX(0,0), pg.screenY(0,0));
            drawNode(pg, node);
        }
        pg.popMatrix();
    }

    private void updateNodeCoordinates(Node node, float x, float y) {
        node.pos.x = x;
        node.pos.y = y;
        node.size.x = size.x;
        node.size.y = nodeHeight;
    }

    float textX = 5;
    float textY = -3;

    private void drawNode(PGraphics pg, Node node) {
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, BOTTOM);
        pg.text(node.name, textX,  textY);
        pg.stroke(0.3f);
        pg.line(0,0, size.x, 0);
        switch(node.type){
            case SLIDER_X:
            case SLIDER_INT_X:
                updateDrawSliderNode(pg, node);
                break;
            default:
                break;
        }
    }

    void updateDrawSliderNode(PGraphics pg, Node node){
        String text = node.getFloatValueToDisplay();
        pg.text(text, 0 + node.size.x * 0.5f, 0 + textY);
        if (node.isDragged) {
            float x = GlobalState.app.mouseX;
            float px = GlobalState.app.pmouseX;
            node.valueFloat += (x - px) * node.valueFloatPrecision;
            pg.noStroke();
            pg.fill(Palette.draggedContentFill);
            pg.rect(0, 0, node.size.x, node.size.y);
            pg.fill(Palette.selectedTextFill);
//            pg.textAlign(RIGHT, BOTTOM);
            pg.text(node.getPrecisionToDisplay(), 0 + node.size.x * 0.9f, 0 + textY);
        }
    }

    protected void validateValue() {
        if (node.valueFloatConstrained) {
            node.valueFloat = constrain(node.valueFloat, node.valueFloatMin, node.valueFloatMax);
        }
    }

    protected void validatePrecision() {

    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
        if(isPointInsideTitleBar(x,y)){
            return;
        }
        if(MathUtils.isPointInRect(x,y,pos.x,pos.y + titleBarHeight,size.x, size.y - titleBarHeight)){
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
