package com.krab.lazy.nodes;

import processing.core.PApplet;

public class SliderIntNode extends SliderNode {

    public SliderIntNode(String path, FolderNode parentFolder, int defaultValue, int min, int max, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, constrained, false);
        minimumFloatPrecisionIndex = precisionRange.indexOf(0.01f);
    }

    public int getIntValue(){
        return (int) Math.floor(valueFloat);
    }

    @Override
    public String getValueToDisplay() {
        // float floor as a string
        String floatDisplay = super.getValueToDisplay();
        if(floatDisplay.contains(".")){
            return floatDisplay.split("\\.")[0];
        }
        return floatDisplay;
    }
}
