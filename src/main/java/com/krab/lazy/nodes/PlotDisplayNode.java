package com.krab.lazy.nodes;

import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.utils.KeyCodes;
import processing.core.PGraphics;
import processing.core.PVector;

import static com.krab.lazy.stores.GlobalReferences.app;
import static com.krab.lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.*;

/**
 * A node that displays a plot grid and allows to drag the grid around.
 * Only changes and highlights the X and Y parts of the potentially three XYZ sliders, since we can't easily use the third dimension using the 2D grid and 2D mouse input.
 */
class PlotDisplayNode extends AbstractNode {

    private final SliderNode sliderX;
    private final SliderNode sliderY;

    // one timed zoom+fade animation per axis, kicked off whenever that axis's precision changes
    private final PrecisionZoom animX = new PrecisionZoom();
    private final PrecisionZoom animY = new PrecisionZoom();
    private static final float PRECISION_ANIM_DURATION = 250f; // ms
    // how far the dots start spread out (coarsening / zoom out) or compressed (fining / zoom in, = 1/this)
    private static final float ZOOM_AMPLITUDE = 2.5f;
    // dot opacity at the start of a change; rises to full as it settles, so dots fade in instead of popping
    private static final float ALPHA_MIN = 0.15f;
    // same gray as the window-drag guide dots
    private static final int DOT_COLOR = 0xFF7F7F7F;

