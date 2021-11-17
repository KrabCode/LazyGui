package toolbox.window;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.WindowManager;
import toolbox.tree.Tree;
import toolbox.window.Window;

// an explorer is a GUI element that lets the user traverse folders and open their windows
public class TreeExplorer extends Window {
    private final Tree tree;
    public TreeExplorer(PVector pos, PVector size, Tree tree) {
        super(pos, size, tree.name, false);
        this.tree = tree;
    }

    @Override
    protected void drawContent(PGraphics pg) {

    }
}
