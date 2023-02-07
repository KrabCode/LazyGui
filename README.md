# LazyGUI
### easy to code GUI for Processing 3+

![LazyGui looks like this](readme_assets/header.png)
---

## What?

**Problem**: You're making a processing sketch, and you want to tweak some values. But restarting the sketch slows you
down. So you use one of the dozen other processing GUI libraries, register its control elements in `setup()` and then ask for their values in
`draw()`. But now when you want to add a new control element you need to add code to two unrelated places. This slows you
down.

- **Solution**: Do not mention any control elements in `setup()`. Only ask for their values in `draw()` using unique paths and have the GUI seamlessly initialize your control element when needed.

**Problem**: You just tweaked some values in your GUI, but now you need to change the code and restart the program. Your GUI changes are lost forever.

- **Solution**: Save the GUI state as a JSON file. Load the most recently saved values at control element initialization. This allows you to seamlessly change your code and continue where you left off.

#### Other features:
- infinite sliders with variable precision
- keyboard input for text and slider values
- copy / paste any value or folder
- undo and redo recent changes with hotkeys
- autosave on program exit
- configurable look and feel
  - pre-made and custom color themes
  - custom fonts (JetBrains Mono by default)
  - background dot grid for windows to snap to
  - context guide lines between a child folder and its parent
  - individual windows have resizable width 
---

## How?

First get the jar from [releases](https://github.com/KrabCode/LazyGui/releases) and then drag & drop it into your Processing
editor window. If you are using a full IDE like IntelliJ, import the jar as a standard java library just like you imported Processing.

### Initialize the GUI in `setup()`:

```java
LazyGui gui;

void setup(){
    size(800,800,P2D);
    gui = new LazyGui(this);
}
```

### Get values from the GUI

#### Slider
```java
float x = gui.slider("x");
ellipse(x, height/2, 50, 50);
```
- mouse wheel changes the selected precision
- click and drag mouse - change value by pixels moved times precision
- keyboard input ints or floats with mouse over the slider

#### Button

```java
if(gui.button("say hello once")){
    println("hello");
}
```
- is only true once after being clicked - returning true switches the value back to false

#### Toggle

```java
if(gui.toggle("spam every frame")){
    println("I'm trapped in a string factory");
}
```
- click to change boolean state n to !n

#### Text input

```java
String userInput = gui.text("text header", "this default text can be edited");
  text(userInput, width/2, height/2);
```
- type with mouse over the text field
- ENTER - insert new line 
- DELETE  - delete the whole text
- BACKSPACE - delete the last character

#### Pick one option from a list

```java
String mode = gui.stringPicker("mode", new String[]{"square", "circle"});
if (mode.equals("square")) {
    rect(175, 175, 50, 50);
} else {
    ellipse(200, 200, 50, 50);
}
```
- opens a window of toggles named after the options where setting one to true sets all others to false

#### Color picker

```java
int pickedColor = gui.colorPicker("background").hex;
background(pickedColor);
```
- HSBA color picker with a hex string display
- you can copy and paste using the hex field

#### Gradient picker

```java
PGraphics gradient = gui.gradient("background");
image(gradient, 0, 0);
```
- allows you to set the position and value of individual colors or disable them entirely
- blend type supports three color mixing algorithms (mix, rgb, hsv - see [gradient.glsl](data/shaders/gradient.glsl))

## Path

The **path**  is the first string parameter to every control element function and it must be unique.
It exists only in memory to inform the GUI - it's not a directory structure in any file storage.
The forward slash `/` is a reserved character used to make folders, but it can be escaped with `\\` like this: `\\/` which won't separate folders.

#### Keep the sliders called "x" and "y" in a folder called "pos" inside another folder called "test"

```java
float x = gui.slider("pos/x");
float y = gui.slider("pos/y");
```

### Global path prefix stack

Repeating the whole path in every control element call can get tiresome, especially with multiple nested levels.
Which is why there is a helpful path stack that you can interact with using pushFolder() and popFolder().

Just like using pushMatrix() and popMatrix() in Processing, you can change your "current directory"
by pushing a new folder name to a stack with gui.pushFolder("folder name") and have every control element called after that be placed into that folder automatically

- as if the contents of the whole current stack got prefixed to the path parameter in every control element call.

popFolder() doesn't have a parameter - it just goes up by one level

You can nest a pushFolder() inside another pushFolder() - your path stack can be many levels deep.
Just remember to call popFolder() the same number of times when done!

#### Keep the sliders called "x" and "y" in a folder called "pos" by using the stack

```java
gui.pushFolder("pos");
float x = gui.slider("x");
float y = gui.slider("y");
gui.popFolder();
```

