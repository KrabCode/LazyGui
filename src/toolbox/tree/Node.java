package toolbox.tree;

import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.structs.Color;
import toolbox.structs.Gradient;

import java.util.ArrayList;

// knows nothing about the window,
// one TreeNode corresponds to one Window in their paths being equal
public class Node {
    public final NodeType type;
    public PVector screenPos = new PVector();
    public PVector screenSize = new PVector();
    public final String path;
    public final String name;
    public final long timeCreated;
    public final ArrayList<Node> children = new ArrayList<>();

    private float valueFloat = 0f;
    private float valueInt = 0f;
    private boolean valueBoolean = false;
    private Color valueColor = new Color();
    private Gradient valueGradient = new Gradient();
    private PVector valuePVector = new PVector();

    public Node(String path, String name, NodeType type) {
        this.path = path;
        this.name = name;
        this.type = type;
        timeCreated = GlobalState.app.millis();
    }


    public Object getValue() {
        switch(type){
            case FOLDER:
                return null;
            case SLIDER_X:
                return valueFloat;
            case SLIDER_INT_X:
                return valueInt;
            case PLOT_XY:
            case PLOT_XYZ:
                return valuePVector;
            case COLOR_PICKER:
                return valueColor;
            case GRADIENT_PICKER:
                return valueGradient;
            case BUTTON:
            case TOGGLE:
                return valueBoolean;
        }
        return null;
    }

    public void setValue(Object val) {
        switch(type){
            case FOLDER:
                return;
            case SLIDER_X:
                valueFloat = (float) val;
            case SLIDER_INT_X:
                valueInt = (int) val;
            case PLOT_XY:
            case PLOT_XYZ:
                valuePVector = (PVector) val;
            case COLOR_PICKER:
                valueColor = (Color) val;
            case GRADIENT_PICKER:
                valueGradient = (Gradient) val;
            case BUTTON:
            case TOGGLE:
                valueBoolean = (boolean) val;
        }
    }
}
