package toolbox.windows;

import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.controls.*;
import toolbox.windows.controls.unfinished.ColorPickerWindow;
import toolbox.windows.controls.unfinished.GradientPickerWindow;
import toolbox.windows.controls.unfinished.PlotWindowXY;
import toolbox.windows.controls.unfinished.PlotWindowXYZ;

import static toolbox.tree.NodeType.FOLDER;
import static toolbox.tree.NodeType.TREE;

public class WindowFactory {

    private static final float cell = GlobalState.cell;

    public static Window createWindowFromNode(Node node, PVector pos) {

        switch(node.type){
            case FOLDER:
                break;
            case SLIDER_X:
                return new SliderFloatWindow(node, pos);
            case SLIDER_INT_X:
                return new SliderIntWindow(node, pos);
            case PLOT_XY:
                return new PlotWindowXY(node, pos);
            case PLOT_XYZ:
                return new PlotWindowXYZ(node, pos);
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
        return new TreeWindow(new Node("/", ">", TREE),
                new PVector(cell,cell),
                new PVector(cell * 8, cell * 10),
                false
        );
    }
}
