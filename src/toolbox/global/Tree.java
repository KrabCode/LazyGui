package toolbox.global;

import toolbox.windows.rows.FolderRow;
import toolbox.windows.rows.AbstractRow;
import toolbox.windows.rows.RowType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;

public class Tree {
    public String name;
    public FolderRow root = new FolderRow("", null);
    private final HashMap<String, AbstractRow> nodesByPath = new HashMap<>();

    public Tree(String name) {
        this.name = name;
    }

    public AbstractRow getLazyInitParentFolderByPath(String rowPath){
        String folderPath = getPathWithoutName(rowPath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public AbstractRow findNodeByPathInTree(String path) {
        if(nodesByPath.containsKey(path)){
            return nodesByPath.get(path);
        }
        Queue<AbstractRow> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            AbstractRow row = queue.poll();
            if (row.path.equals(path)) {
                nodesByPath.put(path, row);
                return row;
            }
            if (row.type == RowType.FOLDER) {
                FolderRow folder = (FolderRow) row;
                for (AbstractRow child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    public void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        FolderRow parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractRow n = findNodeByPathInTree(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = root;
                }
                n = new FolderRow(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (FolderRow) n;
            }else if (n.type == RowType.FOLDER) {
                parentFolder = (FolderRow) n;
            }else{
                println("expected folder based on path but got value row");
            }
            if(i < split.length - 1){
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public void insertNodeAtItsPath(AbstractRow row) {
        if(findNodeByPathInTree(row.path) != null){
            return;
        }
        String folderPath = getPathWithoutName(row.path);
        lazyCreateFolderPath(folderPath);
        FolderRow folder = (FolderRow) findNodeByPathInTree(folderPath);
        folder.children.add(row);
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
