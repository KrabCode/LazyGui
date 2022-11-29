package lazy;

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
        rowHeightInCells = parentFolder.idealWindowWidthInCells;
    }

    private void drawPlotGrid(PGraphics pg) {
        pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        if (shouldHighlightGrid()) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        }
        pg.pushMatrix();
        int cellCountX = floor(1 + 2 * sliderX.currentPrecisionIndex);
        int cellCountY = floor(1 + 2 * sliderY.currentPrecisionIndex);
        // cell count is kept odd on purpose for the line to always go through rounded numbers and not skip around
        float w = (size.x);
        float h = (size.y);
        float valueChangePerCell = 100;
        float valueChangePerCellX = valueChangePerCell * sliderX.valueFloatPrecision;
        float valueChangePerCellY = valueChangePerCell * sliderY.valueFloatPrecision;
        float valueRangeX = cellCountX * valueChangePerCellX;
        float valueRangeY = cellCountY * valueChangePerCellY;
        float nearValueX = valueChangePerCellX / 2 + sliderX.valueFloat % valueChangePerCellX;
        float nearValueY = valueChangePerCellY / 2 + sliderY.valueFloat % valueChangePerCellY;
        pg.translate(w / 2f, h / 2f);
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

        float zeroW = w * 0.035f;
        float zeroH = zeroW * 1.8f;

        // draw selection marker
        strokeForegroundBasedOnMouseOver(pg);
        pg.strokeWeight(max(zeroW, zeroH) + 3);
        pg.point(0.5f, 0.5f); // 0.5f is needed to center the point... for some reason

        // draw zero
        float zeroInnerLineLength = zeroH * 0.2f;
        float zeroScreenRangeX = w / 2 - zeroW / 2;
        float zeroScreenRangeY = h / 2 - zeroH / 2;
        float zeroScreenX = constrain(map(-sliderX.valueFloat, -valueRangeX / 2f, valueRangeX / 2f, -w / 2f, w / 2f), -zeroScreenRangeX, zeroScreenRangeX);
        float zeroScreenY = constrain(map(-sliderY.valueFloat, -valueRangeY / 2f, valueRangeY / 2f, -h / 2f, h / 2f), -zeroScreenRangeY, zeroScreenRangeY);
        pg.noFill();
        pg.strokeWeight(1.99f);
        strokeForegroundBasedOnMouseOver(pg);
        if (abs(zeroScreenX) >= zeroScreenRangeX || abs(zeroScreenY) >= zeroScreenRangeY) {
            // draw arrow towards 0
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
        } else {
            // draw rounded rectangle 0
            pg.rectMode(CENTER);
            pg.rect(zeroScreenX, zeroScreenY, zeroW, zeroH, zeroW / 2f);
            pg.line(zeroScreenX, zeroScreenY - zeroInnerLineLength / 2,
                    zeroScreenX, zeroScreenY + zeroInnerLineLength / 2);
        }
        pg.popMatrix();
    }

    private boolean shouldHighlightGrid() {
        return !(isDragged ||
                sliderX.isDragged ||
                sliderY.isDragged ||
                isMouseOverNode ||
                sliderX.isMouseOverNode ||
                sliderY.isMouseOverNode);
    }


    @Override
    void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        sliderY.verticalMouseMode = true;
        sliderX.disableShader();
        sliderY.disableShader();
        sliderX.mousePressedOverNode(x, y);
        sliderY.mousePressedOverNode(x, y);
    }

    @Override
    void mouseDragNodeContinue(LazyMouseEvent e) {
        sliderX.mouseDragNodeContinue(e);
        sliderY.mouseDragNodeContinue(e);
    }

    @Override
    void mouseReleasedAnywhere(LazyMouseEvent e) {
        super.mouseReleasedAnywhere(e);
        sliderY.verticalMouseMode = false;
        sliderX.enableShader();
        sliderY.enableShader();
    }

    @Override
    void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x, y, dir);
        sliderX.mouseWheelMovedOverNode(x, y, dir);
        sliderY.mouseWheelMovedOverNode(x, y, dir);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        pg.noFill();
        drawPlotGrid(pg);
    }

    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        sliderX.keyPressedOverNode(e, x, y);
        sliderY.keyPressedOverNode(e, x, y);
    }
}
