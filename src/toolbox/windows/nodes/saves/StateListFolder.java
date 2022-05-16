package toolbox.windows.nodes.saves;

import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.ButtonNode;
import toolbox.windows.nodes.NodeFolder;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
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
        List<File> filenamesToRemove = new ArrayList<>();
        if(filenames == null){
            return;
        }

        List<AbstractNode> childrenToRemove = new ArrayList<>();
        for(AbstractNode child : children){
            if(child.name.equals("save") || child.name.equals("open folder")){
                continue;
            }
            boolean childHasLostSourceFile = true;
            for(File file : filenames){
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
        for (File file : filenames) {
            String filename = file.getName();
            if (!filename.contains(".json")) {
                continue;
            }
            String saveDisplayName = getSaveDisplayName(filename);
            String childNodePath = path + "/" + saveDisplayName;
            if(!file.exists()){
                PApplet.println("save file does not exist anymore, removing from gui " + saveDisplayName);
                filenamesToRemove.add(file);
                children.remove(findChildByName(saveDisplayName));
            }else if(NodeTree.findNode(childNodePath) == null){
                children.add(1, new StateItemNode(childNodePath, this, filename));
            }
        }
        filenames.removeAll(filenamesToRemove);
        filenamesToRemove.clear();
        children.sort((o1, o2) -> o2.name.compareTo(o1.name));
    }

    private String getSaveDisplayName(String filename) {
        return "- " + filename.substring(0, filename.indexOf(".json"));
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(State.gui.button(path + "/save")){
            State.createTreeSaveFile();
        }
        updateStateList();
    }


}
