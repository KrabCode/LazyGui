package lazy;

import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.HashMap;
import java.util.Map;

class InternalShaderStore {
    private static final Map<String, PShader> shaders = new HashMap<>();
    private static final String shaderFolder = "shaders/";

    private InternalShaderStore() {

    }

    static void filter(String path, PGraphics pg) {
        pg.filter(getShader(path));
    }

    static void shader(String path, PGraphics pg) {
        pg.shader(getShader(path));
    }

    static PShader getShader(String path) {
        String fullPath = shaderFolder + path;
        if(!shaders.containsKey(fullPath)) {
            shaders.put(fullPath, State.app.loadShader(fullPath));
        }
        return shaders.get(fullPath);
    }
}
