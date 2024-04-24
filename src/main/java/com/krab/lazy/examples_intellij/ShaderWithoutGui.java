package com.krab.lazy.examples_intellij;

import com.krab.lazy.ShaderReloader;
import processing.core.PApplet;
import processing.opengl.PShader;

public class ShaderWithoutGui extends PApplet {

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
//        ShaderReloader.setApplet(this);
    }

    @Override
    public void draw() {
        String path = "shaders/testShader.glsl";
        PShader shader  = ShaderReloader.getShader(path);
        shader.set("time", millis() / 1000f);
        ShaderReloader.filter(path);
    }
}
