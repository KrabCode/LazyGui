package toolbox.windows.controls;

import processing.core.PApplet;
import processing.core.PVector;
import toolbox.tree.Node;

import static processing.core.PApplet.println;

@SuppressWarnings("DuplicatedCode")
public class SliderIntWindow extends SliderFloatWindow {

    public SliderIntWindow(Node node, PVector pos) {
        super(node, pos);
        setPrecision();
    }

    @Override
    protected String getValueToDisplay() {
        return String.valueOf(PApplet.round(node.valueFloat));
    }
}
