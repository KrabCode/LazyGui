package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import static lazy.State.cell;
import static processing.core.PApplet.map;

class PlotFolderNode extends FolderNode {
    static final String PLOT_DISPLAY_NAME = "_";
    static final String SLIDER_X_NAME = "x";
    static final String SLIDER_Y_NAME = "y";
    static final String SLIDER_Z_NAME = "z";

    private final SliderNode sliderX;
    private final SliderNode sliderY;
    private SliderNode sliderZ;

    PlotFolderNode(String path, FolderNode parent, PVector defaultXY, boolean useZ) {
        super(path, parent);
//        isWindowResizable = false;
        idealWindowWidthInCells = 7;
        PVector defaultPos = new PVector();
        if (defaultXY != null) {
            defaultPos = defaultXY.copy();
        }
        sliderX = new SliderNode(path + "/" + SLIDER_X_NAME, this, defaultPos.x, -Float.MAX_VALUE, Float.MAX_VALUE, false);
        sliderY = new SliderNode(path + "/" + SLIDER_Y_NAME, this, defaultPos.y, -Float.MAX_VALUE, Float.MAX_VALUE, false);
        children.add(new PlotDisplayNode(path + "/" + PLOT_DISPLAY_NAME, this, sliderX, sliderY));
        children.add(sliderX);
        children.add(sliderY);
        if (useZ) {
            sliderZ = new SliderNode(path + "/" + SLIDER_Z_NAME, this, defaultPos.z, -Float.MAX_VALUE, Float.MAX_VALUE, false);
            children.add(sliderZ);
        }
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        // super.updateDrawInlineNodeAbstract(pg); // do not draw miniature window icon
        float n = cell * 0.3f;
        pg.pushMatrix();
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        strokeForegroundBasedOnMouseOver(pg);
        pg.strokeWeight(1);
        int count = 3;
        for (int i = 0; i < count; i++) {
            float x = map(i, 0, count-1, -n, n);
            //noinspection SuspiciousNameCombination
            pg.line(-n, x, n, x);
            pg.line(x, -n, x, n);
        }
        pg.popMatrix();
    }

    public PVector getVectorValue() {
        return new PVector(
                sliderX.valueFloat,
                sliderY.valueFloat,
                sliderZ == null ? 0 : sliderZ.valueFloat
        );
    }
}
