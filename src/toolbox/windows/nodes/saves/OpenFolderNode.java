package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.NodeType;

import java.awt.*;
import java.io.IOException;

public class OpenFolderNode extends AbstractNode {

    public OpenFolderNode(String path, FolderNode parentFolder) {
        super(NodeType.TRANSIENT_ROW, path, parentFolder);
    }

    protected void updateDrawInlineNode(PGraphics pg) {

    }

    public void mouseReleasedOverNode(float x, float y) {
        super.mouseReleasedOverNode(x, y);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(State.saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
