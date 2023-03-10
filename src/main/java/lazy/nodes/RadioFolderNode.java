package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import lazy.stores.JsonSaveStore;
import processing.core.PGraphics;

import java.util.*;

import static lazy.stores.LayoutStore.cell;

public class RadioFolderNode extends FolderNode {

    @Expose
    public
    String valueString;
    final Map<String, Boolean> oldValues = new HashMap<>();
    private final String[] options;

    public RadioFolderNode(String path, FolderNode parent, String[] options, String defaultOption) {
        super(path, parent);
        if(!arrayContainsDefault(options, defaultOption)){
            // gracefully ignore the default when it does not appear in the options and carry on as if no default was specified
           defaultOption = null;
        }
        this.options = options;
        valueString = options[0];
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            boolean valueBoolean;
            if(defaultOption == null){
                valueBoolean = i == 0;
            }else{
                valueBoolean = option.equals(defaultOption);
            }
            String childPath = path + "/" + option;
            children.add(new RadioItemNode(childPath, this, valueBoolean, option));
            oldValues.put(childPath, valueBoolean);
        }
        if(defaultOption != null){
            valueString = defaultOption;
        }
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
        checkForChildValueChange(); // loading from json may have changed the child booleans, so we need to reflect this in valueString and oldValues
        rememberCurrentValues();
    }

    public List<String> getOptions(){
        return Arrays.asList(options);
    }

    private boolean arrayContainsDefault(String[] options, String defaultOption) {
        for(String option : options){
            if(option.equals(defaultOption)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
     // don't draw folder icon - do not call super.drawNodeForeground(pg, name)
        drawLeftText(pg, name);
        drawRightBackdrop(pg, cell);
        drawRightText(pg, valueString, true);
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
                break;
            }
        }
    }

    public void selectOption(String optionToSet) {
        boolean success = false;
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if(option.valueString.equals(optionToSet)){
                option.valueBoolean = true;
                success = true;
            }
        }
        if(success){
            setAllOtherOptionsToFalse(optionToSet);
            onActionEnded();
        }
    }

    void setAllOtherOptionsToFalse(RadioItemNode optionToKeepTrue) {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if(!option.path.equals(optionToKeepTrue.path)){
                option.valueBoolean = false;
            }
        }
    }

    void setAllOtherOptionsToFalse(String optionToKeepTrue) {
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if(!option.valueString.equals(optionToKeepTrue)){
                option.valueBoolean = false;
            }
        }
    }

    private void rememberCurrentValues(){
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            oldValues.put(option.path, option.valueBoolean);
        }
    }

    @Override
    public String getConsolePrintableValue() {
        return valueString;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("valueString");
        if(loadedString == null){
            return;
        }
        String oldValue = loadedString.getAsString();
        for (AbstractNode child : children) {
            RadioItemNode option = (RadioItemNode) child;
            if(option.valueString.equals(oldValue)){
                option.valueBoolean = true;
                setAllOtherOptionsToFalse(option);
            }
        }
    }
}
