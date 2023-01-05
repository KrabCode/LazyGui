package lazy.nodes;

import lazy.input.LazyKeyEvent;
import lazy.input.LazyMouseEvent;
import lazy.utils.KeyCodes;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PApplet.*;


class PlotDisplayNode extends AbstractNode {

    private final SliderNode sliderX;
    private final SliderNode sliderY;

    protected PlotDisplayNode(String path, FolderNode parentFolder, SliderNode sliderX, SliderNode sliderY) {
        super(NodeType.TRANSIENT, path, parentFolder);
        this.sliderX = sliderX;
        this.sliderY = sliderY;
        shouldDrawLeftNameText = false;
        //noinspection SuspiciousNameCombination
        idealInlineNodeHeightInCells = parentFolder.idealWindowWidthInCells;
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        pg.noFill();
        drawPlotGrid(pg);
    }

    private void drawPlotGrid(PGraphics pg) {
        pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        if (shouldHighlightGrid()) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        }
        pg.pushMatrix();
        int cellCountX = floor(idealInlineNodeHeightInCells);
        int cellCountY = floor(idealInlineNodeHeightInCells);
        // cell count is kept odd on purpose for the line to always go through rounded numbers and not skip around
        float w = (size.x - 1);
        float h = (size.y - 1);
        float valueChangePerCell = 10;
        float valueChangePerCellX = valueChangePerCell * sliderX.valueFloatPrecision;
        float valueChangePerCellY = valueChangePerCell * sliderY.valueFloatPrecision;
        float valueRangeX = cellCountX * valueChangePerCellX;
        float valueRangeY = cellCountY * valueChangePerCellY;
        float nearValueX = valueChangePerCellX / 2 + sliderX.valueFloat % valueChangePerCellX;
        float nearValueY = valueChangePerCellY / 2 + sliderY.valueFloat % valueChangePerCellY;
        pg.translate(w / 2f + 1, h / 2f);
        float valueStartX = -valueChangePerCellX * 2;
        float valueEndX = valueRangeX + valueChangePerCellX * 2;
        float valueStartY = -valueChangePerCellY * 2;
        float valueEndY = valueRangeY + valueChangePerCellY * 2;

        // draw lines
        for (float valX = valueStartX; valX <= valueEndX; valX += valueChangePerCellX) {
            float x = map(valX - nearValueX, 0, valueRangeX, -w / 2f, w / 2f);
            if (abs(x) <= w / 2f) {
                pg.line(x, -h / 2, x, h / 2);
            }
        }
        for (float valY = valueStartY; valY <= valueEndY; valY += valueChangePerCellY) {
            float y = map(valY - nearValueY, 0, valueRangeY, -h / 2f, h / 2f);
            if (abs(y) <= h / 2f) {
                pg.line(-w / 2, y, w / 2, y);
            }
        }

        pg.translate(0.5f, 0.5f);

        // find zero position on screen
        float zeroSize = min(w,h) * 0.08f;
        float zeroScreenRange = w / 2 - zeroSize / 2;
        float zeroScreenX = constrain(map(-sliderX.valueFloat, -valueRangeX / 2f, valueRangeX / 2f, -w / 2f, w / 2f), -zeroScreenRange, zeroScreenRange);
        float zeroScreenY = constrain(map(-sliderY.valueFloat, -valueRangeY / 2f, valueRangeY / 2f, -h / 2f, h / 2f), -zeroScreenRange, zeroScreenRange);

        // draw zero cross or arrow
        strokeForegroundBasedOnMouseOver(pg);
        pg.strokeWeight(1.99f);
        pg.strokeCap(SQUARE);
        if (abs(zeroScreenX) < zeroScreenRange && abs(zeroScreenY) < zeroScreenRange) {
            // draw zero cross because it was found on screen
            pg.line(zeroScreenX, zeroScreenY - zeroSize / 2,
                    zeroScreenX, zeroScreenY + zeroSize / 2);
            pg.line(zeroScreenX - zeroSize / 2, zeroScreenY,
                    zeroScreenX + zeroSize / 2, zeroScreenY);
        } else {
            // the 0 is outside, so draw an arrow towards it at the border
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
        pg.ellipse(0, 0, zeroSize+1, zeroSize+1);

        pg.popMatrix();
    }

    private boolean shouldHighlightGrid() {
        return isDragged ||
                sliderX.isDragged ||
                sliderY.isDragged ||
                isMouseOverNode ||
                sliderX.isMouseOverNode ||
                sliderY.isMouseOverNode;
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
        if(e.getKeyCode() == KeyCodes.CTRL_C || e.getKeyCode() == KeyCodes.CTRL_V){
            parent.keyPressedOverNode(e, x, y);
        }else{
            sliderX.keyPressedOverNode(e, x, y);
            sliderY.keyPressedOverNode(e, x, y);
        }

    }
}
