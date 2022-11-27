package lazy;

import processing.core.PVector;

public class PlotFolderNode extends FolderNode {
    static final String PLOT_DISPLAY_NAME = "_";
    static final String SLIDER_X_NAME = "x";
    static final String SLIDER_Y_NAME = "y";
    static final String SLIDER_Z_NAME = "z";

    private final SliderNode sliderX;
    private final SliderNode sliderY;
    private SliderNode sliderZ;
    private final PlotDisplayNode plot;

    PlotFolderNode(String path, FolderNode parent, PVector defaultXY, boolean useZ) {
        super(path, parent);
        idealWindowWidthInCells = 8;
        PVector defaultPos = new PVector();
        if(defaultXY != null){
            defaultPos = defaultXY.copy();
        }
        sliderX = new SliderNode(path + "/" + SLIDER_X_NAME, this, defaultPos.x, -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f, false);
        sliderY = new SliderNode(path + "/" + SLIDER_Y_NAME, this, defaultPos.y, -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f, false);
        plot = new PlotDisplayNode(path + "/" + PLOT_DISPLAY_NAME, this, sliderX, sliderY);
        children.add(plot);
        children.add(sliderX);
        children.add(sliderY);
        if(useZ){
            sliderZ = new SliderNode(path + "/" + SLIDER_Z_NAME, this, defaultPos.z, -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f, false);
            children.add(sliderZ);
        }
    }

    public PVector getVectorValue() {
        return new PVector(
                sliderX.valueFloat,
                sliderY.valueFloat,
                sliderZ == null ? 0 : sliderZ.valueFloat
        );
    }
}
