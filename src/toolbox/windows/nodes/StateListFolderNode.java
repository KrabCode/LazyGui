package toolbox.windows.nodes;

import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class StateListFolderNode extends FolderNode{

    public StateListFolderNode(String path, FolderNode parent) {
        super(path, parent);
        children.add(new ButtonNode(path + "/save", this));
        size.x += parent.cell * 2;
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
            String treePath = path + "/" + shortenedName;
            if(NodeTree.findNodeByPathInTree(treePath) == null){
                children.add(1, new LoadStateItemNode(treePath, this, filename));
            }
        }
        children.sort(new Comparator<AbstractNode>() {
            @Override
            public int compare(AbstractNode o1, AbstractNode o2) {
                return o2.name.compareTo(o1.name);
            }
        });
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(State.gui.button(path + "/save")){
            State.createTreeSaveFile();
        }
        updateStateList();
    }

    static class LoadStateItemNode extends AbstractNode {
        String filename;
        public LoadStateItemNode(String path, FolderNode parent, String filename) {
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
