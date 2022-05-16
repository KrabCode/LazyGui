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

class StateListItemNode extends AbstractNode {
    String fileName, fullPath;

    public StateListItemNode(String path, NodeFolder parent, String fileName, String fullPath) {
        super(NodeType.VALUE_NODE, path, parent);
        this.fileName = fileName;
        this.fullPath = fullPath;
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        drawControlButtons(pg);
    }

    private void drawControlButtons(PGraphics pg) {
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
            State.overwriteFileWithCurrentState(fullPath);
            println("saved");
        } else
        if(renameButtonClicked(x,y)) {
            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);
            String newName = JOptionPane.showInputDialog(frame, "rename file:");
            if(newName != null){
                State.renameFile(fileName, newName);
            }
        } else
        if(deleteButtonClicked(x,y)){
            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);
            int dialogResult = JOptionPane.showConfirmDialog(frame, "really truly delete file?", "deleting file", JOptionPane.YES_NO_OPTION);
            if(dialogResult == 0){
                State.deleteFile(fileName);
            }
        } else{
            State.loadStateFromFile(fileName);
        }
    }

    public void rename(){
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
