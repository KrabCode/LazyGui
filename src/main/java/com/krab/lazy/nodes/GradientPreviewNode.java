package com.krab.lazy.nodes;

import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.stores.NormColorStore;
import processing.core.PGraphics;

import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.*;
import static processing.core.PApplet.map;
import static processing.core.PApplet.radians;

class GradientPreviewNode extends AbstractNode {
    final GradientPickerFolderNode parent;
    final int NULL = -1;
    int draggedColorStopIndex = NULL;

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
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawColorStops(pg);
    }

    private void drawColorStops(PGraphics pg) {
        pg.textAlign(RIGHT, CENTER);
        pg.textFont(FontStore.getSideFont());
        int indexOfColorStopUnderMouse = NULL;
        if(isMouseOverNode && draggedColorStopIndex == NULL){
             indexOfColorStopUnderMouse = findClosestStopByScreenCoordinate(app.mouseX, app.mouseY);
       }
        // draw all the non-highlighted stops first, so they stay in the background
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (colorStop == null || colorStop.isPosSliderBeingUsed() || i == indexOfColorStopUnderMouse) {
                continue;
            }
            drawColorStop(pg, colorStop, false);
        }
        // draw all the non-highlighted stops first, so they stay in the background
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (colorStop != null && (colorStop.isPosSliderBeingUsed() || i == indexOfColorStopUnderMouse)) {
                drawColorStop(pg, colorStop, true);
            }
        }
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
        if (!highlight) {
            pg.noStroke();
            fillBackgroundBasedOnMouseOver(pg);
        } else {
            pg.stroke(NormColorStore.color(1));
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
        draggedColorStopIndex = findClosestStopByScreenCoordinate(x, y);
        if(draggedColorStopIndex != NULL){
            GradientColorStopPositionSlider posSlider = parent.findColorStopByIndex(draggedColorStopIndex).posSlider;
            posSlider.verticalMouseMode = parent.isGradientDirectionVertical();
            posSlider.mousePressedOverNode(x, y);
        }
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        if(draggedColorStopIndex != NULL){
            parent.findColorStopByIndex(draggedColorStopIndex).posSlider.mouseDragNodeContinue(e);
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleasedAnywhere(LazyMouseEvent e) {
        super.mouseReleasedAnywhere(e);
        if(draggedColorStopIndex != NULL){
            parent.findColorStopByIndex(draggedColorStopIndex).posSlider.mouseReleasedAnywhere(e);
            parent.findColorStopByIndex(draggedColorStopIndex).posSlider.verticalMouseMode = false;
        }
        draggedColorStopIndex = NULL;
    }

    int findClosestStopByScreenCoordinate(float x, float y){
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
