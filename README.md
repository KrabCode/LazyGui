![LazyGui header](https://i.imgur.com/2wExMsG.png)

Table of Contents
<!-- TOC -->
  * [LazyGui is a GUI library for Processing](#lazygui-is-a-gui-library-for-processing)
  * [How do I start using this?](#how-do-i-start-using-this)
    * [with PDE (the default Processing editor)](#with-pde-the-default-processing-editor)
    * [with other IDEs like IntelliJ IDEA](#with-other-ides-like-intellij-idea)
  * [Minimal code example](#minimal-code-example)
  * [Control elements](#control-elements)
    * [Slider](#slider)
    * [Plot](#plot)
    * [Color picker](#color-picker)
    * [Gradient picker](#gradient-picker)
    * [Button](#button)
    * [Toggle](#toggle)
    * [Text input](#text-input)
    * [Radio](#radio)
    * [Image preview](#image-preview)
  * [Hotkeys](#hotkeys)
    * [Global hotkeys](#global-hotkeys)
    * [Mouse-over hotkeys](#mouse-over-hotkeys)
  * [Mouse interaction](#mouse-interaction)
  * [Drawing the GUI manually](#drawing-the-gui-manually)
  * [Saving and loading values](#saving-and-loading-values)
    * [Create save](#create-save)
    * [Load save](#load-save)
  * [Paths and folders](#paths-and-folders)
    * [Creating a folder with the forward slash](#creating-a-folder-with-the-forward-slash)
    * [Escaping the forward slash](#escaping-the-forward-slash)
    * [Global path prefix stack](#global-path-prefix-stack)
      * [Folder made by using the stack](#folder-made-by-using-the-stack)
      * [See the current stack for debugging](#see-the-current-stack-for-debugging)
    * [Hide and show anything](#hide-and-show-anything)
    * [Has a value changed last frame?](#has-a-value-changed-last-frame)
    * [Folder visuals](#folder-visuals)
  * [Constructor settings](#constructor-settings)
  * [Window restoration](#window-restoration)
  * [Live shader reloading](#live-shader-reloading)
  * [Input](#input)
  * [Compatibility](#compatibility)
  * [Dependencies](#dependencies)
  * [Further reading](#further-reading)
  * [How to contribute](#how-to-contribute)
  * [How to compile and run this library](#how-to-compile-and-run-this-library)
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
  
## How do I start using this?

### with PDE (the default Processing editor)
- Find LazyGui in the Contribution Manager under the Libraries tab and click `Install`

- See example sketches: `File -> Examples... -> Contributed Libraries`

- Leaf through the offline javadocs: `Help -> Libraries Reference -> LazyGui`
   

### with other IDEs like IntelliJ IDEA
Get the latest jar file from [releases](https://github.com/KrabCode/LazyGui/releases/latest) and then import it into your project using your IDE as a standard java library just like you imported Processing.

## Minimal code example
```java
import com.krab.lazy.*;

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
- **saves** for managing your save files

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
- change the selected precision when mouse is over a slider with the mouse wheel or `/` and `*` keys
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
- returns a read-only [PickerColor](https://krabcode.github.io/LazyGui/com/krab/lazy/PickerColor.html) object with an integer 'hex' field 
  - this hex integer is the same thing as the Processing [color datatype](https://processing.org/reference/color_datatype.html)
  - displays the correct color in any Processing [color mode](https://processing.org/reference/colorMode_.html)
- paste in values from sites like [colorhunt.co](https://colorhunt.co/)
- copy and paste the hex value with mouse over the desired color row / preview / hex string

### Gradient picker
![gradient pickers look like this](https://user-images.githubusercontent.com/25923016/229208017-99699e27-dbb7-4054-ac21-a8fe16156868.gif)
```java
// simple getter
PGraphics bgGradient = gui.gradient("background gradient");
image(bgGradient, 0, 0);

// alternative getter that specifies the default colors
gui.gradient("name", new int[]{color(255,0,150), color(0,150,0), color(0,100,150)});

// alternative getter which allows you to specify default colors and positions
// it uses varargs so you can use two or more gui.colorPoint() parameters
gui.gradient("name",
  gui.colorPoint(color(255, 0, 0), 0f),
  gui.colorPoint(color(0, 255, 0), 0.5f),
  gui.colorPoint(color(0, 0, 255), 1f)
);

// special getter for a color inside the gradient at a position in range [0, 1]
// faster than texture.get(x, y) thanks to a color look up table
PickerColor myColor = gui.gradientColorAt("name", positionNorm);
```
- allows you to set the position and value of individual colors and get the result as a PGraphics
- output texture size is kept equal to main sketch size
- choose from 3 supported color spaces with the "blend" option
  - [mix](https://registry.khronos.org/OpenGL-Refpages/gl4/html/mix.xhtml) - naive RGB lerp (default)
  - [hsv](https://www.shadertoy.com/view/MsS3Wc) - cycle through hues
  - [oklab](https://bottosson.github.io/posts/oklab/) - perceptual color space
- gradients are drawn using this shader: [data/shaders/gradient.glsl](data/shaders/gradient.glsl)


### Button
![a button looks like this](https://user-images.githubusercontent.com/25923016/229208008-04dec541-c3d5-4f77-bd3f-ff824d64d395.gif)
```java
// getter that is only true once after being clicked and then switches to false 
boolean clear = gui.button("clear");
if(clear){
    background(0.1);
    println("background cleared");
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
gui.textSet("text header", "content")
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

// setter that changes the currently selected option
gui.radioSet("mode", "square");

// setter that specifies new options for an existing radio
gui.radioSetOptions("mode", new String[]{"square", "circle", "triangle"});
```
- opens a folder of toggles where setting one to true sets all others to false
- returns the selected option as a string
- changes to the options parameter will be ignored after the radio is first initialized
- the options can only be changed at runtime with `radioSetOptions()`
- instead of the `String[]` array of options you can also use `List<String>` or `ArrayList<String>`

### Image preview
![image preview placeholder](https://private-user-images.githubusercontent.com/25923016/501999099-d4acde02-e076-4814-b4eb-068c2521ad5e.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjA2MTUxOTIsIm5iZiI6MTc2MDYxNDg5MiwicGF0aCI6Ii8yNTkyMzAxNi81MDE5OTkwOTktZDRhY2RlMDItZTA3Ni00ODE0LWI0ZWItMDY4YzI1MjFhZDVlLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTEwMTYlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUxMDE2VDExNDEzMlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWQyNzVlZmI5OTg4MTllMzMzNDM1NTQwZmEzNDI2NzgwZmY0NzA5ODM2OTQwMGU5ZWVkYTE3MjgwZmI4OGE1YTUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.KgcL1koALKa2T03kkgIoz2EP9oskil8MfEvlTpaTlUw.jpg)

A simple preview window for a `PImage` or `PGraphics`.

- top-left aligned, resizable preview
- doesn't call get() internally to avoid performance issues
- you can use get() yourself to make snapshots of your main canvas during draw()

Example:
```java
// PImage or PGraphics
gui.image("preview", img);

// Main canvas snapshots to show different states during one draw() call
gui.image("snapshot 1", get());
// draw something ..
gui.image("snapshot 2", get());
```

## Hotkeys

### Global hotkeys
|   Key    | Action                                 |     
|:--------:|:---------------------------------------|
|    H     | Hide GUI / Show GUI                    |     
|    D     | Close windows                          |     
|    I     | Save screenshot                        | 
| CTRL + Z | Undo                                   | 
| CTRL + Y | Redo                                   |        
| CTRL + S | [New save](#saving-and-loading-values) |   

### Mouse-over hotkeys
|     Key     | Action on element under mouse |
|:-----------:|:------------------------------|
| Right click | Close window                  |
|      R      | Reset value to default        |
|  CTRL + C   | Copy value or folder          |
|  CTRL + V   | Paste to value or folder      |
|      *      | Increase slider precision     |
|  Wheel up   | Increase slider precision     |
|      /      | Lower slider precision        |
| Wheel down  | Lower slider precision        |

## Mouse interaction
Interacting with your sketch using the mouse can be very useful, but the GUI can get in the way, usually you don't want the sketch to react when you're dragging a slider in the GUI.

Unfortunately the GUI has no way to block the sketch from receiving the mouse event, but it can tell you whether the mouse has interacted with the GUI thanks to the `isMouseOutsideGui()` method.
```java
void mousePressed(){
    if(gui.isMouseOutsideGui()){
        // do something at the mouse
    }
}
```
see: [isMouseOutsideGui()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#isMouseOutsideGui--), [isMouseOverGui()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#isMouseOverGui--), [MouseDrawing](https://github.com/KrabCode/LazyGui/blob/master/src/main/java/com/krab/lazy/examples/MouseDrawing/MouseDrawing.pde)  

## Drawing the GUI manually
The GUI draws itself at the end of draw() by default, but you can override this by calling `gui.draw()` before that happens. The GUI will never draw itself more than once per frame, so the automatic execution is skipped when this is called manually.

This can be useful in cases like including the GUI in a recording or using the PeasyCam library where you probably want to display the GUI between `cam.beginHUD()` and `cam.endHUD()` to separate the GUI overlay from the camera controlled 3D scene.

see: [draw()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#draw--),
[PeasyCamExample](https://github.com/KrabCode/LazyGui/blob/master/src/main/java/com/krab/lazy/examples/PeasyCamExample/PeasyCamExample.pde)

## Saving and loading values
The GUI can save its current values to disk in a json file. It can also load these values to overwrite the current GUI state.
You can control this from the `saves` folder under the root window of the GUI. Any new, renamed and deleted save files will be detected by this window at runtime.

![save](https://user-images.githubusercontent.com/25923016/229351055-70bc5ae6-877d-4b3f-bbcc-a40ada90bda1.png)

### Create save
- create a new save with the button at `saves/create new save` or `CTRL + S`
  - or create a new save from code with [createSave()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#createSave()) or [createSave(path)](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#createSave(java.lang.String))
- an **autosave** is created by default when the sketch exits gracefully (like by pressing the Escape key)
  - the autosave includes endless loop detection that prevents autosaving
  - you can edit this behavior in the `saves/autosave rules` folder
### Load save
- the sketch tries to **load the latest save on startup**
  - this is usually helpful, but when bad values in a save are breaking your sketch, you can either delete the offending json file or use [constructor settings](#constructor-settings) to ignore it on startup
- load a save manually by clicking on its row in the `saves` window 
  - or load saves from code with [loadSave(path)](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#loadSave(java.lang.String)).
- loading will not initialize any new control elements 
- for a value to be overwritten in the current GUI its [path](#paths-and-folders) needs to match exactly with the saved path for that value
  - this means you lose saved values when you rename a folder or a control element
  - but you can freely copy saves between sketches, the sketch name does not matter

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
Which is why there's a helpful path stack that you can interact with using `pushFolder()` and `popFolder()`.

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

see javadocs: [pushFolder()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#pushFolder(java.lang.String)), [popFolder()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#popFolder()), [getFolder()](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGui.html#getFolder())

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

### Has a value changed last frame?
You can check whether a value has changed last frame with `gui.hasChanged("myPath")` and `gui.hasChanged()`.
This can be useful when you don't want to do an expensive operation every frame but only when its controlling parameters change.

This works with single control elements, but it also works recursively through any child elements, so you can call it on a folder, and it will return true if any value nested under it has changed.

The result after a change is only true for one frame after a change and then gets reset to false for the next frame.
These functions respect the current path stack. They do not initialize any new control elements or folders.
Calling the function does not flip the value to false by itself.

See the [UtilityMethods](https://github.com/KrabCode/LazyGui/blob/1a2d857f89eba45efa4c5522b5425d7c962c69f2/src/main/java/com/krab/lazy/examples/UtilityMethods/UtilityMethods.pde) example which uses it to load a PFont whenever the user changes the font name or size.

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

Here's how to use it in a builder chain where the ordering does not matter (except for conflicting instructions):

```java
gui = new LazyGui(this, new LazyGuiSettings()
        // set as false to not load anything on startup, true by default
    .setLoadLatestSaveOnStartup(false)
        
        // expects filenames like "1" or "auto.json", overrides 'load latest'    
    .setLoadSpecificSaveOnStartup("1")
            
        // controls whether to autosave, true by default
    .setAutosaveOnExit(false)    
    
        // windows will never 'restore' when loading a save (allowed once at startup by default)
    .setWindowRestoreNever()
);

```
- for a list of all the options, see the [LazyGuiSettings javadocs](https://krabcode.github.io/LazyGui/com/krab/lazy/LazyGuiSettings.html)

## Window restoration
When you load a save, the GUI will try to restore the window state to what it was when the save was made.
This includes the position, size, and open/closed state of each window.
There are three available modes, selected using the [Constructor settings](#constructor-settings).
- `setWindowRestoreNever()` 
- `setWindowRestoreOnlyOnStartup()` <= default, a balance of startup convenience and control at runtime
- `setWindowRestoreAlways()`

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

For shader compilation to work, ShaderReloader needs a reference to a PApplet, so in `setup()`:
- either call `new LazyGui(this)` as seen in the [minimal code example](#minimal-code-example)
- or call `ShaderReloader.setApplet(this)` in case you don't need the GUI in your sketch

see: [Shader Reloader javadocs](https://krabcode.github.io/LazyGui/com/krab/lazy/ShaderReloader.html)

## Input
This GUI also includes the Input utility that makes it easier to see whether any number of keys are currently pressed on the keyboard. 
Processing only shows you one key at a time while this utility keeps track of past events and can tell you whether any char or keyCode was just pressed, is currently held down or if it was just released. 

Example detecting CTRL + SPACE with Input class static methods:
```java
boolean isControlDown = Input.getCode(CONTROL).down;
boolean spaceWasJustPressed = Input.getChar(' ').pressed;
if(isControlDown && spaceWasJustPressed){
    println("ctrl + space pressed");
}
```

see: [Input javadocs](https://krabcode.github.io/LazyGui/com/krab/lazy/Input.html)

## Compatibility
LazyGui runs on all of these:
- Linux (tested on Ubuntu)
- Windows (tested on Windows 10)
- Mac OS 
  - including Silicon with its smooth() level limitation, fixed [here](https://github.com/KrabCode/LazyGui/issues/244)

## Dependencies
- This library is compiled with [Processing 3.3.7](https://github.com/processing/processing), which makes it compatible with 
  - legacy Processing 3.3.7+ ([download](https://processing.org/releases))
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
- If you want to code something yourself you can fork the repository, make some edits on the `develop` branch (or make your own branch based on `develop`), test your changes and submit a pull request

## How to compile and run this library
- Clone and open the library in IntelliJ IDEA
- Do `link gradle project` which should call `gradle build` and set all the source folders correctly
- The `com/krab/lazy/examples_intellij` directory contains runnable examples which you can edit and run to test your changes to the GUI
- If you're making a new feature, making a new example sketch there is probably a good idea