package toolbox.windows.nodes.select;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;

import java.util.HashMap;
import java.util.Map;

public class StringPickerFolder extends NodeFolder {

    public String valueString;
    Map<String, Boolean> oldValues = new HashMap<>();

    public StringPickerFolder(String path, NodeFolder parent, String[] options, String defaultOption) {
        super(path, parent);
        //TODO throw error if default option is not contained in the options
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
        State.overwriteWithLoadedStateIfAny(this);
        checkForChildValueChange(); // loading from json may have changed the child booleans, so we need to reflect this in valueString and oldValues
        rememberCurrentValues();
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        // don't draw folder icon
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

    private void setAllOtherOptionsToFalse(StringPickerItem optionToKeepTrue) {
        for (AbstractNode child : children) {
            StringPickerItem option = (StringPickerItem) child;
            if(!option.path.equals(optionToKeepTrue.path)){
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
    public void drawLeftText(PGraphics pg, String text) {
        super.drawLeftText(pg, text);
        drawRightText(pg, valueString);
    }
}
