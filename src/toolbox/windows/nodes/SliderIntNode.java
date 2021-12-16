package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import processing.core.PApplet;

public class SliderIntNode extends SliderNode {

    float minimumIntPrecision = 0.1f;

    public SliderIntNode(String path, FolderNode parentFolder, int defaultValue, int min, int max, float defaultPrecision, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, defaultPrecision, constrained);
    }

    @Override
    public String getValueToDisplay() {
        return String.valueOf(PApplet.round(valueFloat));
    }

    @Override
    public void validatePrecision() {
        if (valueFloatPrecision <= minimumIntPrecision) {
            valueFloatPrecision = minimumIntPrecision;
            currentPrecisionIndex = precisionRange.indexOf(minimumIntPrecision);
        }
    }
}
