package com.krab.lazy.nodes;

import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import processing.core.PGraphics;

import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.*;
import static processing.core.PApplet.map;
import static processing.core.PApplet.radians;

class GradientPreviewNode extends AbstractNode {
    final GradientPickerFolderNode parent;
    final int NULL = -1;
    int hoveredColorIndex = NULL;
    int draggedColorIndex = NULL;

    GradientPreviewNode(String path, GradientPickerFolderNode parent) {
        super(NodeType.TRANSIENT, path, parent);
        this.parent = parent;
        masterInlineNodeHeightInCells = 6;
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        pg.image(parent.getOutputGraphics(), 1, 1, size.x - 1, size.y - 1);
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        int colorRowToHighlight = NULL;
        if(draggedColorIndex != NULL){
            colorRowToHighlight = hoveredColorIndex;
        }
        else if(hoveredColorIndex != NULL){
            colorRowToHighlight = hoveredColorIndex;
        }
        if(colorRowToHighlight != NULL){
            parent.findColorStopByIndex(colorRowToHighlight).isMouseOverNode = true;
        }
        if(isParentWindowVisible() && isMouseOverNode && draggedColorIndex == NULL){
            hoveredColorIndex = findClosestStopOnScreen(app.mouseX, app.mouseY);
        }else{
            // turn off mouseover for only once when setting hovered index to NULL from non-null
            // to allow natural mouseover interaction otherwise
            // weird hacks here and in the PlotFolder - need a better system
            if(hoveredColorIndex != NULL && draggedColorIndex == NULL){
                for (int i = 0; i < parent.colorCount; i++) {
                    parent.findColorStopByIndex(i).isMouseOverNode = false;
                }
            }
            hoveredColorIndex = NULL;
        }
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawColorStops(pg);
    }

    private void drawColorStops(PGraphics pg) {
        pg.textAlign(RIGHT, CENTER);
        pg.textFont(FontStore.getSideFont());
        // draw all the non-highlighted stops first, so they stay in the background
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (!shouldHighlightColorStop(colorStop, i, hoveredColorIndex)) {
                drawColorStop(pg, colorStop, false);
            }
        }
        // draw all the highlighted stops last, so they are put in the foreground
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (shouldHighlightColorStop(colorStop, i, hoveredColorIndex)) {
                drawColorStop(pg, colorStop, true);
            }
        }
    }

    private boolean shouldHighlightColorStop(GradientColorStopNode colorStop, int i, int indexOfColorStopUnderMouse) {
        return colorStop.isPosSliderBeingUsed() || i == indexOfColorStopUnderMouse;
    }

    private void drawColorStop(PGraphics pg, GradientColorStopNode colorStop, boolean highlight) {
        boolean isGradientVertical = parent.isGradientDirectionVertical();
        pg.pushMatrix();
        float side = LayoutStore.cell * 0.5f;
        if (isGradientVertical) {
            float pointerLineY = map(colorStop.getGradientPos(), 0, 1, 0, size.y);
            pg.translate(size.x, pointerLineY);
        } else {
            float pointerLineX = map(colorStop.getGradientPos(), 0, 1, 0, size.x);
            pg.translate(pointerLineX, size.y);
            pg.rotate(HALF_PI);
        }
        pg.translate(-5, 0);
        pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        if (!highlight) {
            pg.noStroke();
        } else {
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }
        drawEquilateralTrianglePointingLeft(pg, side);
        pg.popMatrix();
    }

    private void drawEquilateralTrianglePointingLeft(PGraphics pg, float side) {
        float theta = radians(30);
        pg.beginShape();
        pg.vertex(0, side * sin(theta));
        pg.vertex(-side, 0);
        pg.vertex(0, side * sin(-theta));
        pg.endShape(CLOSE);
    }

    @Override
    public void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        draggedColorIndex = findClosestStopOnScreen(x, y);
        if(draggedColorIndex != NULL){
            GradientColorStopPositionSlider posSlider = parent.findColorStopByIndex(draggedColorIndex).posSlider;
            posSlider.verticalMouseMode = parent.isGradientDirectionVertical();
            posSlider.mousePressedOverNode(x, y);
        }
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        if(draggedColorIndex != NULL){
            parent.findColorStopByIndex(draggedColorIndex).posSlider.mouseDragNodeContinue(e);
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleasedAnywhere(LazyMouseEvent e) {
        super.mouseReleasedAnywhere(e);
        if(draggedColorIndex != NULL){
            parent.findColorStopByIndex(draggedColorIndex).posSlider.mouseReleasedAnywhere(e);
            parent.findColorStopByIndex(draggedColorIndex).posSlider.verticalMouseMode = false;
            hoveredColorIndex = draggedColorIndex; // prevents flickering
            e.setConsumed(true);
        }
        draggedColorIndex = NULL;
    }

    int findClosestStopOnScreen(float x, float y){
        boolean isGradientVertical = parent.isGradientDirectionVertical();
        float queryGradientPos = isGradientVertical ?
                norm(y-pos.y, 0, size.y) :
                norm(x-pos.x, 0, size.x);
        float distanceToClosestStop = 10000;
        int indexOfClosestStop = NULL;
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            float distanceToThisColorStop = abs(colorStop.getGradientPos() - queryGradientPos);
            if(distanceToThisColorStop < distanceToClosestStop){
                indexOfClosestStop = i;
                distanceToClosestStop = distanceToThisColorStop;
            }
        }
        return indexOfClosestStop;
    }
}
