package toolbox.global;

import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.HashMap;
import java.util.Map;

public class InternalShaderStore {
    private static final Map<String, PShader> shaders = new HashMap<>();

    private InternalShaderStore() {

    }

    public static PShader getShader(String path) {
        if(!shaders.containsKey(path)) {
            shaders.put(path, State.app.loadShader(path));
        }
        return shaders.get(path);
    }

    public static  void filter(String path, PGraphics pg) {
        pg.filter(shaders.get(path));
    }

    public static void shader(String path, PGraphics pg) {
        pg.shader(shaders.get(path));
    }
}
