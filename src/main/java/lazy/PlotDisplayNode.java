package lazy;

import processing.core.PGraphics;

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
        if(!isDragged &&
            !sliderX.isDragged &&
            !sliderY.isDragged &&
            !isMouseOverNode &&
            !sliderX.isMouseOverNode &&
            !sliderY.isMouseOverNode){
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        }
        pg.pushMatrix();
        int cellCountX = floor(1 + 2 * sliderX.currentPrecisionIndex);
        int cellCountY = floor(1 + 2 * sliderY.currentPrecisionIndex);
        // cell count is kept odd on purpose for the line to always go through rounded numbers and not skip around
        float w = (size.x - 1);
        float h = (size.y - 1);
        float valueChangePerCell = 100;
        float valueChangePerCellX = valueChangePerCell * sliderX.valueFloatPrecision;
        float valueChangePerCellY = valueChangePerCell * sliderY.valueFloatPrecision;
        float valueRangeX = cellCountX * valueChangePerCellX;
        float valueRangeY = cellCountY * valueChangePerCellY;
        float nearValueX = valueChangePerCellX / 2 + sliderX.valueFloat % valueChangePerCellX;
        float nearValueY = valueChangePerCellY / 2 + sliderY.valueFloat % valueChangePerCellY;
        pg.translate(w / 2f, h / 2f);
        for (float valX = -valueChangePerCellX * 2; valX <= valueRangeX + valueChangePerCellX * 2; valX += valueChangePerCellX) {
            float x = map(valX - nearValueX, 0, valueRangeX, -w / 2f, w / 2f);
            if (abs(x) <= w / 2f) {
                pg.line(x, -h / 2, x, h / 2);
            }
        }
        for (float valY = -valueChangePerCellY * 2; valY <= valueRangeY + valueChangePerCell * 2; valY += valueChangePerCellY) {
            float y = map(valY - nearValueY, 0, valueRangeY, -h / 2f, h / 2f);
            if (abs(y) <= h / 2f) {
                pg.line(-w / 2, y, w / 2, y);
            }
        }
        strokeForegroundBasedOnMouseOver(pg);
        pg.strokeWeight(7.5f);
        pg.point(0.5f, 0.5f);
        pg.popMatrix();
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
