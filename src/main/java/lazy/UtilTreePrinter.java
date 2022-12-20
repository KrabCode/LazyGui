package lazy;

public class UtilTreePrinter {

    static String prettyPrintTree() {
        StringBuilder sb = new StringBuilder();
        buildPrettyPrintedTreeString(NodeTree.getRoot(), 1, sb);
        return sb.toString();
    }

    private static void buildPrettyPrintedTreeString(AbstractNode node, int depth, StringBuilder outputBuilder) {
        StringBuilder prefix = new StringBuilder();
        boolean hasNonTransientChildren = false;

        boolean isFolder = node.type == NodeType.FOLDER;
        if (isFolder) {
            FolderNode folder = (FolderNode) node;
            boolean hasChildren = folder.children.size() > 0;
            if(hasChildren){
                for(AbstractNode child : folder.children){
                    if(child.type != NodeType.TRANSIENT){
                        hasNonTransientChildren = true;
                        break;
                    }
                }
            }
        }
        boolean shouldDisplay = node.type != NodeType.TRANSIENT && (!isFolder || hasNonTransientChildren);
        if (shouldDisplay) {
            for (int i = 0; i < depth; i++) {
                boolean atMaxDepth = i == depth - 1;
                if (atMaxDepth) {
                    prefix.append(hasNonTransientChildren ? "+ " : "- ");
                } else {
                    prefix.append("|  ");
                }
            }
            String nodeValue = node.getConsolePrintableValue();
            boolean hasValue = nodeValue != null && nodeValue.length() > 0;
            outputBuilder.append(prefix)
                    .append(node.name)
                    .append(hasValue ? ": " : "")
                    .append(nodeValue)
                    .append("\n");
        }
        if (isFolder) {
            FolderNode folder = (FolderNode) node;
            AbstractNode skippedOptions = null;
            for (AbstractNode child : folder.children) {
                if("options".equals(child.path)){
                    skippedOptions = child;
                    continue;
                }
                buildPrettyPrintedTreeString(child, depth + 1, outputBuilder);
            }
            if(skippedOptions != null){
                buildPrettyPrintedTreeString(skippedOptions, depth + 1, outputBuilder);
            }
        }
    }
}
