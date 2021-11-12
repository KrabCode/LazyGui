package toolbox.windows;

import processing.core.PVector;
import toolbox.font.GlobalState;
import toolbox.tree.Node;

public class WindowFactory {

    private static float cell = GlobalState.cell;

    public static Window createWindowFromNode(Node hitboxMatch, float x, float y) {
        return new TestWindow(hitboxMatch.path, hitboxMatch.name,
                new PVector(x, y),
                new PVector(cell * 6, cell * 6));
    }

    public static TreeWindow createMainTreeWindow(){
        return new TreeWindow("/", "tree", new PVector(0,0), new PVector(cell * 10, cell * 5), false);
    }


}
