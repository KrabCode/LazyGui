package com.krab.lazy.nodes;


import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import processing.core.PGraphics;

import static com.krab.lazy.stores.GlobalReferences.app;
import static com.krab.lazy.stores.LayoutStore.cell;
import static processing.core.PConstants.CENTER;

public class ButtonNode extends AbstractNode {
    public ButtonNode(String path, FolderNode folder) {
        super(NodeType.TRANSIENT, path, folder);
    }

    boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        boolean mousePressed = app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawRightBackdrop(pg, cell);
        drawRightButton(pg);
    }

    void drawRightButton(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x - cell *0.5f, cell * 0.5f);
        fillBackgroundBasedOnMouseOver(pg);
        pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        pg.rectMode(CENTER);
        float outerButtonSize = cell * 0.6f;
        pg.rect(0,0, outerButtonSize, outerButtonSize);
        pg.stroke(ThemeStore.getColor(isInlineNodeDragged ? ThemeColorType.FOCUS_FOREGROUND : ThemeColorType.NORMAL_FOREGROUND));
        if(isMouseOverNode){
            if (isInlineNodeDragged){
                pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            }else{
                pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            }
        }
        float innerButtonSize = cell * 0.35f;
        pg.rect(0,0, innerButtonSize, innerButtonSize);
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {

    }

    public boolean getBooleanValueAndSetItToFalse() {
        boolean result = valueBoolean;
        valueBoolean = false;
        return result;
    }
}
