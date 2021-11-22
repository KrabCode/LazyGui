package toolbox.tree.nodes.color;

import com.jogamp.newt.event.MouseEvent;
import toolbox.tree.nodes.SliderNode;

public abstract class ColorSliderNode extends SliderNode {

    public final ColorPickerFolderNode parentColorPickerFolder;
    float maximumFloatPrecision = 0.1f;

    public ColorSliderNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder);
        this.parentColorPickerFolder = parentFolder;
        this.valueFloatDefault = 0;
        this.valueFloat = defaultValue;
        valueFloatPrecisionDefault = 0.01f;
        valueFloatPrecision = valueFloatPrecisionDefault;
        currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
        valueFloatConstrained = true;
        valueFloatMin = 0;
        valueFloatMax = 1;
        initSliderPrecisionArrays();
        initSliderBackgroundShader();
    }


    @Override
    public void validatePrecision() {
        if (valueFloatPrecision >= maximumFloatPrecision) {
            valueFloatPrecision = maximumFloatPrecision;
            currentPrecisionIndex = precisionRange.indexOf(maximumFloatPrecision);
        }
    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragNodeContinue(e, x, y, px, py);
        updateColorInParentFolder();
        e.setConsumed(true);
    }

    @Override
    public void mouseReleasedOverNode(float x, float y) {
        super.mouseReleasedOverNode(x, y);
        updateColorInParentFolder();
    }

    abstract void updateColorInParentFolder();
}