    protected PlotDisplayNode(String path, FolderNode parentFolder, SliderNode sliderX, SliderNode sliderY) {
        super(NodeType.TRANSIENT, path, parentFolder);
        this.sliderX = sliderX;
        this.sliderY = sliderY;
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        masterInlineNodeHeightInCells = floor(size.x / cell);
        drawPlotGrid(pg);
        if(isMouseOverNode){
            sliderX.isMouseOverNode = true;
            sliderY.isMouseOverNode = true;
        }
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {

    }

    private void drawPlotGrid(PGraphics pg) {
        float w = size.x - 1;
        float h = size.y - 1;

        // pixels of grid movement per unit of value, anchored to the real precision so the grid moves
        // exactly 1px per 1px of pointer drag (a drag moves value by `precision` per pixel) and never drifts
        float pixelsPerValueX = 1f / sliderX.valueFloatPrecision;
        float pixelsPerValueY = 1f / sliderY.valueFloatPrecision;

        // advance each axis's zoom+fade animation for this frame; one progress value drives both zoom and fade
        animX.update(sliderX.valueFloatPrecision);
        animY.update(sliderY.valueFloatPrecision);

        // the zoom scales everything about the center (the cursor); the fade dims the dots mid-change so they
        // fade in instead of popping. Both share the same progress, so scaling and fading happen together.
        float gridAlpha = min(animX.alpha(), animY.alpha());

        // screen offset of the world origin (value 0) relative to the cursor, which sits at the center
        double originX = -sliderX.valueFloat * pixelsPerValueX * animX.zoom();
        double originY = -sliderY.valueFloat * pixelsPerValueY * animY.zoom();

        pg.pushMatrix();
        pg.translate(w / 2f + 1, h / 2f);
        drawDots(pg, w, h, pixelsPerValueX, pixelsPerValueY, animX.zoom(), animY.zoom(), gridAlpha);
        drawOriginAndMarker(pg, w, h, (float) originX, (float) originY);
        pg.popMatrix();
    }

    // Tracks one slider's precision and, whenever it changes, plays a timed zoom+fade that eases back to rest.
    private static class PrecisionZoom {
        private float lastPrecision = Float.NaN;
        private float fromZoom = 1f;
        private int startMillis = 0;
        private float eased = 1f;

        void update(float precision) {
            if (Float.isNaN(lastPrecision)) {
                startMillis = app.millis() - (int) PRECISION_ANIM_DURATION; // start already settled
            } else if (precision != lastPrecision) {
                // finer precision (smaller) = zoom in, start compressed; coarser = zoom out, start spread
                fromZoom = precision < lastPrecision ? 1f / ZOOM_AMPLITUDE : ZOOM_AMPLITUDE;
                startMillis = app.millis();
            }
            lastPrecision = precision;
            float t = constrain((app.millis() - startMillis) / PRECISION_ANIM_DURATION, 0, 1);
            eased = t * t * (3 - 2 * t); // smoothstep
        }

        // >1 spreads the dots (zoom out on coarsening), <1 compresses them (zoom in on fining), easing to 1
        float zoom() {
            return lerp(fromZoom, 1f, eased);
        }

        float alpha() {
            return lerp(ALPHA_MIN, 1f, eased);
        }
    }

    private void drawDots(PGraphics pg, float w, float h,
                          float pixelsPerValueX, float pixelsPerValueY, float zoomX, float zoomY, float alpha) {
        // dot spacing is `cell` at rest; the transient zoom spreads it apart and eases it back
        float spacingX = cell * zoomX;
        float spacingY = cell * zoomY;

        // anchor the lattice to the real value grid (`cell * precision` per dot) so it stays put even at
        // huge values and never drifts; the same zoom that scales the spacing scales the phase about center
        float phaseX = (float) (-valueModPixels(sliderX.valueFloat, cell * sliderX.valueFloatPrecision, pixelsPerValueX)) * zoomX;
        float phaseY = (float) (-valueModPixels(sliderY.valueFloat, cell * sliderY.valueFloatPrecision, pixelsPerValueY)) * zoomY;

        pg.strokeWeight(2.5f);
        pg.strokeCap(ROUND);
        // pack alpha into the ARGB int so it renders correctly regardless of the buffer's color mode
        int dotColor = (round(constrain(alpha, 0, 1) * 255) << 24) | (DOT_COLOR & 0x00FFFFFF);
        // one batched GPU draw call for all dots; stroke is set per vertex because P2D POINTS take the
        // vertex color, not the stroke state set before beginShape()
        pg.beginShape(POINTS);
        for (float x = phaseX - spacingX * ceil((phaseX + w / 2f) / spacingX); x <= w / 2f; x += spacingX) {
            if (abs(x) > w / 2f) continue;
            for (float y = phaseY - spacingY * ceil((phaseY + h / 2f) / spacingY); y <= h / 2f; y += spacingY) {
                if (abs(y) > h / 2f) continue;
                pg.stroke(dotColor);
                pg.vertex(x, y);
            }
        }
        pg.endShape();
    }

    // distance in pixels that `value` sits past the last grid dot below it, kept in value space for precision
    private double valueModPixels(double value, float spacingValue, float pixelsPerValue) {
        double valueMod = value - spacingValue * Math.floor(value / spacingValue);
        return valueMod * pixelsPerValue;
    }

    private void drawOriginAndMarker(PGraphics pg, float w, float h, float originX, float originY) {
        pg.translate(0.5f, 0.5f);
        float zeroSize = min(w, h) * 0.08f;
        float zeroScreenRange = w / 2 - zeroSize / 2;
        float zeroScreenX = constrain(originX, -zeroScreenRange, zeroScreenRange);
        float zeroScreenY = constrain(originY, -zeroScreenRange, zeroScreenRange);

        strokeForegroundBasedOnMouseOver(pg);
        pg.strokeWeight(1.99f);
        pg.strokeCap(SQUARE);
        if (abs(originX) < zeroScreenRange && abs(originY) < zeroScreenRange) {
            // draw zero cross because the origin is on screen
            pg.line(zeroScreenX, zeroScreenY - zeroSize / 2,
                    zeroScreenX, zeroScreenY + zeroSize / 2);
            pg.line(zeroScreenX - zeroSize / 2, zeroScreenY,
                    zeroScreenX + zeroSize / 2, zeroScreenY);
        } else {
            // the origin is outside, so draw an arrow towards it at the border
            float arrowLength = 20;
            float arrowAppendageLength = 6;
            float arrowAppendageAngle = 0.7f;
            PVector toZero = new PVector(zeroScreenX, zeroScreenY);
            PVector arrowBase = toZero.copy().setMag(toZero.mag() - arrowLength);
            pg.line(arrowBase.x, arrowBase.y, zeroScreenX, zeroScreenY);
            PVector appendageHuggingArrowLine = arrowBase.copy().setMag(arrowAppendageLength);
            for (int i = 0; i < 2; i++) {
                pg.pushMatrix();
                pg.translate(zeroScreenX, zeroScreenY);
                float rotateDirectionSign = i % 2 == 0 ? 1 : -1;
                pg.rotate(PI + arrowAppendageAngle * rotateDirectionSign);
                pg.line(0, 0, appendageHuggingArrowLine.x, appendageHuggingArrowLine.y);
                pg.popMatrix();
            }
        }

        // draw selection marker
        pg.noStroke();
        fillForegroundBasedOnMouseOver(pg);
        pg.ellipse(0, 0, zeroSize + 1, zeroSize + 1);
    }

    @Override
    public void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        sliderY.verticalMouseMode = true;
        sliderX.mousePressedOverNode(x, y);
        sliderY.mousePressedOverNode(x, y);
    }

    @Override
    public void mouseDragNodeContinue(LazyMouseEvent e) {
        sliderX.mouseDragNodeContinue(e);
        sliderY.mouseDragNodeContinue(e);
    }


    @Override
    public void mouseReleasedAnywhere(LazyMouseEvent e) {
        super.mouseReleasedAnywhere(e);
        sliderY.verticalMouseMode = false;
    }

    @Override
    public void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x, y, dir);
        sliderX.mouseWheelMovedOverNode(x, y, dir);
        sliderY.mouseWheelMovedOverNode(x, y, dir);
    }

    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if((e.isControlDown() && e.getKeyCode() == KeyCodes.C) || (e.isControlDown() && e.getKeyCode() == KeyCodes.V)){
            if(parent != null){
                parent.keyPressedOverNode(e, x, y);
            }
        }else{
            sliderX.keyPressedOverNode(e, x, y);
            sliderY.keyPressedOverNode(e, x, y);
        }
    }
}
