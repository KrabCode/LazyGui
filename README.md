# Lazy Gui

---

### Why?

**Problem**: You're making a processing sketch, and you want to tweak some values. But restarting the sketch slows you
down. So you use a processing GUI library, register its control elements in setup() and then ask for their values in
draw(). But now when you want to add a new control element you need to add code to two unrelated places. This slows you
down.

- **Solution**: Just ask for the values in draw() and have the GUI silently take care of the initialization and putting
  your control inside a window of related controls based on a string path you provide.

**Problem**: You just tweaked some values in your GUI, but now you need to change the code and restart the program. Your
GUI changes are lost forever.

- **Solution**: Save the GUI state as a JSON file. Load the most recently saved values at control element
  initialization. This allows you to seamlessly change your code and continue where you left off.

#### Other features:
- autosave GUI state on program exit
- hotkey based undo and redo
- pre-made and custom color themes

---

#### Faster iteration makes your life easier and your output prettier.

---

### How to use it?

First get the jar from [releases](https://github.com/KrabCode/LazyGui/releases) and drag & drop it into your Processing
editor window.

#### Initialize the GUI:

```java
LazyGui gui;

void setup(){
    size(800,800,P2D);
    gui=new LazyGui(this);
}

void draw(){
    background(100);
}
```
 Then during `draw()` you can do this:

#### Slider
```java
float x=gui.slider("x");
ellipse(x,height/2,50,50);
```

#### Button:

```java
if(gui.button("say hello once")){
    println("hello");
}
```

#### Toggle

```java
if(gui.toggle("spam every frame")){
    println("I'm trapped in a string factory")
}
```

#### Pick one option from a list

```java
String mode = gui.stringPicker("mode", new String[]{"square", "circle"});
if (mode.equals("square")) {
    rect(175, 175, 50, 50);
} else {
    ellipse(200, 200, 50, 50);
}
```

#### Color picker
```java
int pickedColor = gui.colorPicker("background").hex;
background(pickedColor);
```

#### Gradient picker
```java
PGraphics gradient = gui.gradient("background");
image(gradient, 0, 0);
```

### Folders

You may have noticed the string parameter to every control, I call that the **path** and it must be unique.
The forward slash `/` is a reserved character used to make folders.

#### Keep the sliders called "x" and "y" in a folder called "pos"
```java
float x = gui.slider("pos/x");
float y = gui.slider("pos/y");
``` 