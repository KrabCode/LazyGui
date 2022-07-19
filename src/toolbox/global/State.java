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

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

import static processing.core.PApplet.*;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;

// TODO bug: set default value in java to something that compiles but freezes
//          - the value gets autosaved and cannot be overwritten from the java default anymore
//          - solution: remember the default when saving and ignore saved value if the new default is different
//          - similar problem: set current value to something that freezes - don't autosave when frozen

public class State {
    public static float cell = 22;
    public static PFont font = null;
    public static PApplet app = null;
    public static Gui gui = null;
    public static GLWindow window = null;
    public static PGraphics normalizedColorProvider = null;
    public static float textMarginX = 5;
    public static String sketchName = null;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    public static final float defaultWindowWidthInPixels = State.cell * 10;
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

        registerExitHandler();

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

    }

    private static void registerExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(State::createAutosave));
    }

    public static void createAutosave(){
        if(isSketchStuckInEndlessLoop()){
            println("NOT autosaving," +
                    " because the last frame took more than " + lastFrameMillisStuckLimit + " ms," +
                    " which looks like an endless loop due to bad settings");
            return;
        }
        createTreeSaveFile("autosave");
    }

    private static long lastFrameMillis;
    private static long lastFrameMillisStuckLimit = 1000;

    public static void updateSketchFreezeDetection(){
        lastFrameMillis = app.millis();
    }

    public static boolean isSketchStuckInEndlessLoop(){
        long timeSinceLastFrame = app.millis() - lastFrameMillis;
        return timeSinceLastFrame > lastFrameMillisStuckLimit;
    }

    public static void createTreeSaveFile(String filename) {
        overwriteFileWithCurrentState(getFullPathWithJsonSuffix(filename));
    }

    public static void loadMostRecentSave() {
        reloadSaveFolderContents();
        if(saveFilesSorted.size() > 0){
            loadStateFromJson(saveFilesSorted.get(0));
        }
    }

    private static void reloadSaveFolderContents() {
        File[] saveFiles = saveDir.listFiles();
        assert saveFiles != null;
        saveFilesSorted = new ArrayList<>(Arrays.asList(saveFiles));
        saveFilesSorted.removeIf(file -> !file.isFile());
        if (saveFilesSorted.size() == 0) {
            return;
        }
        saveFilesSorted.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
    }

    public static void loadStateFromFile(String filename) {
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(filename)) {
                loadStateFromJson(saveFile);
                return;
            }
        }
    }

    private static String readFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void renameFile(String oldName, String newName){
        if(oldName == null || newName == null){
            return;
        }
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(oldName)) {
                saveFile.renameTo(new File(getFullPathWithJsonSuffix(newName)));
                break;
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFile(String fileName) {
        String fullPath = getFullPathWithoutTypeSuffix(fileName);
        new File(fullPath).delete();
    }

    private static String getFullPathWithJsonSuffix(String filenameWithoutSuffix){
        return getFullPathWithoutTypeSuffix(filenameWithoutSuffix + ".json");
    }

    private static String getFullPathWithoutTypeSuffix(String filenameWithSuffix){
        return saveDir.getAbsolutePath() + "\\" + filenameWithSuffix;
    }

    public static void overwriteFileWithCurrentState(String fullPath) {
        String json = gson.toJson(NodeTree.getRoot());
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fullPath, false));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        println("Saved current state to: " + fullPath);
    }

    public static ArrayList<File> getSaveFileList() {
        reloadSaveFolderContents();
        return saveFilesSorted;
    }

    public static void loadStateFromJson(File jsonToLoad) {
        if(!jsonToLoad.exists()){
            println("Error: save file doesn't exist");
            return;
        }
        String json;
        try {
            json = readFile(jsonToLoad);
        } catch (IOException e) {
            println("Error loading state from file", e.getMessage());
            return;
        }
        // don't delete or do anything to the existing nodes, just overwrite their values if they exist
        JsonElement loadedRoot = gson.fromJson(json, JsonElement.class);
        lastLoadedStateMap.clear();
        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(loadedRoot);
        while (!queue.isEmpty()) {
            JsonElement loadedNode = queue.poll();
            String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
            AbstractNode nodeToEdit = NodeTree.findNode(loadedPath);
            if (nodeToEdit != null) {
                overwriteWithLoadedStateIfAny(nodeToEdit, loadedNode);
            }
            lastLoadedStateMap.put(loadedPath, loadedNode);
            String loadedType = loadedNode.getAsJsonObject().get("type").getAsString();
            if (Objects.equals(loadedType, NodeType.FOLDER.toString())) { // TODO bad
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
