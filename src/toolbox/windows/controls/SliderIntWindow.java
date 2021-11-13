package toolbox.windows.controls;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.Window;

public class SliderIntWindow extends ControlWindow {

    public SliderIntWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 8, GlobalState.cell * 4));
    }

    @Override
    protected void drawContent(PGraphics pg) {

    }
}
