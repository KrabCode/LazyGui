package toolbox.tree;

import processing.core.PVector;
import toolbox.font.GlobalState;
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

    public Node(String path, String name, NodeType type) {
        this.path = path;
        this.name = name;
        this.type = type;
        timeCreated = GlobalState.app.millis();
    }
}
