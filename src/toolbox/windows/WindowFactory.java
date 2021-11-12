package toolbox.windows;

import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.controls.*;

import static toolbox.tree.NodeType.FOLDER;

public class WindowFactory {

    private static final float cell = GlobalState.cell;

    public static Window createWindowFromNode(Node node) {
        PVector pos = new PVector(node.screenPos.x + cell * 10, node.screenPos.y);

        switch(node.type){
            case FOLDER:
                break;
            case SLIDER_X:
                return new SliderFloatWindow(node, pos);
            case SLIDER_INT_X:
                return new SliderIntWindow(node, pos);
            case PLOT_XY:
                return new PlotXYWindow(node, pos);
            case PLOT_XYZ:
                return new PlotXYZWindow(node, pos);
            case COLOR_PICKER:
                return new ColorPickerWindow(node, pos);
            case GRADIENT_PICKER:
                return new GradientPickerWindow(node, pos);
            case BUTTON:
                return new ButtonWindow(node, pos);
            case TOGGLE:
                return new ToggleWindow(node, pos);
        }
        return null;
    }

    public static TreeWindow createMainTreeWindow(){
        return new TreeWindow(new Node("/", ">", FOLDER),
                new PVector(cell,cell),
                new PVector(cell * 10, cell * 15),
                false
        );
    }
}
