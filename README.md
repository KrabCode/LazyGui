## LazyGUI is on a mission to make iterating in processing a joyful experience

### Why?
Because optimizing your iteration loop makes your life easier and your output prettier.

---

### What?

**Problem**: You're making a processing sketch, and you want to tweak some values. But restarting the sketch slows you
down. So you use one of the dozen other processing GUI libraries, register its control elements in setup() and then ask for their values in
draw(). But now when you want to add a new control element you need to add code to two unrelated places. This slows you
down.

- **Solution**: Do not mention control elements in setup(). Ask for their values in draw() using unique paths and have the GUI silently take care of the initialization and placing your control element.

**Problem**: You just tweaked some values in your GUI, but now you need to change the code and restart the program. Your GUI changes are lost forever.

- **Solution**: Save the GUI state as a JSON file. Load the most recently saved values at control element initialization. This allows you to seamlessly change your code and continue where you left off.

#### Other features:
- autosave GUI state on program exit
- hotkey based undo and redo
- pre-made and custom color themes

---

### How?

First get the jar from [releases](https://github.com/KrabCode/LazyGui/releases) and drag & drop it into your Processing
editor window. If you are using a full fledged IDE simply import the jar as a library.

#### Initialize the GUI:
This initializes the GUI in `setup()` and displays it every time `draw()` ends.
```java
LazyGui gui;

void setup(){
    size(800,800,P2D);
    gui = new LazyGui(this);
}

void draw(){
    background(100);
}
```
 After you create the LazyGui object you can use it to ask for values of control elements at their unique paths:

#### Slider
```java
float x = gui.slider("x");
ellipse(x, height/2, 50, 50);
```

#### Button

```java
if(gui.button("say hello once")){
    println("hello");
}
```

#### Toggle

```java
if(gui.toggle("spam every frame")){
    println("I'm trapped in a string factory");
}
```

#### Text input

```java
  String userInput = gui.text("text header", "this default text can be edited");
  text(userInput, width/2, height/2);
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

### Path

The **path**  is the first string parameter to every control element function and it must be unique.
It exists only in memory to inform the GUI - it's not a directory structure in any file storage.
The forward slash `/` is a reserved character used to make folders, but it can be escaped with '\\' like this: '\\/' which won't separate folders.

#### Keep the sliders called "x" and "y" in a folder called "pos"
```java
float x = gui.slider("pos/x");
float y = gui.slider("pos/y");
```

#### Global path prefix stack

Repeating the whole path in every control element call can get tiresome, especially with multiple nested levels.
Which is why there is a helpful path stack that you can interact with using pushFolder() and popFolder().

Just like using pushMatrix() and popMatrix() in Processing, you can change your "current directory"
by pushing a new folder name to a stack with gui.pushFolder("folder name") and have every control element called after that be placed into that folder automatically
    - as if the contents of the whole current stack got prefixed to the path parameter in every control element call.

popFolder() doesn't have a parameter - it just goes up by one level

You can nest a pushFolder() inside another pushFolder() - your path stack can be many levels deep.
Just remember to call popFolder() the same number of times when done!

#### Same result as the last code example, only using pushFolder() and popFolder() instead of spelling out the whole path every time
```java
gui.pushFolder("pos");
float x = gui.slider("x");
float y = gui.slider("y");
gui.popFolder();
```
