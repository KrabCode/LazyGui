package toolbox.global;

import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;

public class NodeStore {
    public FolderNode treeRoot = new FolderNode("", null);
    private final HashMap<String, AbstractNode> nodesByPath = new HashMap<>();

    public NodeStore() {

    }

    public AbstractNode getLazyInitParentFolderByPath(String nodePath){
        String folderPath = getPathWithoutName(nodePath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public AbstractNode findNodeByPathInTree(String path) {
        if(nodesByPath.containsKey(path)){
            return nodesByPath.get(path);
        }
        Queue<AbstractNode> queue = new LinkedList<>();
        queue.offer(treeRoot);
        while (!queue.isEmpty()) {
            AbstractNode node = queue.poll();
            if (node.path.equals(path)) {
                nodesByPath.put(path, node);
                return node;
            }
            if (node.type == NodeType.FOLDER) {
                FolderNode folder = (FolderNode) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    public void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        FolderNode parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractNode n = findNodeByPathInTree(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = treeRoot;
                }
                n = new FolderNode(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (FolderNode) n;
            }else if (n.type == NodeType.FOLDER) {
                parentFolder = (FolderNode) n;
            }else{
                println("expected folder based on path but got value node");
            }
            if(i < split.length - 1){
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public void insertNodeAtItsPath(AbstractNode node) {
        if(findNodeByPathInTree(node.path) != null){
            return;
        }
        String folderPath = getPathWithoutName(node.path);
        lazyCreateFolderPath(folderPath);
        FolderNode folder = (FolderNode) findNodeByPathInTree(folderPath);
        folder.children.add(node);
    }

    public String getPathWithoutName(String pathWithName) {
        String[] split = pathWithName.split("/");
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
            if(i < split.length - 2){
                sum.append("/");
            }
        }
        return sum.toString();
    }
}
