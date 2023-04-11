package com.krab.lazy;

import com.krab.lazy.stores.GlobalReferences;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static processing.core.PApplet.println;

/**
 * Utility class for live-coding shaders
 * Takes a string path to the shader and re-compiles it when needed using the shader file's last modified time.
 * Saving the shader file in your text editor is needed to actually recompile it and display the results.
 */
@SuppressWarnings("unused")
public class ShaderReloader {
    private static final ArrayList<ShaderSnapshot> snapshots = new ArrayList<>();
    private static final int shaderRefreshRateInMillis = 36;

    private ShaderReloader() {

    }

    /**
     * Gets the current snapshot of a vertex + fragment shader for uniform setting purposes.
     * Only attempts to compile the first time it's called during lazy initialization,
     * then it relies on the user calling ShaderReloader.filter() or ShaderReloader.shader() to attempt to re-compile.
     *
     * @param fragPath path to the shader, either absolute or relative from the data folder
     * @return PShader to set uniforms on
     */
    public static PShader getShader(String fragPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, null);
        snapshot = initIfNull(snapshot, fragPath, null);
        return snapshot.compiledShader;
    }

    /**
     * Gets the current snapshot of a vertex + fragment shader for uniform setting purposes.
     * Only attempts to compile at lazy initialization time,
     * it relies on the user calling ShaderReloader.filter() or ShaderReloader.shader() to attempt to re-compile.
     *
     * @param fragPath path to the fragment shader, either absolute or relative from the data folder
     * @param vertPath path to the vertex shader, either absolute or relative from the data folder
     * @return PShader to set uniforms on
     */
    public static PShader getShader(String fragPath, String vertPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, vertPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        return snapshot.compiledShader;
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing filter() on the main canvas.
     *
     * @param fragPath path to fragment shader
     */
    public static void filter(String fragPath) {
        shader(fragPath, null, true, GlobalReferences.app.g);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing filter() on the parameter canvas.
     *
     * @param fragPath path to fragment shader
     * @param pg canvas to apply the filter on
     */
    public static void filter(String fragPath, PGraphics pg) {
        shader(fragPath, null, true, pg);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing filter() on the parameter canvas.
     *
     * @param fragPath path to fragment shader
     * @param vertPath path to fragment shader
     * @param pg canvas to apply the filter on
     */
    public static void filter(String fragPath, String vertPath, PGraphics pg) {
        shader(fragPath, vertPath, true, pg);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing shader() on the main canvas.
     *
     * @param fragPath path to fragment shader
     */
    public static void shader(String fragPath) {
        shader(fragPath, null, false, GlobalReferences.app.g);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing shader() on the main canvas.
     *
     * @param fragPath path to fragment shader
     * @param vertPath path to vertex shader
     */
    public static void shader(String fragPath, String vertPath) {
        shader(fragPath, vertPath, false, GlobalReferences.app.g);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing shader() on the parameter canvas.
     *
     * @param fragPath path to fragment shader
     * @param pg canvas to apply the shader on
     */
    public static void shader(String fragPath, PGraphics pg) {
        shader(fragPath, null, false, pg);
    }

    /**
     * Re-compiles the shader if needed and then applies it as a processing shader() on the parameter canvas.
     *
     * @param fragPath path to fragment shader
     * @param vertPath path to fragment shader
     * @param pg canvas to apply the shader on
     */
    public static void shader(String fragPath, String vertPath, PGraphics pg) {
        shader(fragPath, vertPath, false, pg);
    }

    private static void shader(String fragPath, String vertPath, boolean filter, PGraphics pg) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, vertPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        snapshot.update(filter, pg);
    }

    private static ShaderSnapshot initIfNull(ShaderSnapshot snapshot, String fragPath, String vertPath) {
        if (snapshot == null) {
            snapshot = new ShaderSnapshot(fragPath, vertPath);
            snapshots.add(snapshot);
        }
        return snapshot;
    }

    private static ShaderSnapshot findSnapshotByPath(String fragPathQuery, String vertPathQuery) {
        for (ShaderSnapshot snapshot : snapshots) {
            if (vertPathQuery != null) {
                if (snapshot.vertPath.equals(vertPathQuery) && snapshot.fragPath.equals(fragPathQuery)) {
                    return snapshot;
                }
            } else {
                if (snapshot.fragPath.equals(fragPathQuery)) {
                    return snapshot;
                }
            }
        }
        return null;
    }

    private static class ShaderSnapshot {
        final String fragPath;
        final String vertPath;
        final File fragFile;
        File vertFile;
        PShader compiledShader;
        long fragLastKnownModified, vertLastKnownModified, lastChecked;
        boolean compiledOk = false;
        long lastKnownUncompilable = -shaderRefreshRateInMillis;

        ShaderSnapshot(String fragPath, String vertPath) {
            if (vertPath != null) {
                compiledShader = GlobalReferences.app.loadShader(fragPath, vertPath);
                vertFile = GlobalReferences.app.dataFile(vertPath);
                vertLastKnownModified = vertFile.lastModified();
                if (!vertFile.isFile()) {
                    println("Could not find shader at " + vertFile.getPath());
                }
            } else {
                compiledShader = GlobalReferences.app.loadShader(fragPath);
            }
            fragFile = GlobalReferences.app.dataFile(fragPath);
            if (!fragFile.isFile()) {
                println("Could not find shader at " + fragFile.getPath());
            }
            fragLastKnownModified = fragFile.lastModified();
            this.fragPath = fragPath;
            this.vertPath = vertPath;
            tryCompileNewVersion(currentTimeMillis());
            lastChecked = currentTimeMillis();
        }

        void update(boolean shaderMode, PGraphics pg) {
            long currentTimeMillis = currentTimeMillis();
            long lastModified = fragFile.lastModified();
            if (vertFile != null) {
                lastModified = max(lastModified, vertFile.lastModified());
            }
            if (compiledOk && currentTimeMillis < lastChecked + shaderRefreshRateInMillis) {
//                println("working shader did not change, not checking, standard apply");
                applyShader(compiledShader, shaderMode, pg);
                return;
            }
            if (!compiledOk && lastModified > lastKnownUncompilable) {
//                println("file changed, trying to compile");
                tryCompileNewVersion(lastModified);
                return;
            }
            lastChecked = currentTimeMillis;
            if (lastModified > fragLastKnownModified && lastModified > lastKnownUncompilable) {
//                println("file changed, repeat try");
                tryCompileNewVersion(lastModified);
            } else if (compiledOk) {
//                println("file didn't change, standard apply");
                applyShader(compiledShader, shaderMode, pg);
            }
        }

        private void applyShader(PShader shader, boolean shaderMode, PGraphics pg) {
            if (shaderMode) {
                pg.filter(shader);
            } else {
                pg.shader(shader);
            }
        }

        private void tryCompileNewVersion(long lastModified) {
            try {
                PShader candidate;
                if (vertFile == null) {
                    candidate = GlobalReferences.app.loadShader(fragPath);
                } else {
                    candidate = GlobalReferences.app.loadShader(fragPath, vertPath);
                }
                candidate.init();
                compiledShader = candidate;
                compiledOk = true;
                fragLastKnownModified = lastModified;
                println("Compiled shader from:", fragPath != null ? fragPath : "",
                        vertPath != null ? vertPath : "");
            } catch (Exception ex) {
                lastKnownUncompilable = lastModified;
                System.err.println((fragPath != null ? fragPath : "") +
                        (vertPath != null ? " or " + (vertFile != null ? vertPath : null) : "") + "says: ");
                System.err.println(ex.getMessage());
            }
        }

    }
}