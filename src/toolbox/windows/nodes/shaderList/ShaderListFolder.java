package toolbox.windows.nodes.shaderList;

import processing.core.PGraphics;
import toolbox.windows.nodes.*;

public class ShaderListFolder extends FolderNode {

    public ShaderListFolder(String path, FolderNode parent) {
        super(path, parent);
        ShaderListItem darken = new ShaderListItem(path + "/darken", this, "filters/darken.glsl");
        children.add(darken);
        darken.children.add(new SliderNode(darken.path+"/delta", darken, 0.1f, 0, 1, 0.01f, false));

        ShaderListItem chromab = new ShaderListItem(path + "/chromab", this, "filters/chromab.glsl");
        children.add(chromab);
        chromab.children.add(new SliderNode(chromab.path+"/innerEdge", chromab, 0.0f, 0, 1, 0.01f, false));
        chromab.children.add(new SliderNode(chromab.path+"/outerEdge", chromab, 2.5f, 0, 1, 0.01f, false));
        chromab.children.add(new SliderNode(chromab.path+"/intensity", chromab, 1, 0, 1, 0.01f, false));
        chromab.children.add(new SliderNode(chromab.path+"/rotation", chromab, 0, 0, 1, 0.01f, false));
        chromab.children.add(new SliderIntNode(chromab.path+"/steps", chromab, 4, 0, 1, 1f, false));
    }

    public void filter(PGraphics pg) {
        for(AbstractNode child : children){
            if(child.type == NodeType.FOLDER_ROW){
                ShaderListItem childShader = (ShaderListItem) child;
                childShader.filter(pg);
            }
        }
    }
}
