package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.windows.nodes.ButtonNode;
import toolbox.windows.nodes.NodeFolder;

import java.awt.*;
import java.io.File;
import java.util.List;

public class StateListFolder extends NodeFolder {

    public StateListFolder(String path, NodeFolder parent) {
        super(path, parent);
        if(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)){
            children.add(new OpenFolderNode(path + "/open folder", this));
        }
        children.add(new ButtonNode(path + "/save", this));
        updateStateList();
    }

    public void updateStateList() {
        List<File> filenames = State.getSaveFileList();
        if(filenames == null){
            return;
        }
        for (File file : filenames) {
            String filename = file.getName();
            if (!filename.contains(".json")) {
                continue;
            }
            String saveDisplayName = "- " + filename.substring(0, filename.indexOf(".json"));
            String nodePath = path + "/" + saveDisplayName;
            if(NodeTree.findNodeByPathInTree(nodePath) == null){
                children.add(1, new StateItemNode(nodePath, this, filename));
            }
        }
        children.sort((o1, o2) -> o2.name.compareTo(o1.name));
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(State.gui.button(path + "/save")){
            State.createTreeSaveFile();
        }
        updateStateList();
    }


}
