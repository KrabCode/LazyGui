package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.ShaderReloader;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class ShaderTest extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
        colorMode(HSB,1,1,1,1);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        drawBackground();
        drawShader();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }

    private void drawShader() {
        gui.pushFolder("shader uniforms");
        String shaderPath = gui.text("shader path", "shaders/testShader.glsl");
        PShader shader = ShaderReloader.getShader(shaderPath);
        shader.set("time", radians(frameCount));
        setUniforms(shader, UNIFORM_TYPE.INTEGER);
        setUniforms(shader, UNIFORM_TYPE.FLOAT);
        setUniforms(shader, UNIFORM_TYPE.VECTOR);
        setUniforms(shader, UNIFORM_TYPE.COLOR);
        setUniforms(shader, UNIFORM_TYPE.SAMPLER);
        ShaderReloader.filter(shaderPath, pg);
        gui.popFolder();
    }

    enum UNIFORM_TYPE{
        INTEGER,
        FLOAT,
        VECTOR,
        COLOR,
        SAMPLER
    }

    private void setUniforms(PShader shader, UNIFORM_TYPE type) {
        gui.pushFolder(type.name().toLowerCase() + "s");
        int maxSliderCount = 20;
        boolean addNew = gui.button("add new");
        int sliderCount = gui.sliderInt("count");
        if(addNew){
            gui.sliderSet("count", sliderCount + 1);
        }
        for (int i = 0; i < maxSliderCount; i++) {
            gui.pushFolder(type.name().toLowerCase() + " " + i);
            if(i < sliderCount){
                gui.showCurrentFolder();
            }else{
                gui.hideCurrentFolder();
                gui.popFolder();
                continue;
            }
            String name = gui.text("name");
            switch(type){
                case INTEGER:
                    shader.set(name, gui.sliderInt("value"));
                    break;
                case FLOAT:
                    shader.set(name, gui.slider("value"));
                    break;
                case VECTOR:
                    shader.set(name, gui.plotXYZ("value"));
                    break;
                case COLOR:
                    int hex = gui.colorPicker("value").hex;
                    shader.set(name, red(hex), green(hex), blue(hex), alpha(hex));
                    break;
                case SAMPLER:
                    shader.set(name, gui.gradient("value"));
                    break;
            }
            gui.popFolder();
        }
        gui.popFolder();
    }
}
