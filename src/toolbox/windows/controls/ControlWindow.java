package toolbox.windows.controls;

import processing.core.PVector;
import toolbox.tree.Node;
import toolbox.windows.Window;

public abstract class ControlWindow extends Window {
    public ControlWindow(Node node, PVector pos, PVector size) {
        super(node, pos, size);
    }
}
