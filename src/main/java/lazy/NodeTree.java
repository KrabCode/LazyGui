package lazy;

import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.NodeType;

import java.util.*;

import static processing.core.PApplet.println;

public class NodeTree {
    private static final NodeFolder root = new NodeFolder("", null);
    private static final HashMap<String, AbstractNode> nodesByPath = new HashMap<>();

    private NodeTree() {

    }

    public static NodeFolder getRoot() {
        return root;
    }

    public static NodeFolder findParentFolderLazyInitPath(String nodePath) {
        String folderPath = Utils.getPathWithoutName(nodePath);
        lazyInitFolderPath(folderPath);
        return (NodeFolder) findNode(folderPath);
    }

    public static AbstractNode findNode(String path) {
        if (nodesByPath.containsKey(path)) {
            return nodesByPath.get(path);
        }
        Queue<AbstractNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            AbstractNode node = queue.poll();
            if (node.path.equals(path)) {
                nodesByPath.put(path, node);
                return node;
            }
            if (node.type == NodeType.FOLDER) {
                NodeFolder folder = (NodeFolder) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    // https://github.com/KrabCode/LazyGui/issues/6
    public static void lazyInitFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        NodeFolder parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractNode n = findNode(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = root;
                }
                n = new NodeFolder(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (NodeFolder) n;
            } else if (n.type == NodeType.FOLDER) {
                parentFolder = (NodeFolder) n;
            } else {
                println("expected folder based on path but got value node");
            }
            if (i < split.length - 1) {
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public static void insertNodeAtItsPath(AbstractNode node) {
        if (findNode(node.path) != null) {
            return;
        }
        String folderPath = Utils.getPathWithoutName(node.path);
        lazyInitFolderPath(folderPath);
        NodeFolder folder = (NodeFolder) findNode(folderPath);
        assert folder != null;
        folder.children.add(node);
    }

    public static List<AbstractNode> getAllNodesAsList(){
        List<AbstractNode> result = new ArrayList<>();
        Queue<AbstractNode> queue = new LinkedList<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            AbstractNode node = queue.poll();
            result.add(node);
            if (node.type == NodeType.FOLDER) {
                NodeFolder folder = (NodeFolder) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return result;
    }

    public static void setAllOtherNodesMouseOver(AbstractNode nodeToKeep, boolean newValue){
        List<AbstractNode> allNodes = getAllNodesAsList();
        allNodes.remove(nodeToKeep);
        for(AbstractNode node : allNodes){
            node.isMouseOverNode = newValue;
        }
    }
}

