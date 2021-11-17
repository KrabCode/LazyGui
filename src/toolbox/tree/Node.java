package toolbox.tree;

import processing.core.PVector;
import toolbox.GlobalState;

public class Node {
    public final NodeType type;
    public final String path;
    public final String name;
    public final long timeCreated;

    public float valueFloat;
    public float valueFloatMin;
    public float valueFloatMax;
    public float valueFloatDefault;
    public boolean valueFloatConstrained;
    public float valueFloatPrecision = 1;
    public float valueFloatPrecisionDefault = 1;

    public boolean valueBooleanDefault = false;
    public boolean valueBoolean = false;

    public Node(String path, String name, NodeType type) {
        this.path = path;
        this.name = name;
        this.type = type;
        timeCreated = GlobalState.app.millis();
    }
}
