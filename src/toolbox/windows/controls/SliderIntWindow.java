package toolbox.windows.controls;

import processing.core.PApplet;
import processing.core.PVector;
import toolbox.tree.Node;

import static processing.core.PApplet.println;

@SuppressWarnings("DuplicatedCode")
public class SliderIntWindow extends SliderFloatWindow {

    float minimumIntPrecision = 0.1f;

    public SliderIntWindow(Node node, PVector pos) {
        super(node, pos);
    }

    @Override
    protected String getValueToDisplay() {
        return String.valueOf(PApplet.round(node.valueFloat));
    }

    @Override
    protected void validatePrecision(){
        if(node.valueFloatPrecision < minimumIntPrecision){
            node.valueFloatPrecision = minimumIntPrecision;
            currentPrecisionIndex = minimumIntPrecisionIndex;
        }
    }
}
