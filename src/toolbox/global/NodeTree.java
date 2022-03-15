package toolbox.global;

import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.main;
import static processing.core.PApplet.println;

public class NodeTree {
    private static final NodeFolder root = new NodeFolder("", null);
    private static final HashMap<String, AbstractNode> nodesByPath = new HashMap<>();

    private NodeTree() {

    }

    public static NodeFolder getRoot() {
        return root;
    }

    public static AbstractNode getLazyInitParentFolderByPath(String nodePath) {
        String folderPath = Utils.getPathWithoutName(nodePath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public static AbstractNode findNodeByPathInTree(String path) {
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

    public static void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        NodeFolder parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractNode n = findNodeByPathInTree(runningPath);
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
        if (findNodeByPathInTree(node.path) != null) {
            return;
        }
        String folderPath = Utils.getPathWithoutName(node.path);
        lazyCreateFolderPath(folderPath);
        NodeFolder folder = (NodeFolder) findNodeByPathInTree(folderPath);
        assert folder != null;
        folder.children.add(node);
    }

}

