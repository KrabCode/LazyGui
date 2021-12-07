package toolbox.windows.nodes.shaderList;

import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.global.ShaderStore;
import toolbox.windows.nodes.*;

import static processing.core.PApplet.floor;

public class ShaderListItem extends FolderNode {

    public final String shaderPath;
    ToggleNode skipNode;

    public ShaderListItem(String path, FolderNode parent, String shaderPath) {
        super(path, parent);
        this.shaderPath = shaderPath;
        skipNode = new ToggleNode(path + "/skip", this, false);
        children.add(skipNode);
    }

    public void filter(PGraphics pg) {
        if(skipNode.valueBoolean){
            return;
        }
        PShader shader = ShaderStore.lazyInitGetShader(shaderPath);
        for(AbstractNode node : children){
            String className = node.className.toLowerCase(); // TODO REMOVE BAD BAD BAD
            if(className.contains("sliderint")){
                SliderIntNode sliderNode = (SliderIntNode) node;
                shader.set(node.name, floor(sliderNode.valueFloat));
            }else if(className.contains("slider")){
                SliderNode sliderNode = (SliderNode) node;
                shader.set(node.name, floor(sliderNode.valueFloat));
            }
        }
        ShaderStore.hotFilter(shaderPath, pg);
    }
}
