package lazy;

import processing.core.PGraphics;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.println;

class SaveFolderNode extends FolderNode {
    final String pathPrintFolderPath = "/print folder path";
    final String pathOpenSaveFolder  = "/open save folder";
    final String pathAutosaveOnExit  = "/autosave on exit";
    final String pathCreateNewSave   = "/create new save";
    ArrayList<AbstractNode> childrenThatAreNotSaveFiles = new ArrayList<>();

    SaveFolderNode(String path, FolderNode parent) {
        super(path, parent);
        children.add(new ButtonNode(path + pathCreateNewSave, this));
        children.add(new ButtonNode(path + pathPrintFolderPath, this));
        children.add(new ButtonNode(path + pathOpenSaveFolder , this));
        children.add(new ToggleNode(path + pathAutosaveOnExit  , this, false));
        childrenThatAreNotSaveFiles.addAll(children);
        updateStateList();
    }

    void updateStateList() {
        List<File> filenames = UtilJsonSaves.getSaveFileList();
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
            if(findChildByName(saveDisplayName) == null){
                children.add(childrenThatAreNotSaveFiles.size(), new SaveNode(childNodePath, this, filename));
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
        if(State.gui.button(path + pathCreateNewSave)){
            UtilJsonSaves.createNewSaveWithRandomName();
        }
        if(State.gui.button(path + pathOpenSaveFolder)){
            openSaveFolder();
        }
        if(State.gui.button(path + pathPrintFolderPath)){
            println("LazyGui save folder: " + UtilJsonSaves.getSaveDir().getAbsolutePath());
        }

        LazyGui.autosaveEnabled = State.gui.toggle(path + pathAutosaveOnExit, LazyGui.autosaveEnabled);
        updateStateList();
    }

    static void openSaveFolder() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(UtilJsonSaves.getSaveDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
