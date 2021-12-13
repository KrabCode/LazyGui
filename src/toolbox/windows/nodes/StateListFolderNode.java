package toolbox.windows.nodes;

import processing.core.PGraphics;
import toolbox.global.State;

import java.io.File;
import java.util.List;

public class StateListFolderNode extends FolderNode{

    public StateListFolderNode(String path, FolderNode parent) {
        super(path, parent);
        updateStateList();
    }

    public void updateStateList() {
        children.clear();
        List<File> filenames = State.getSaveFileList();
        for (int i = 0; i < filenames.size(); i++) {
            String filename = filenames.get(i).getName();
            if(!filename.contains(".json")){
                continue;
            }
            String shortenedName = filename.substring(0, filename.indexOf(".json"));
            children.add(new LoadStateItemNode(path + "/" + shortenedName, this, filename));
        }
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        updateStateList();
    }

    class LoadStateItemNode extends AbstractNode {
        String filename;
        public LoadStateItemNode(String path, FolderNode parent, String filename) {
            super(NodeType.VALUE_ROW, path, parent);
            this.filename = filename;
        }

        protected void updateDrawInlineNode(PGraphics pg) {

        }

        public void nodeClicked(float x, float y) {
            State.loadSave(filename);
        }
    }

}
