package toolbox.windows.rows;

import processing.core.PApplet;

public class SliderIntRow extends SliderRow {

    float minimumIntPrecision = 0.1f;

    public SliderIntRow(String path, FolderRow parentFolder) {
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
