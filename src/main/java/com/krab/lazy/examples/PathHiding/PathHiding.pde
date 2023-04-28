import com.krab.lazy.*;

LazyGui gui;

public void setup() {
  size(1200, 800, P2D);
  smooth(8);
  gui = new LazyGui(this);

  // hide the gui options if you don't need to see them
  gui.hide("options");

  textSize(64);
}

public void draw() {
    background(gui.colorPicker("background", color(50)).hex);
    drawText();
    drawRectangles();
}

private void drawText() {
    gui.pushFolder("text");
    boolean show = gui.toggle("editable?");
    String content = gui.text("content", "hello");
    text(content, 200, 600);

    // hide any single control element by path
    if (show) {
        gui.show("content");
    } else {
        gui.hide("content");
    }

    gui.popFolder();
}

void drawRectangles() {
    gui.pushFolder("rects");
    int maxRectCount = 20;
    int rectCount = gui.sliderInt("count", 10, 0, maxRectCount);

    for (int i = 0; i < maxRectCount; i++) {
        // make a dynamic list of rects each with its own folder
        gui.pushFolder("#" + i);

        if(i < rectCount){
            // show the current folder in case it was hidden
            gui.showCurrentFolder();
        }else{
            // this rect is over the rectCount limit, so hide its folder and skip drawing it
            gui.hideCurrentFolder();
            // shouldn't forget to pop out of the folder before 'continue' or 'return'
            gui.popFolder();
            continue;
        }
        PVector pos = gui.plotXY("pos", 600, 80 + i * 22);
        PVector size = gui.plotXY("size", 5);
        fill(gui.colorPicker("fill", color(200)).hex);
        noStroke();
        rect(pos.x, pos.y, size.x, size.y);
        gui.popFolder();
    }
    gui.popFolder();
}