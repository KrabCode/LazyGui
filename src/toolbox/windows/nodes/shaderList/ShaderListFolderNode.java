package toolbox.windows.nodes.shaderList;

import processing.core.PGraphics;
import toolbox.windows.nodes.FolderNode;

public class ShaderListFolderNode extends FolderNode {

    public ShaderListFolderNode(String path, FolderNode parent) {
        super(path, parent);
        children.add(new ShaderFolderNode(path + "/anti-alias", this));
        children.add(new ShaderFolderNode(path + "/blur", this));
        children.add(new ShaderFolderNode(path + "/sharpen", this));
        children.add(new ShaderFolderNode(path + "/chromab", this));
    }

    public void filter(PGraphics pg) {

    }
}
