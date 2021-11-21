package toolbox.tree.nodes.sliders;

import processing.core.PApplet;
import toolbox.tree.nodes.FolderNode;

public class SliderIntNode extends SliderNode {

    float minimumIntPrecision = 0.1f;

    public SliderIntNode(String path, FolderNode parentFolder) {
        super(path, parentFolder);
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
