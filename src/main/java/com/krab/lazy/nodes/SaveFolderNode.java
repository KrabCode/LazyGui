package com.krab.lazy.nodes;

import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.stores.NodeTree;
import processing.core.PGraphics;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.krab.lazy.stores.JsonSaveStore.*;

public class SaveFolderNode extends FolderNode {

    private final List<AbstractNode> childrenThatAreNotSaveFiles = new ArrayList<>();
    private final ButtonNode buttonCreateNewSave, buttonOpenSaveFolder;
    private final ToggleNode autosaveEnabledToggle, autosaveGuardToggle, readAutosaveExplanationToggle;
    private final SliderIntNode autosaveMillisSlider;
    private final String readmeTextNodePath;
    private final TextNode autosaveReadmeTextNode;
    private final String autosaveReadmeContents =
            "- autosave triggers on graceful sketch exit\n" +
            "- save guard blocks autosave in unresponsive sketches\n" +
            "- unresponsive meaning it took longer than \n" +
            "   'save guard (ms)' to render the last frame";

    public SaveFolderNode(String path, FolderNode parent) {
        super(path, parent);
        buttonCreateNewSave = new ButtonNode(path + "/create new save", this);
        buttonOpenSaveFolder = new ButtonNode(path + "/open save folder", this);
        String autosaveFolderPath = "/autosave rules";
        FolderNode autosaveFolder = new FolderNode(path + autosaveFolderPath, this);
        autosaveEnabledToggle = new ToggleNode(path + autosaveFolderPath + "/autosave enabled", autosaveFolder, autosaveOnExitEnabled);
        autosaveGuardToggle = new ToggleNode(path + autosaveFolderPath + "/save guard", autosaveFolder, autosaveLockGuardEnabled);
        autosaveMillisSlider = new SliderIntNode(path + autosaveFolderPath + "/save guard (ms)", autosaveFolder, 1000, 1, 1000 * 60 * 60, true);
        readAutosaveExplanationToggle = new ToggleNode(path + autosaveFolderPath + "/read more", autosaveFolder, false);
        readmeTextNodePath = path + autosaveFolderPath + "/readme";
        autosaveReadmeTextNode = new TextNode(readmeTextNodePath, autosaveFolder, autosaveReadmeContents);
        autosaveFolder.children.add(autosaveEnabledToggle);
        autosaveFolder.children.add(autosaveGuardToggle);
        autosaveFolder.children.add(autosaveMillisSlider);
        autosaveFolder.children.add(readAutosaveExplanationToggle);
        autosaveFolder.children.add(autosaveReadmeTextNode);
        children.add(buttonCreateNewSave);
        children.add(buttonOpenSaveFolder);
        children.add(autosaveFolder);
        childrenThatAreNotSaveFiles.addAll(children);
        updateReadmeVisibility();
        updateSaveList();
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        autosaveOnExitEnabled = autosaveEnabledToggle.valueBoolean;
        autosaveLockGuardEnabled = autosaveGuardToggle.valueBoolean;
        autosaveLockGuardMillisLimit = autosaveMillisSlider.getIntValue();
        if(buttonCreateNewSave.getBooleanValueAndSetItToFalse()){
            JsonSaveStore.createNewManualSave();
        }
        if(buttonOpenSaveFolder.getBooleanValueAndSetItToFalse()){
            openSaveFolder();
        }
        updateReadmeVisibility();
        updateSaveList();
    }

    private void updateReadmeVisibility() {
        if(readAutosaveExplanationToggle.valueBoolean){
            NodeTree.showAtFullPath(readmeTextNodePath);
            autosaveReadmeTextNode.setStringValue(autosaveReadmeContents);
        }else{
            NodeTree.hideAtFullPath(readmeTextNodePath);
        }
    }

    void updateSaveList() {
        List<File> filenames = JsonSaveStore.getSaveFileList();
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
                children.add(childrenThatAreNotSaveFiles.size(), new SaveItemNode(childNodePath, this, filename));
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

    protected void drawNodeBackground(PGraphics pg) {
        super.drawNodeBackground(pg);
    }

    static void openSaveFolder() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(JsonSaveStore.getSaveDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
