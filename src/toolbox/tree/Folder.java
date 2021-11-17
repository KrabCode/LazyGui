package toolbox.tree;

import java.util.ArrayList;

public class Folder {
    public final Folder parent;
    public ArrayList<Folder> childFolders = new ArrayList<Folder>();
    public ArrayList<Node> childNodes = new ArrayList<Node>();

    public Folder(Folder parent) {
        this.parent = parent;
    }
}
