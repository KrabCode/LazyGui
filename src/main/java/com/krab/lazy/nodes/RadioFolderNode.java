package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.stores.LayoutStore;
import processing.core.PGraphics;

import java.util.*;

import static com.krab.lazy.stores.LayoutStore.cell;

public class RadioFolderNode extends FolderNode {

    @Expose
    public
    String valueString;
    final Map<String, Boolean> oldValues = new HashMap<>();
    private String[] options;

    public RadioFolderNode(String path, FolderNode parent, String[] options, String defaultOption) {
        super(path, parent);
        setOptions(options, defaultOption);
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
        checkForChildValueChange(); // loading from json may have changed the child booleans, so we need to reflect this in valueString and oldValues
        rememberCurrentValues();
    }

    public List<String> getOptions() {
        return Arrays.asList(options);
    }

    public void setOptions(String[] options, String defaultOption) {
        if(options == null){
            options = new String[0];
        }
        if(!children.isEmpty() || !arrayContains(options, defaultOption)){
            defaultOption = null;
        }
        if(!arrayContains(options, valueString)){
            valueString = null;
        }
        if (valueString != null) {
            defaultOption = this.valueString;
        }
        if (options.length > 0) {
            valueString = options[0];
        }
        if (defaultOption != null) {
            valueString = defaultOption;
        }
        this.options = options;
        children.clear();
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            boolean valueBoolean;
            if (defaultOption == null) {
                valueBoolean = i == 0;
            } else {
                valueBoolean = option.equals(defaultOption);
            }
            String childPath = path + "/" + option;
            children.add(new RadioItemNode(childPath, this, valueBoolean, option));
            oldValues.put(childPath, valueBoolean);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean arrayContains(String[] options, String query) {
        if (options == null) {
            return false;
        }
        for (String option : options) {
            if (option.equals(query)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        if (LayoutStore.shouldHideRadioValue()) {
            super.drawNodeForeground(pg, name);
        } else {
            drawRightBackdrop(pg, cell);
            drawRightTextToNotOverflowLeftText(pg, getValueAsString(), name, true); //we need to calculate how much space is left for value after the name is displayed
        }
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        checkForChildValueChange();
        rememberCurrentValues();
    }

    private void checkForChildValueChange() {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            boolean oldValue = oldValues.get(option.path);
            if (option.valueBoolean && !oldValue) {
                valueString = option.valueString;
                setAllOtherOptionsToFalse(option);
                onActionEnded();
                break;
            }
        }
    }

    public void selectOption(String optionToSet) {
        boolean success = false;
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if (option.valueString.equals(optionToSet)) {
                option.valueBoolean = true;
                success = true;
            }
        }
        if (success) {
            setAllOtherOptionsToFalse(optionToSet);
            onActionEnded();
        }
    }

    void setAllOtherOptionsToFalse(RadioItemNode optionToKeepTrue) {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if (!option.path.equals(optionToKeepTrue.path)) {
                option.valueBoolean = false;
            }
        }
    }

    void setAllOtherOptionsToFalse(String optionToKeepTrue) {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if (!option.valueString.equals(optionToKeepTrue)) {
                option.valueBoolean = false;
            }
        }
    }

    private void rememberCurrentValues() {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            oldValues.put(option.path, option.valueBoolean);
        }
    }

    @Override
    public String getValueAsString() {
        if (options.length == 0) {
            return "null";
        }
        return valueString;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("valueString");
        if (loadedString == null) {
            return;
        }
        String oldValue = loadedString.getAsString();
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if (option.valueString.equals(oldValue)) {
                option.valueBoolean = true;
                setAllOtherOptionsToFalse(option);
            }
        }
    }
}
