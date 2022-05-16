package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.NodeType;

import javax.swing.*;

import static processing.core.PApplet.println;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

class StateItemNode extends AbstractNode {
    String filename;
    public StateItemNode(String path, NodeFolder parent, String filename) {
        super(NodeType.VALUE_NODE, path, parent);
        this.filename = filename;
    }

    protected void updateDrawInlineNode(PGraphics pg) {

        pg.rectMode(CENTER);
        PVector buttonCenterPos;
        for (int i = 0; i < 3; i++) {
            float buttonSize = cell * 0.9f;
            buttonCenterPos = new PVector(size.x - cell * i - cell * 0.5f, cell * 0.5f);
            strokeForegroundBasedOnMouseOver(pg);
            pg.noFill();
            pg.rect(buttonCenterPos.x, buttonCenterPos.y, buttonSize, buttonSize);
            pg.textAlign(CENTER,CENTER);
            fillForegroundBasedOnMouseOver(pg);
            if(i == 0){
                pg.text("s", buttonCenterPos.x, buttonCenterPos.y);
            }else if(i == 1){
                pg.text("r", buttonCenterPos.x, buttonCenterPos.y);
            }else if(i == 2){
                pg.text("d", buttonCenterPos.x, buttonCenterPos.y);
            }
        }
    }

    public void nodeClicked(float x, float y) {

        if(saveButtonClicked(x,y)){
            println("save");

        }
        if(renameButtonClicked(x,y)){
            println("rename");
            String newName = JOptionPane.showInputDialog("hello"); // TODO nefunguje v tomhle threadu
            if(newName != null){
                State.renameFile(filename, newName);
            }
        }
        if(deleteButtonClicked(x,y)){
            println("delet");
        }
    }

    private boolean saveButtonClicked(float x, float y) {
        return buttonClickIndex(x,y) == 0;
    }

    private boolean renameButtonClicked(float x, float y) {
        return  buttonClickIndex(x,y) == 1;
    }

    private boolean deleteButtonClicked(float x, float y) {
        return buttonClickIndex(x,y) == 2;
    }

    int buttonClickIndex(float x, float y){
        for (int i = 0; i < 3; i++) {
            if(Utils.isPointInRect(x,y,pos.x + size.x - cell - cell * i, pos.y, cell, cell)){
                return i;
            }
        }
        return -1;
    }

}
