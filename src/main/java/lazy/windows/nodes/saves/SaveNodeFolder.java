package lazy.windows.nodes.saves;

import lazy.windows.nodes.ToggleNode;
import processing.core.PGraphics;
import lazy.NodeTree;
import lazy.State;
import lazy.Utils;
import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.ButtonNode;
import lazy.windows.nodes.NodeFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveNodeFolder extends NodeFolder {

    ArrayList<AbstractNode> childrenThatAreNotSaveFiles = new ArrayList<>();

    public SaveNodeFolder(String path, NodeFolder parent) {
        super(path, parent);
        children.add(new ButtonNode(path + "/open save folder", this));
        children.add(new ButtonNode(path + "/create new save", this));
        children.add(new ToggleNode(path + "/autosave on exit", this, true));
        childrenThatAreNotSaveFiles.addAll(children);
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
                children.add(childrenThatAreNotSaveFiles.size(), new SaveNode(childNodePath, this, filename, file.getAbsolutePath()));
            }
        }
    }

    private void removeChildrenWithDeletedSaveFiles(List<File> existingFilenames) {
        List<AbstractNode> childrenToRemove = new ArrayList<>();
        for(AbstractNode child : children){
            if(childrenThatAreNotSaveFiles.contains(child)){
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

    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        super.updateDrawInlineNodeAbstract(pg);
        if(State.gui.button(path + "/create new save")){
            State.createNewSaveWithRandomName();
        }
        if(State.gui.button(path + "/open save folder")){
            Utils.openSaveFolder();
        }
        State.autosaveEnabled = State.gui.toggle(path + "/autosave on exit");
        updateStateList();
    }

}