package lazy.nodes;

import lazy.input.LazyKeyEvent;
import lazy.stores.UndoRedoStore;
import lazy.utils.KeyCodes;
import processing.core.PGraphics;
import processing.core.PVector;

import static lazy.stores.LayoutStore.cell;

public class PlotFolderNode extends FolderNode {
    static final String PLOT_DISPLAY_NAME = "_";
    static final String SLIDER_X_NAME = "x";
    static final String SLIDER_Y_NAME = "y";
    static final String SLIDER_Z_NAME = "z";

    private final SliderNode sliderX;
    private final SliderNode sliderY;
    private SliderNode sliderZ;

    public PlotFolderNode(String path, FolderNode parent, PVector defaultXY, boolean useZ) {
        super(path, parent);
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
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawRightBackdrop(pg, cell);
        String vectorToDisplay = sliderX.getValueToDisplay() + "," + sliderY.getValueToDisplay();
        if(sliderZ != null){
            vectorToDisplay += "," + sliderZ.getValueToDisplay();
        }
        drawRightText(pg, vectorToDisplay, true);
    }

    public PVector getVectorValue() {
        return new PVector(
                sliderX.valueFloat,
                sliderY.valueFloat,
                sliderZ == null ? 0 : sliderZ.valueFloat
        );
    }

    public void setVectorValue(float x, float y, float z) {
        sliderX.valueFloat = x;
        sliderY.valueFloat = y;
        if(sliderZ != null){
            sliderZ.valueFloat = z;
        }
    }

    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        if((e.isControlDown() && e.getKeyCode() == KeyCodes.C) || (e.isControlDown() && e.getKeyCode() == KeyCodes.V)){
            super.keyPressedOverNode(e, x, y);
            UndoRedoStore.onUndoableActionEnded();
        }else{
            sliderX.keyPressedOverNode(e, x, y);
            sliderY.keyPressedOverNode(e, x, y);
        }
    }
}
