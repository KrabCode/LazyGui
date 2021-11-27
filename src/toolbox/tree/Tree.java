package toolbox.tree;

import toolbox.tree.rows.FolderRow;
import toolbox.tree.rows.Row;
import toolbox.tree.rows.RowType;

import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;

public class Tree {
    public String name;
    public FolderRow root = new FolderRow("", null);

    public Tree(String name) {
        this.name = name;
    }

    public Row getLazyInitParentFolderByPath(String rowPath){
        String folderPath = getPathWithoutName(rowPath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public Row findNodeByPathInTree(String path) {
        Queue<Row> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Row row = queue.poll();
            if (row.path.equals(path)) {
                return row;
            }
            if (row.type == RowType.FOLDER) {
                FolderRow folder = (FolderRow) row;
                for (Row child : folder.children) {
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
            Row n = findNodeByPathInTree(runningPath);
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

    public void insertNodeAtItsPath(Row row) {
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
