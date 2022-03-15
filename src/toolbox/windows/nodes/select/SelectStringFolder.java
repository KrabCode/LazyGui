package toolbox.windows.nodes.select;

import processing.core.PGraphics;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeFolder;

import java.util.HashMap;
import java.util.Map;

public class SelectStringFolder extends NodeFolder {

    public String valueString;
    Map<String, Boolean> oldValues = new HashMap<>();

    public SelectStringFolder(String path, NodeFolder parent, String[] options) {
        super(path, parent);
        valueString = options[0];
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            boolean valueBoolean = i == 0;
            String childPath = path + "/" + option;
            children.add(new SelectStringItem(childPath, this, valueBoolean, option));
            oldValues.put(childPath, valueBoolean);
        }
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        // don't draw folder icon
        reflectChildValueChange();
        rememberCurrentValues();
    }

    private void reflectChildValueChange() {
        for (AbstractNode child : children) {
            SelectStringItem option = (SelectStringItem) child;
            boolean oldValue = oldValues.get(option.path);
            if (option.valueBoolean && !oldValue) {
                setAllOtherOptionsToFalse(option);
                valueString = option.valueString;
                break;
            }
        }
    }

    private void setAllOtherOptionsToFalse(SelectStringItem optionToKeepTrue) {
        for (AbstractNode child : children) {
            SelectStringItem option = (SelectStringItem) child;
            if(!option.path.equals(optionToKeepTrue.path)){
                option.valueBoolean = false;
            }
        }
    }

    private void rememberCurrentValues(){
        for (AbstractNode child : children) {
            SelectStringItem option = (SelectStringItem) child;
            oldValues.put(option.path, option.valueBoolean);
        }
    }

    @Override
    public void drawLeftText(PGraphics pg, String text) {
        super.drawLeftText(pg, text);
        String shortenedValue = valueString;
        if(shortenedValue.length() > 9){
            shortenedValue = valueString.substring(0, 9);
        }
        drawRightText(pg, shortenedValue);
    }

}
