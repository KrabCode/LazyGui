package toolbox.windows.nodes.shaderList;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.NodeType;
import toolbox.windows.nodes.SliderNode;
import toolbox.windows.nodes.colorPicker.ColorPickerFolderNode;

public class PremadeShaderFolder extends FolderNode {

    public PremadeShaderFolder(String path, FolderNode parent) {
        super(path, parent);

        // TODO make a separate folder for ready-made filters
        ShaderListItem chromaKey = new ShaderListItem(path + "/chromakey", this, "filters/chromaKey.glsl", false);
        chromaKey.children.add(new ColorPickerFolderNode(chromaKey.path + "/color", chromaKey, State.normalizedColorProvider.color(0.75f,0.5f,1)));
        chromaKey.children.add(new SliderNode(chromaKey.path  + "/base", chromaKey,  0.9f, -1, 1,0.01f, true));
        chromaKey.children.add(new SliderNode(chromaKey.path  + "/ramp", chromaKey, -0.1f, -1,  1,0.01f, true));
        children.add(chromaKey);
    }


    public void applyShaders(PGraphics pg) {
        for (AbstractNode child : children) {
            if (child.type == NodeType.FOLDER_ROW) {
                ShaderListItem childShader = (ShaderListItem) child;
                childShader.applyShader(pg, false);
            }
        }
    }
}
