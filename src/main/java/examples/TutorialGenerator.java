package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class TutorialGenerator extends PApplet {
    LazyGui gui;
    PGraphics pg;
    int recStarted = -1;
    int saveIndex = 1;
    String recordingId = generateRandomShortId();
    private PImage pointer;


    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
        noSmooth();
    }

    public void setup() {
        pointer = loadImage("C:\\Users\\Krab\\Documents\\GitHub\\LazyGui\\readme_assets\\cursor.png");
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        clear();
        noCursor();
        drawScene();
        gui.draw();
        image(pointer, mouseX + gui.slider("pointer/y"), mouseY + gui.slider("pointer/x"));
        record();
    }

    private void drawScene() {
        pg.beginDraw();
        pg.background(gui.colorPicker("background").hex);
        PVector offset = gui.plotXY("circle/translate");
        pg.fill(gui.colorPicker("circle/fill").hex);
        pg.stroke(gui.colorPicker("circle/stroke").hex);
        pg.strokeWeight(gui.slider("circle/weight", 3));
        pg.pushMatrix();
        pg.translate(width / 2f + offset.x, width / 2f + offset.y);
        pg.ellipse(0, 0, 50, 50);
        pg.popMatrix();
        pg.endDraw();
        image(pg, 0, 0, width, height);
    }


    public String generateRandomShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public void record() {
        gui.pushFolder("rec");
        int recLength = gui.sliderInt("frames", 600);
        if (gui.button("start")) {
            recordingId = generateRandomShortId();
            recStarted = frameCount;
            saveIndex = 1;
        }
        boolean stopCommand = gui.button("stop");
        if (stopCommand) {
            recStarted = -1;
        }

        String sketchMainClassName = getClass().getSimpleName();
        String recDir = "out/rec/" + sketchMainClassName + "_" + recordingId;
        String recDirAbsolute = Paths.get(recDir).toAbsolutePath().toString();
        if (gui.button("open folder")) {
            Desktop desktop = Desktop.getDesktop();
            File dir = new File(recDirAbsolute + "\\");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                desktop.open(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int recordRectPosX = width / 2 + gui.sliderInt("rect center x", 0);
        int recordRectPosY = height / 2 + gui.sliderInt("rect center y", 0);
        int recordRectSizeX = gui.sliderInt("rect size x", height / 2);
        int recordRectSizeY = gui.sliderInt("rect size y", height / 2);
        if (recordRectSizeX % 2 != 0) {
            recordRectSizeX += 1;
        }
        if (recordRectSizeY % 2 != 0) {
            recordRectSizeY += 1;
        }
        String recImageFormat = ".jpg";
        if (recStarted != -1 && frameCount < recStarted + recLength) {
            println("saved " + saveIndex + " / " + recLength);
            PImage cutout = get(
                    recordRectPosX - recordRectSizeX / 2,
                    recordRectPosY - recordRectSizeY / 2,
                    recordRectSizeX,
                    recordRectSizeY
            );
            cutout.save(recDir + "/" + saveIndex++ + recImageFormat);
        }
        if (stopCommand || (recStarted != -1 && frameCount == recStarted + recLength)) {
            println("Recorded image series folder: " + recDirAbsolute);
        }
        if (gui.toggle("show rect")) {
            pushStyle();
            stroke(color(0xFFFFFFFF));
            noFill();
            rectMode(CENTER);
            rect(recordRectPosX, recordRectPosY, recordRectSizeX, recordRectSizeY);
            popStyle();
        }

        int ffmpegFramerate = gui.sliderInt("ffmpeg fps", 60, 1, Integer.MAX_VALUE);
        if (gui.button("ffmpeg make mp4")) {
            String outMovieFilename = recDirAbsolute + "/_" + generateRandomShortId();
            String inputFormat = recDirAbsolute + "/%01d" + recImageFormat;
            String command = String.format("ffmpeg  -r " + ffmpegFramerate + " -i %s -start_number_range 100000 -an %s.mp4",
                    inputFormat, outMovieFilename);
            println("running ffmpeg: " + command);
            try {
                Process proc = Runtime.getRuntime().exec(command);
                new Thread(() -> {
                    Scanner sc = new Scanner(proc.getErrorStream());
                    while (sc.hasNextLine()) {
                        println(sc.nextLine());
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        gui.popFolder();
    }
}
