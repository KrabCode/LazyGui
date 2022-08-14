package lazy.windows.nodes.sliders;

import processing.core.PApplet;
import lazy.windows.nodes.NodeFolder;

public class SliderIntNode extends SliderNode {

    float minimumIntPrecision = 0.1f;

    public SliderIntNode(String path, NodeFolder parentFolder, int defaultValue, int min, int max, float defaultPrecision, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, defaultPrecision, constrained);
    }

    public int getIntValue(){
        return PApplet.floor(valueFloat);
    }

    @Override
    public String getValueToDisplay() {
        return String.valueOf(PApplet.floor(valueFloat));
    }

    @Override
    public void validatePrecision() {
        if (valueFloatPrecision <= minimumIntPrecision) {
            valueFloatPrecision = minimumIntPrecision;
            currentPrecisionIndex = precisionRange.indexOf(minimumIntPrecision);
        }
    }
}
