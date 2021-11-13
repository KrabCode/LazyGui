package toolbox.windows.controls;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.Window;

import static processing.core.PApplet.*;
import static processing.core.PApplet.println;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

@SuppressWarnings("DuplicatedCode")
public class SliderIntWindow extends SliderFloatWindow {

    public SliderIntWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 10, GlobalState.cell * 2));
        node.precision = 100;
    }

    @Override
    protected String getValueToDisplay() {
        return String.valueOf(PApplet.round(node.valueFloat));
    }
}
