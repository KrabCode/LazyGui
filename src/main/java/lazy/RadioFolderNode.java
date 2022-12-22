package lazy;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import processing.core.PGraphics;

import java.util.*;

class RadioFolderNode extends FolderNode {

    @Expose
    String valueString;
    final Map<String, Boolean> oldValues = new HashMap<>();
    private final String[] options;

    RadioFolderNode(String path, FolderNode parent, String[] options, String defaultOption) {
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
            children.add(new StringPickerItem(childPath, this, valueBoolean, option));
            oldValues.put(childPath, valueBoolean);
        }
        if(defaultOption != null){
            valueString = defaultOption;
        }
        UtilSaves.overwriteWithLoadedStateIfAny(this);
        checkForChildValueChange(); // loading from json may have changed the child booleans, so we need to reflect this in valueString and oldValues
        rememberCurrentValues();
    }

    List<String> getOptions(){
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
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        // don't draw folder icon
    }

    @Override
    void updateValuesRegardlessOfParentWindowOpenness() {
        checkForChildValueChange();
        rememberCurrentValues();
    }

    private void checkForChildValueChange() {
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            boolean oldValue = oldValues.get(option.path);
            if (option.valueBoolean && !oldValue) {
                valueString = option.valueString;
                setAllOtherOptionsToFalse(option);
                break;
            }
        }
    }

    void selectOption(String optionToSet) {
        boolean success = false;
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            if(option.valueString.equals(optionToSet)){
                option.valueBoolean = true;
                success = true;
            }
        }
        if(success){
            setAllOtherOptionsToFalse(optionToSet);
        }
    }

    void setAllOtherOptionsToFalse(StringPickerItem optionToKeepTrue) {
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            if(!option.path.equals(optionToKeepTrue.path)){
                option.valueBoolean = false;
            }
        }
    }
    void setAllOtherOptionsToFalse(String optionToKeepTrue) {
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            if(!option.valueString.equals(optionToKeepTrue)){
                option.valueBoolean = false;
            }
        }
    }

    private void rememberCurrentValues(){
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            oldValues.put(option.path, option.valueBoolean);
        }
    }

    @Override
    void drawLeftText(PGraphics pg, String text) {
        super.drawLeftText(pg, text);
        drawRightText(pg, valueString);
    }

    @Override
    String getConsolePrintableValue() {
        return valueString;
    }

    @Override
    void overwriteState(JsonElement loadedNode) {
        // TODO test if this even works
        super.overwriteState(loadedNode);
        JsonElement loadedString = loadedNode.getAsJsonObject().get("valueString");
        if(loadedString == null){
            return;
        }
        String oldValue = loadedString.getAsString();
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            if(option.valueString.equals(oldValue)){
                option.valueBoolean = true;
                setAllOtherOptionsToFalse(option);
            }
        }
    }
}
