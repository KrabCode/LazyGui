package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.ButtonNode;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.NodeType;

import java.io.File;
import java.util.List;

public class StateListFolderNode extends FolderNode {

    public StateListFolderNode(String path, FolderNode parent) {
        super(path, parent);
        children.add(new ButtonNode(path + "/save", this));
        children.add(new OpenFolderNode(path + "/open folder", this));
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
            String shortenedName = filename.substring(0, filename.indexOf(".json"));
            String nodePath = path + "/" + shortenedName;
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

    static class StateItemNode extends AbstractNode {
        String filename;
        public StateItemNode(String path, FolderNode parent, String filename) {
            super(NodeType.VALUE_ROW, path, parent);
            this.filename = filename;
        }

        protected void updateDrawInlineNode(PGraphics pg) {

        }

        public void nodeClicked(float x, float y) {
            State.loadStateFromFile(filename);
        }

    }

}
