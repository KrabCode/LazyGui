![LazyGui header](https://user-images.githubusercontent.com/25923016/229351039-be75376e-ee45-4a2b-905d-392ad2a289d4.png)

Table of Contents
<!-- TOC -->
  * [LazyGui is a GUI library for Processing](#lazygui-is-a-gui-library-for-processing)
  * [How do I run this?](#how-do-i-run-this)
      * [Minimal example](#minimal-example)
  * [Control elements](#control-elements)
    * [Slider](#slider)
    * [Plot](#plot)
    * [Color picker](#color-picker)
    * [Gradient picker](#gradient-picker)
    * [Button](#button)
    * [Toggle](#toggle)
    * [Text input](#text-input)
    * [Radio](#radio)
  * [Hotkeys](#hotkeys)
  * [Mouse interaction](#mouse-interaction)
  * [Saving and loading values](#saving-and-loading-values)
    * [Saving](#saving)
    * [Loading](#loading)
  * [Paths and folders](#paths-and-folders)
    * [Creating a folder with the forward slash](#creating-a-folder-with-the-forward-slash)
    * [Escaping the forward slash](#escaping-the-forward-slash)
    * [Global path prefix stack](#global-path-prefix-stack)
      * [Folder made by using the stack](#folder-made-by-using-the-stack)
      * [See the current stack for debugging](#see-the-current-stack-for-debugging)
    * [Hide and show anything](#hide-and-show-anything)
    * [Folder visuals](#folder-visuals)
  * [Constructor settings](#constructor-settings)
  * [Live shader reloading](#live-shader-reloading)
  * [Dependencies](#dependencies)
  * [Further reading](#further-reading)
  * [How to contribute](#how-to-contribute)
<!-- TOC -->

## LazyGui is a GUI library for Processing

**Main ideas**
- no need to register your windows or controls in `setup()`
- ask for values in `draw()` at a unique path, which will
  - *lazily* initialize a control element
  - place it in a window hierarchy
  - return its current value

**Quality of life features**
- very customizable look and feel
- mouse or keyboard value input
- [save / load](#saving-and-loading-values) your gui state as json files
    - autosave on program exit
    - autoload on program start
- [hotkeys](#hotkeys) for common actions
    - copy / paste any value or folder
    - undo / redo any change
- [reloading shaders](#live-shader-reloading) at runtime
  
## How do I run this?

First get the jar from [releases](https://github.com/KrabCode/LazyGui/releases) and then drag & drop it into your Processing
editor window. If you are using a full IDE like IntelliJ, import the jar as a standard java library just like you imported Processing.

#### Minimal example
```java
LazyGui gui;

void setup(){
    size(800,800,P2D);
    gui = new LazyGui(this);
}

void draw(){
    background(gui.colorPicker("background").hex);
}
```
![root and options look like this](https://user-images.githubusercontent.com/25923016/229351048-1fdd04c2-1634-41f3-b29b-264a58c67709.png)

The gui displays itself at the end of `draw()` and by default it shows a root folder that can't be closed with two built in folders
- **options** for tweaking the various gui settings
- **saves** for managing your save files. 

## Control elements
- getters and setters initialize controls when first called
- subsequent calls at the same [path](#paths-and-folders) use the existing control element
- optional default parameters are used when first called and then ignored
- visually, rows of controls are ordered by when they were first initialized

### Slider
![a slider looks like this](https://user-images.githubusercontent.com/25923016/229161773-5be9b0fa-3c15-4b9c-bebe-f428990a0111.gif)
```java
// simplest getter for an infinite slider
float x = gui.slider("x");

// alternative getters that specify defaults and constraints
gui.slider("x", defaultFloat);
gui.slider("x", defaultFloat, minimumFloat, maximumFloat);

// setters
gui.sliderAdd("x", floatToAdd);
gui.sliderSet("x", floatToSet);
```
- mouse wheel changes the selected precision when mouse is over the slider
- click and drag mouse horizontally - change value by (pixels * precision)
- supports keyboard input with mouse over the slider - tries to parse the string as Float or Int
- there is a `sliderInt()` alternative that uses and returns `int`

### Plot
![a plot looks like this](https://user-images.githubusercontent.com/25923016/229208020-4557e5fa-d726-4add-aa13-9fced1451373.gif)
```java
// simplest getter
PVector pos = gui.plotXY("position");

// alternative getters that specify defaults
gui.plotXY("position", defaultFloatXYZ);
gui.plotXY("position", defaultFloatX, defaultFloatY);
gui.plotXY("position", defaultPVector);

//setters
gui.plotSet("position", valueFloat);
gui.plotSet("position", valueFloatX, valueFloatY);
gui.plotSet("position", valuePVector);
```
- drag the grid with your mouse to change both X and Y at the same time
- keyboard input for both values with mouse over the grid
- change both of their precisions at the same time with the mouse wheel over the grid
  - change just one of their precisions with mouse over one of the x,y sliders
- there is a `plotXYZ()` variant with an extra Z slider (not connected to the grid)

### Color picker
![a color picker looks like this](https://user-images.githubusercontent.com/25923016/229208014-0a79609c-41ec-4ddb-9861-d67a83294d46.gif)
```java
// simplest getter
PickerColor myColor = gui.colorPicker("background");
// use it with .hex in place of color
background(myColor.hex);

// alternative getters that specify the default color
gui.colorPicker("background", color(36));
gui.colorPicker("background", grayNorm); // 'norm' meaning float in the range [0, 1]
gui.colorPicker("background", hueNorm, saturationNorm, brightnessNorm);
gui.colorPicker("background", hueNorm, saturationNorm, brightnessNorm, alphaNorm);

// setters
gui.colorPickerSet("background", color(36));
gui.colorPickerHueAdd("background", hueToAdd);
```
- HSBA color picker with a hex string display
- returns a read-only PickerColor object with an integer 'hex' field 
  - this hex integer is the same thing as the Processing [color datatype](https://processing.org/reference/color_datatype.html)
  - displays the correct color in any Processing [color mode](https://processing.org/reference/colorMode_.html)
- paste in values from sites like [colorhunt.co](https://colorhunt.co/)
- copy and paste the hex value with mouse over the desired color row / preview / hex string

### Gradient picker
![gradient pickers also look like this](https://user-images.githubusercontent.com/25923016/229208017-99699e27-dbb7-4054-ac21-a8fe16156868.gif)
```java
// simple getter
PGraphics bgGradient = gui.gradient("background gradient");
image(bgGradient, 0, 0);

// alternative getter that specifies default color(s)
gui.gradient("name", color(255,0,150));
gui.gradient("name", new int[]{color(255,0,150), color(0,150,0), color(0,100,150)});

// special getter for a color inside the gradient at a position in range [0, 1]
// faster than texture.get(x, y) thanks to a color look up table
PickerColor myColor = gui.gradientColorAt("name", positionNorm);
```
- allows you to set the position and value of individual colors and get the result as a PGraphics
- output texture size is kept equal to main sketch size


### Button
![a button looks like this](https://user-images.githubusercontent.com/25923016/229208008-04dec541-c3d5-4f77-bd3f-ff824d64d395.gif)
```java
// getter that is only true once after being clicked and then switches to false 
if(gui.button("do the thing!")){
    println("it is done");
}
```

### Toggle
![a toggle looks like this](https://user-images.githubusercontent.com/25923016/229208032-0e7286a0-342e-4317-8728-5d214b25d187.gif)
- click to flip the boolean state
- off by default

```java
// simple getter
boolean isToggledOn = gui.toggle("spam every frame")
if(isToggledOn){
    println("I'm trapped in a string factory");
}

// alternative getter that specifies a default
gui.toggle("spam every frame", booleanDefault)
        
// setter
gui.toggleSet("spam every frame", booleanValue)
```

### Text input
![text input looks like this](https://user-images.githubusercontent.com/25923016/229208031-40950a44-2be2-42fd-aee9-f72ef0d341fe.gif)
```java
// simple getter
String userInput = gui.text("text header");

// getter that specifies a default content
gui.text("text header", "this default text can be edited");
gui.text("", "this will rename its parent folder"); 

// one time setter that also blocks any interaction when called every frame
textSet("text header", "content")
```
| Mouse Hotkey | Action under mouse    |
|--------------|-----------------------|
| Enter        | insert new line       |
| Delete       | delete entire string  |
| Backspace    | delete last character |
- typing with mouse over the text appends to its last line
- see [folder visuals](#folder-visuals) on how to rename the parent folder at runtime using this text control with a specific path
- the text editor is pretty basic, but you can paste the text into it from elsewhere

### Radio
![radio looks like this](https://user-images.githubusercontent.com/25923016/229208026-bf14f362-8eed-493c-81bd-a3a83c1d170c.gif)
```java
// simplest getter
String mode = gui.radio("mode", new String[]{"square", "circle"});
if (mode.equals("square")) {
    rect(175, 175, 50, 50);
} else {
    ellipse(200, 200, 50, 50);
}

// getter that specifies a default
gui.radio("mode", stringArray, defaultOption);

// setter
gui.radioSet("mode", "square");
```
- opens a folder of toggles where setting one to true sets all others to false
- returns the selected option as a string
- any changes to the available options will be ignored after the radio is first initialized
- instead of the `String[]` array of options you can also use `List<String>` or `ArrayList<String>`

## Hotkeys
|   Global hotkey   | Action                                 |     
|:-----------------:|:---------------------------------------|
|         H         | Hide GUI / Show GUI                    |     
|         D         | Close windows                          |     
|         I         | Save screenshot                        | 
|     CTRL + Z      | Undo                                   | 
|     CTRL + Y      | Redo                                   |        
|     CTRL + S      | [New save](#saving-and-loading-values) |   

| Mouse hotkey | Action on element under mouse |
|:------------:|:------------------------------|
| Right click  | Close window                  |
|      R       | Reset value to default        |
|   CTRL + C   | Copy value or folder          |
|   CTRL + V   | Paste to value or folder      |

## Mouse interaction
Interacting with your sketch using the mouse can be very useful, with some examples being drawing with a mouse brush or clicking to select an object in a 3D scene.
But the GUI can get in the way - you don't want the sketch to draw when you're changing your brush properties in the GUI.

Unfortunately the GUI has no way to block the sketch from receiving the mouse event, but it can tell the user whether the mouse event has interacted with the GUI or not and that is what this utility function is for:
```java
void mousePressed(){
    if(gui.isMouseOutsideGui()){
        // draw something at the mouse
    }
}
```

## Saving and loading values
The GUI can save its current values to disk in a json file. It can also load these values to overwrite the current GUI state.
You can control this from the `saves` folder under the root window of the GUI. Any new, renamed and deleted save files will be detected by this window at runtime.

![save](https://user-images.githubusercontent.com/25923016/229351055-70bc5ae6-877d-4b3f-bbcc-a40ada90bda1.png)

### Saving
- create a new save at runtime with `CTRL + S`
- an **autosave** is created by default when the sketch exits gracefully (like by pressing the Escape key)
  - the autosave includes endless loop detection that prevents autosaving
### Loading
- the sketch tries to **load the latest save on startup**
  - this is usually helpful, but when bad values in a save are breaking your sketch, you can either delete the offending json file or use [constructor settings](#constructor-settings) to ignore it on startup
- you can load values from a save by clicking on its row in the `saves` window.
- loading will not initialize any new control elements 
- for a value to be overwritten in the current GUI its [path](#paths-and-folders) needs to match exactly with the saved path for that value
  - this means you lose saved values when you rename something 

## Paths and folders

The **path**  is the first string parameter to every control element function, and it must be unique.
It exists only in memory to inform the GUI - it's not a directory structure in any file storage.
The forward slash `/` is a reserved character used to make folders, but it can be escaped with `\\` like this: `\\/` which won't separate folders.

### Creating a folder with the forward slash
![wave folder example](https://user-images.githubusercontent.com/25923016/229351063-fa338553-5a28-4571-9b60-4a75cdde67a5.png)
```java
float frq = gui.slider("wave/frequency");
float amp = gui.slider("wave/amplitude");
```

### Escaping the forward slash
![Escaped forward slash example](https://user-images.githubusercontent.com/25923016/229351073-a977cf5e-030f-4e68-9625-04f24430daff.png)
```java
boolean state = gui.toggle("off\\/on");
```

### Global path prefix stack

Repeating the whole path in every control element call can get tiresome, especially with multiple nested folders.
Which is why there is a helpful path stack that you can interact with using `pushFolder()` and `popFolder()`.

Just like using `pushMatrix()` and `popMatrix()` in Processing, you can change your "current directory"
by pushing a new folder name to a stack with `gui.pushFolder("folder name")` and have every control element called after that be placed into that folder automatically
as if the contents of the whole current stack got prefixed to every path parameter you use while calling the GUI.

- `popFolder()` doesn't have a parameter - it just returns by one level

You can nest a `pushFolder()` inside another `pushFolder()` - your path stack can be many levels deep.
Just remember to call `popFolder()` the same number of times - the stack does get cleared after the end of draw() before the GUI starts drawing itself, but it's better not to rely on that.


#### Folder made by using the stack
```java
gui.pushFolder("wave");
float frq = gui.slider("frequency");
float amp = gui.slider("amplitude");
gui.popFolder();
```

#### See the current stack for debugging
```java
println(gui.getFolder());
```

### Hide and show anything
You can hide folders and single elements from code, while still receiving their values in code - the only change is visual. 
This is helpful when you have a loop for folders whose paths differ by the index, and you create too many of these folders and then want to hide some of them.
You can also use this to hide the default 'options' or 'saves' folders.

```java 
gui.hide("myPath") // hide anything at this path (the prefix stack applies here like everywhere else)
gui.show("myPath") // reveal anything previously hidden at this path
gui.hideCurrentFolder() // hide the folder at the current path prefix stack
gui.showCurrentFolder() // show the folder at the current path prefix stack if it has been previously hidden 
```

### Folder visuals
Runtime changes of what a folder row looks like in its parent window. 
This helps with organizing folders, especially with folder paths that differ only by the index inside a loop.

A folder will display a little 'on' light in its icon when at least one **toggle** inside the folder is set to true and its name matches one of the following: 
- starts with
  - "active"
  - "enabled"
  - "visible"
- equal to "" (empty string)

A folder will display a name editable at runtime when there is a **text control** whose name matches one of the following:
- starts with 
  - "label"
  - "name"
- is equal to "" (empty string)

## Constructor settings

You can initialize your gui with an extra settings object to set various global defaults and affect startup and exit behavior.
Loading a save overwrites these, but you can also disable loading on startup here.

Here's how to use it in a builder chain where the ordering does not matter:

```java
gui = new LazyGui(this, new LazyGuiSettings()
        // set as false to not load anything on startup, true by default
    .setLoadLatestSaveOnStartup(false)
        
        // expects filenames like "1" or "auto.json", overrides 'load latest'    
    .setLoadSpecificSaveOnStartup("1")
            
        // controls whether to autosave, true by default
    .setAutosaveOnExit(false)    
);

```
- for a list of all the options, see the [LazyGuiSettings javadocs](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGuiSettings.html)

## Live shader reloading
This GUI includes the (slightly out of scope) ShaderReloader class that watches your shader files as you edit them and re-compiles them when changes are made. 
If an error occurs during compilation, it keeps using the last compiled state and prints out the error to console.

Example using a fragment shader:
```java
String shaderPath = "template.glsl";
PShader shader = ShaderReloader.getShader(shaderPath);
shader.set("time", (float) 0.001 * millis());
ShaderReloader.filter(shaderPath);
```
see: [Shader Reloader javadocs](https://krabcode.github.io/LazyGui/com/krab/lazy/ShaderReloader.html)

## Dependencies
- This library is compiled with [Processing 3.5.4](https://github.com/processing/processing), which makes it compatible with 
  - legacy Processing 3.+ ([download](https://processing.org/releases))
  - current Processing 4.+ ([download](https://processing.org/download))
- This library uses and includes [gson-2.8.9](https://github.com/google/gson), mainly due to its handy [@Expose](https://www.javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/annotations/Expose.html) annotation

## Further reading
- [LazyGui javadocs](https://krabcode.github.io/LazyGui/) with function comments going into more depth than this readme
- [Processing examples](src/main/java/com/krab/lazy/examples) for the PDE
- [IntelliJ examples](src/main/java/com/krab/lazy/examples_intellij) for use in an IDE like IntelliJ IDEA
- [LazySketches](https://github.com/KrabCode/LazySketches) - bigger sketches using this GUI in my other repo
- [How to run this GUI in Kotlin](https://gist.github.com/wrightwriter/98a7c5cdeaccd28bb599f3561de3a52d)

## How to contribute
- Create a new GitHub [issue](https://github.com/KrabCode/LazyGui/issues) if you don't find your problem already in there
- Talk to me on the dedicated library [discord server](https://discord.gg/VBTCsnYMzd)
- If you want to code something yourself you can fork the repository and submit a pull request with an explanation
