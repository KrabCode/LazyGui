package toolbox.windows.nodes.shaderList;

import processing.core.PGraphics;
import toolbox.windows.nodes.*;

public class ShaderListFolder extends FolderNode {

    public ShaderListFolder(String path, FolderNode parent) {
        super(path, parent);
        ShaderListItem aa = new ShaderListItem(path + "/anti-alias", this, "filters/antiAlias.glsl");
        aa.children.add(new SliderIntNode(aa.path+"/sampleSize", aa, 1, 0, 100, true));
        children.add(aa);
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
