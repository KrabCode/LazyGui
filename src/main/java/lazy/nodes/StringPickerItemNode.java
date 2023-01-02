package lazy.nodes;

class StringPickerItemNode extends ToggleNode {

    final String valueString;

    StringPickerItemNode(String path, FolderNode folder, boolean valueBoolean, String valueString) {
        super(path, folder, valueBoolean);
        this.type = NodeType.TRANSIENT;
        this.valueString = valueString;
    }

    @Override
    public void mouseReleasedOverNode(float x, float y){
        if(armed && !valueBoolean){ // can only toggle manually to true, toggle to false happens automatically
            valueBoolean = true;
        }
        armed = false;
    }
}
