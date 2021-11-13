package toolbox.tree;

import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.structs.Color;
import toolbox.structs.Gradient;

import java.util.ArrayList;

// each Window carries one Node to represent its state
public class Node {
    public final NodeType type;
    public PVector screenPos = new PVector();
    public PVector screenSize = new PVector();
    public final String path;
    public final String name;
    public final long timeCreated;
    public final ArrayList<Node> children = new ArrayList<>();

    public float valueFloat;
    public float valueFloatMin;
    public float valueFloatMax;
    public float valueFloatDefault;
    public boolean valueFloatConstrained;

    public float precision = 1;

    public boolean valueBooleanDefault = false;
    public boolean valueBoolean = false;
    public Color valueColor = new Color();
    public Gradient valueGradient = new Gradient();
    public PVector valuePVector = new PVector();

    public Node(String path, String name, NodeType type) {
        this.path = path;
        this.name = name;
        this.type = type;
        timeCreated = GlobalState.app.millis();
    }
}
