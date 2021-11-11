package toolbox.tree;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

// knows nothing about the window,
// one TreeNode corresponds to one Window in their paths being equal
public class Node {
    public PVector screenPos = new PVector();
    public PVector screenSize = new PVector();
    public final String path;
    public final String name;
    public final long timeCreated;
    public final ArrayList<Node> children = new ArrayList<>();

    public Node(PApplet app, String path, String name) {
        this.path = path;
        this.name = name;
        timeCreated = app.millis();
    }
}
