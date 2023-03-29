package com.krab.lazy.nodes;

import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.utils.KeyCodes;
import processing.core.PGraphics;
import processing.core.PVector;

import static com.krab.lazy.stores.LayoutStore.cell;

public class PlotFolderNode extends FolderNode {
    static final String PLOT_DISPLAY_NAME = "_";
    static final String SLIDER_X_NAME = "x";
    static final String SLIDER_Y_NAME = "y";
    static final String SLIDER_Z_NAME = "z";

    private final SliderNode sliderX;
    private final SliderNode sliderY;
    private SliderNode sliderZ;
    int syncedPrecisionIndex = -1;

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
        String vectorToDisplay = getValueAsString();
        drawRightText(pg, vectorToDisplay, true);
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        syncPrecision();
    }

    private void syncPrecision() {
        if(syncedPrecisionIndex == -1){
            syncedPrecisionIndex = sliderX.currentPrecisionIndex;
        }
        boolean changeDetected = sliderX.currentPrecisionIndex != syncedPrecisionIndex ||
                sliderY.currentPrecisionIndex != syncedPrecisionIndex ||
                (sliderZ != null && sliderZ.currentPrecisionIndex != syncedPrecisionIndex);
        if(changeDetected){
            if(sliderX.currentPrecisionIndex != syncedPrecisionIndex){
                syncedPrecisionIndex = sliderX.currentPrecisionIndex;
            }else if(sliderY.currentPrecisionIndex != syncedPrecisionIndex){
                syncedPrecisionIndex = sliderY.currentPrecisionIndex;
            }else{
                syncedPrecisionIndex = sliderZ.currentPrecisionIndex;
            }
            sliderX.setPrecisionIndexAndValue(syncedPrecisionIndex);
            sliderY.setPrecisionIndexAndValue(syncedPrecisionIndex);
            if(sliderZ != null){
                sliderZ.setPrecisionIndexAndValue(syncedPrecisionIndex);
            }
        }
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
            onActionEnded();
        }else{
            sliderX.keyPressedOverNode(e, x, y);
            sliderY.keyPressedOverNode(e, x, y);
        }
    }

    @Override
    public String getValueAsString() {
        return sliderX.getValueToDisplay() + "|" + sliderY.getValueToDisplay() +
                (sliderZ == null ? "" : "|" + sliderZ.getValueToDisplay());
    }
}
