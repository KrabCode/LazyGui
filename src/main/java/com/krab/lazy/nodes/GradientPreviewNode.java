package com.krab.lazy.nodes;

import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.stores.NormColorStore;
import processing.core.PGraphics;

import static processing.core.PApplet.*;
import static processing.core.PApplet.map;
import static processing.core.PApplet.radians;

class GradientPreviewNode extends AbstractNode {
    final GradientPickerFolderNode parent;

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
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (colorStop == null || colorStop.isPosSliderBeingUsed()) {
                continue;
            }
            drawColorStop(pg, colorStop, false);
        }
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if (colorStop != null && colorStop.isPosSliderBeingUsed()) {
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
}
