package com.krab.lazy.nodes;

public class GradientColorStopPositionSlider extends SliderNode{

    public GradientColorStopPositionSlider(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, constrained);
        maximumFloatPrecisionIndex = precisionRange.indexOf(0.1f);
        setPrecisionIndexAndValue(precisionRange.indexOf(0.01f));
    }
}
