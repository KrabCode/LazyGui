package toolbox.windows.nodes.saves;

import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.ButtonNode;
import toolbox.windows.nodes.NodeFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StateListFolder extends NodeFolder {


    ArrayList<AbstractNode> childrenToIgnoreWhenIterating = new ArrayList<>();

    public StateListFolder(String path, NodeFolder parent) {
        super(path, parent);
        children.add(new ButtonNode(path + "/new save", this));
        childrenToIgnoreWhenIterating.addAll(children);
        updateStateList();
        idealWindowWidth = State.cell * 12;
    }

    public void updateStateList() {
        List<File> filenames = State.getSaveFileList();
        if(filenames == null){
            return;
        }
        removeChildrenWithDeletedSaveFiles(filenames);
        addNewlyFoundSaveFilesAsChildren(filenames);
    }

    private void addNewlyFoundSaveFilesAsChildren(List<File> filenames) {
        for (File file : filenames) {
            String filename = file.getName();
            if (!filename.contains(".json")) {
                continue;
            }
            String saveDisplayName = getSaveDisplayName(filename);
            String childNodePath = path + "/" + saveDisplayName;
            if(NodeTree.findNode(childNodePath) == null){
                children.add(1, new StateListItemNode(childNodePath, this, filename, file.getAbsolutePath()));
            }
        }
    }

    private void removeChildrenWithDeletedSaveFiles(List<File> existingFilenames) {
        List<AbstractNode> childrenToRemove = new ArrayList<>();
        for(AbstractNode child : children){
            if(childrenToIgnoreWhenIterating.contains(child)){
                continue;
            }
            boolean childHasLostSourceFile = true;
            for(File file : existingFilenames){
                if(child.name.equals(getSaveDisplayName(file.getName()))){
                    childHasLostSourceFile = false;
                    break;
                }
            }
            if(childHasLostSourceFile){
                childrenToRemove.add(child);
            }
        }
        children.removeAll(childrenToRemove);
        childrenToRemove.clear();
    }

    private String getSaveDisplayName(String filenameWithSuffix) {
        return filenameWithSuffix.substring(0, filenameWithSuffix.indexOf(".json"));
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(State.gui.button(path + "/new save")){
            String newName = Utils.dialogInput("Input new save name:", "Create new save");
            if(newName != null && newName.length() > 0){
                State.createTreeSaveFile(newName);
            }
        }
        updateStateList();
    }

}
