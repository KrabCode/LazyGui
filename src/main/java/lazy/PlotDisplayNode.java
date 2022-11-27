package lazy;

import processing.core.PGraphics;

import static processing.core.PApplet.*;


public class PlotDisplayNode extends AbstractNode {

    private SliderNode sliderX;
    private SliderNode sliderY;

    protected PlotDisplayNode(String path, FolderNode parentFolder, SliderNode sliderX, SliderNode sliderY) {
        super(NodeType.TRANSIENT, path, parentFolder);
        this.sliderX = sliderX;
        this.sliderY = sliderY;
        rowHeightInCells = 6;
        shouldDrawLeftNameText = false;
    }

    @Override
    void mousePressedOverNode(float x, float y) {
        super.mousePressedOverNode(x, y);
        sliderY.verticalMouseMode = true;
        sliderX.mousePressedOverNode(x,y);
        sliderY.mousePressedOverNode(x,y);
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
    }

    @Override
    void mouseWheelMovedOverNode(float x, float y, int dir) {
        super.mouseWheelMovedOverNode(x, y, dir);
        if (dir > 0) {
            sliderX.increasePrecision();
            sliderY.increasePrecision();
        } else if (dir < 0) {
            sliderX.decreasePrecision();
            sliderY.decreasePrecision();
        }
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        pg.noFill();
        drawPlotGrid(pg);
    }

    private void drawPlotGrid(PGraphics pg) {
        pg.pushMatrix();
        int cellCount = floor(parent.idealWindowWidthInCells);
        float w = (size.x - 2);
        float h = (size.y - 2);
        float valueX = sliderX.valueFloat;
        float valueY = sliderY.valueFloat;
        float xStep = w / cellCount;
        float yStep = xStep;
        float xOffset = -(valueX % xStep);
        float yOffset = -(valueY % yStep);
        strokeForegroundBasedOnMouseOver(pg);
        pg.translate(1, 1);
        for (int i = 0; i <= cellCount; i++) {
            pg.strokeWeight(0.25f);
            float x = xOffset + i * xStep;
            if (x >= 0 && x <= w) {
                pg.line(x, 0, x, h);
            }
            float y = yOffset + i * yStep;
            if (y >= 0 && y <= h) {
                pg.line(0, y, w, y);
            }
        }
        pg.stroke(State.colorStore.color(1,1,1));
        pg.strokeWeight(10);
        pg.point(w / 2, h / 2);
        pg.popMatrix();
    }


}
