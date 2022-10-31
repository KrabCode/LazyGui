package lazy;

import processing.core.PApplet;

class SliderIntNode extends SliderNode {

    float minimumIntPrecision = 0.1f;

    SliderIntNode(String path, FolderNode parentFolder, int defaultValue, int min, int max, float defaultPrecision, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, defaultPrecision, constrained);
    }

    int getIntValue(){
        return PApplet.floor(valueFloat);
    }

    @Override
    String getValueToDisplay() {
        return String.valueOf(PApplet.floor(valueFloat));
    }

    @Override
    protected void validatePrecision() {
        if (valueFloatPrecision <= minimumIntPrecision) {
            valueFloatPrecision = minimumIntPrecision;
            currentPrecisionIndex = precisionRange.indexOf(minimumIntPrecision);
        }
    }
}
