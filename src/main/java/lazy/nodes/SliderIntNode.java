package lazy.nodes;

import processing.core.PApplet;

public class SliderIntNode extends SliderNode {

    final float minimumIntPrecision = 0.1f;

    public SliderIntNode(String path, FolderNode parentFolder, int defaultValue, int min, int max, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, constrained);
    }

    public int getIntValue(){
        return PApplet.floor(valueFloat);
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

    @Override
    protected void validatePrecision() {
        if (valueFloatPrecision <= minimumIntPrecision) {
            valueFloatPrecision = minimumIntPrecision;
            currentPrecisionIndex = precisionRange.indexOf(minimumIntPrecision);
        }
    }
}
