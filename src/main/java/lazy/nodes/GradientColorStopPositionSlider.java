package lazy.nodes;

public class GradientColorStopPositionSlider extends SliderNode{

    public GradientColorStopPositionSlider(String path, FolderNode parentFolder, float defaultValue, float min, float max, boolean constrained) {
        super(path, parentFolder, defaultValue, min, max, constrained);
        setPrecisionIndexAndValue(precisionRange.indexOf(0.01f));
    }

    @Override
    protected void validatePrecision() {
        float maximumFloatPrecision = 0.1f;
        if (valueFloatPrecision >= maximumFloatPrecision) {
            setPrecisionIndexAndValueWithoutValidation(precisionRange.indexOf(maximumFloatPrecision));
        }
    }
}
