package toolbox.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PSurface;
import toolbox.Gui;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

import static processing.core.PApplet.*;
import static processing.core.PApplet.second;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;


public class State {
    public static float cell = 24;
    public static PFont font = null;
    public static PApplet app = null;
    public static Gui gui = null;
    public static Robot robot = null;
    public static GLWindow window = null;
    public static PGraphics normalizedColorProvider = null;
    public static float textMarginX = 5;
    public static String sketchName = null;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    public static int clipboardHex = 0;
    public static final float windowWidth = cell * 8;
    private static ArrayList<File> saveFilesSorted;
    static Map<String, JsonElement> lastLoadedStateMap = new HashMap<>();
    public static File saveDir;


    public static void init(Gui gui, PApplet app) {
        State.gui = gui;
        State.app = app;
//        printAvailableFonts();
        try {
            State.font = app.createFont("Calibri", 20);
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
            }
        }

        sketchName = app.getClass().getSimpleName();
        saveDir = new File(State.app.sketchPath() + "/saves/" + sketchName);
        println("Save folder path: " + saveDir.getAbsolutePath());
        if (!saveDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            saveDir.mkdirs();
        }

        normalizedColorProvider = app.createGraphics(256, 256, P2D);
        normalizedColorProvider.colorMode(HSB, 1, 1, 1, 1);

        PSurface surface = State.app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    private static void printAvailableFonts() {
        String[] fontList = PFont.list();
        for (String s :
                fontList) {
            println(s);
        }
    }

    public static String timestamp() {
        return year() + "-"
                + nf(month(), 2) + "-"
                + nf(day(), 2) + "_"
                + nf(hour(), 2) + "."
                + nf(minute(), 2) + "."
                + nf(second(), 2);
    }

    public static void createTreeSaveFile() {
        String json = gson.toJson(NodeTree.getRoot());
        BufferedWriter writer;
        String timestamp = timestamp();
        String filePath = saveDir.getAbsolutePath() + "/" + timestamp + ".json";
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveFilesSorted.add(0, new File(filePath));
    }

    public static void loadMostRecentSave() {
        File[] saveFiles = saveDir.listFiles();
        assert saveFiles != null;
        saveFilesSorted = new ArrayList<>(java.util.List.of(saveFiles));
        saveFilesSorted.removeIf(file -> !file.isFile());
        if (saveFilesSorted.size() == 0) {
            return;
        }
        saveFilesSorted.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
        loadStateFromJson(saveFilesSorted.get(0));
    }

    public static void loadStateFromFile(String filename) {
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(filename)) {
                loadStateFromJson(saveFile);
                return;
            }
        }
    }

    private static String readFile(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        assert lines != null;
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static ArrayList<File> getSaveFileList() {
        return saveFilesSorted;
    }

    public static void loadStateFromJson(File jsonToLoad) {
        String json = readFile(jsonToLoad);
        // don't delete or do anything to the existing nodes, just overwrite their values if they exist
        JsonElement loadedRoot = gson.fromJson(json, JsonElement.class);
        lastLoadedStateMap.clear();
        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(loadedRoot);
        while (!queue.isEmpty()) {
            JsonElement loadedNode = queue.poll();
            String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
            AbstractNode nodeToEdit = NodeTree.findNodeByPathInTree(loadedPath);
            if (nodeToEdit != null) {
                overwriteWithLoadedStateIfAny(nodeToEdit, loadedNode);
            }
            lastLoadedStateMap.put(loadedPath, loadedNode);
            String loadedType = loadedNode.getAsJsonObject().get("type").getAsString();
            if (Objects.equals(loadedType, NodeType.FOLDER.toString())) {
                JsonArray loadedChildren = loadedNode.getAsJsonObject().get("children").getAsJsonArray();
                for (JsonElement child : loadedChildren) {
                    queue.offer(child);
                }
            }
        }
    }

    public static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode) {
        overwriteWithLoadedStateIfAny(abstractNode, lastLoadedStateMap.get(abstractNode.path));
    }

    public static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode, JsonElement loadedNodeState) {
        if (loadedNodeState == null) {
            return;
        }
        abstractNode.overwriteState(loadedNodeState);
    }
}
