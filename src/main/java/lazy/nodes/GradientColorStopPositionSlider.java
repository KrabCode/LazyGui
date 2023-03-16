package lazy.nodes;

public class GradientColorStopPositionSlider extends SliderNode{

    private final float maximumFloatPrecision;

    public GradientColorStopPositionSlider(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, constrained);
        maximumFloatPrecision = 0.01f;
    }

    @Override
    protected void validatePrecision() {
        if (valueFloatPrecision >= maximumFloatPrecision) {
            valueFloatPrecision = maximumFloatPrecision;
            currentPrecisionIndex = precisionRange.indexOf(maximumFloatPrecision);
        }
    }
}
