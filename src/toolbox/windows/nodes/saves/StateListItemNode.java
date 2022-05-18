package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.global.palettes.ThemeColorType;
import toolbox.global.palettes.ThemeStore;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.NodeType;


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
        for (int i = 0; i < 4; i++) {
            boolean isMouseOverButton = i == mouseOverButtonIndex(State.app.mouseX, State.app.mouseY);
            float buttonSize = cell;
            buttonCenterPos = new PVector(size.x - cell * i - cell * 0.5f, cell * 0.5f);
            pg.noStroke();
            pg.fill(isMouseOverButton? ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND) : ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.rect(buttonCenterPos.x, buttonCenterPos.y, buttonSize, buttonSize);
            pg.textAlign(CENTER,CENTER);
            pg.fill(isMouseOverButton? ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND) : ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            String textContent;
            if(i == 0){
                textContent = "S";
            }else if(i == 1){
                textContent = "R";
            }else if(i == 2){
                textContent = "D";
            }else {
                textContent = "L";
            }
            pg.text(textContent, buttonCenterPos.x, buttonCenterPos.y);
        }
    }

    public void nodeClicked(float x, float y) {
        if(saveButtonClicked(x,y)){
            State.overwriteFileWithCurrentState(fullPath);
        } else if(renameButtonClicked(x,y)) {
            String newName = Utils.dialogInput("Rename save \"" + name + "\" to:", "Enter new save name");
            if(newName != null && newName.length() > 0){
                State.renameFile(fileName, newName);
            }
        } else if(deleteButtonClicked(x,y)){
            if(Utils.dialogConfirm("Do you really want to delete \"" + name + "\"?", "Delete confirmation")){
                State.deleteFile(fileName);
            }
        } else if(loadButtonClicked(x,y)){
            State.loadStateFromFile(fileName);
        }
    }

    private boolean saveButtonClicked(float x, float y) {
        return mouseOverButtonIndex(x,y) == 0;
    }

    private boolean renameButtonClicked(float x, float y) {
        return  mouseOverButtonIndex(x,y) == 1;
    }

    private boolean deleteButtonClicked(float x, float y) {
        return mouseOverButtonIndex(x,y) == 2;
    }

    private boolean loadButtonClicked(float x, float y) {
        return mouseOverButtonIndex(x,y) == 3;
    }

    int mouseOverButtonIndex(float x, float y){
        for (int i = 0; i < 4; i++) {
            if(Utils.isPointInRect(x,y,pos.x + size.x - cell - cell * i, pos.y, cell, cell)){
                return i;
            }
        }
        return -1;
    }

}
