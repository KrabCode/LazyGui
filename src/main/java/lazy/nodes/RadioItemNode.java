package lazy.nodes;

class RadioItemNode extends ToggleNode {

    final String valueString;

    RadioItemNode(String path, FolderNode folder, boolean valueBoolean, String valueString) {
        super(path, folder, valueBoolean);
        this.type = NodeType.TRANSIENT;
        this.valueString = valueString;
    }

    @Override
    public void mouseReleasedOverNode(float x, float y){
        if(armed && !valueBoolean){ // can only toggle manually to true, toggle to false happens automatically
            valueBoolean = true;
            onActionEnded();
        }
        armed = false;
    }
}
