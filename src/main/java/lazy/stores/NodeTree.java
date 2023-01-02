package lazy.stores;

import lazy.nodes.NodeType;
import lazy.nodes.AbstractNode;
import lazy.nodes.FolderNode;
import lazy.utils.NodePaths;

import java.util.*;

import static processing.core.PApplet.println;

public class NodeTree {
    private static final FolderNode root = new FolderNode("", null);
    private static final HashMap<String, AbstractNode> nodesByPath = new HashMap<>();

    private NodeTree() {

    }

    public static FolderNode getRoot() {
        return root;
    }

    public static FolderNode findParentFolderLazyInitPath(String nodePath) {
        String folderPath = NodePaths.getPathWithoutName(nodePath);
        lazyInitFolderPath(folderPath);
        AbstractNode pathParent = findNode(folderPath);
        return (FolderNode) pathParent;
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
                FolderNode folder = (FolderNode) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    // TODO find some way to escape the slash in path params
    // https://github.com/KrabCode/LazyGui/issues/6
    static void lazyInitFolderPath(String path) {
        String[] split = NodePaths.splitByUnescapedSlashes(path);
        String runningPath = split[0];
        FolderNode parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractNode n = findNode(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = root;
                }
                n = new FolderNode(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (FolderNode) n;
            } else if (n.type == NodeType.FOLDER) {
                parentFolder = (FolderNode) n;
            } else {
                println("Expected to find or to be able to create a folder at path \"" + runningPath + "\" but found an existing " + n.className + ". You cannot put any control elements there.");
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
        String folderPath = NodePaths.getPathWithoutName(node.path);
        lazyInitFolderPath(folderPath);
        FolderNode folder = (FolderNode) findNode(folderPath);
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
                FolderNode folder = (FolderNode) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return result;
    }

    public static void setAllNodesMouseOverToFalse(){
        setAllOtherNodesMouseOverToFalse(null);
    }

    public static void setAllOtherNodesMouseOverToFalse(AbstractNode nodeToKeep){
        List<AbstractNode> allNodes = getAllNodesAsList();
        for(AbstractNode node : allNodes){
            if(node == nodeToKeep){
                continue;
            }
            node.isMouseOverNode = false;
        }
    }

    public static FolderNode findFirstOpenParentNodeRecursively(FolderNode node) {
        if(node == getRoot()){
            return null;
        }
        if(node.isParentWindowOpen()){
            return node;
        }
        return findFirstOpenParentNodeRecursively(node.parent);
    }
}

