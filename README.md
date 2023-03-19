<!-- TOC -->
  * [LazyGui is a GUI library for Processing](#lazygui-is-a-gui-library-for-processing)
  * [How do I run this?](#how-do-i-run-this)
      * [Minimal example](#minimal-example)
  * [How do I get values from the GUI?](#how-do-i-get-values-from-the-gui)
    * [Slider](#slider)
    * [Plot](#plot)
    * [Color picker](#color-picker)
    * [Gradient picker](#gradient-picker)
    * [Button](#button)
    * [Toggle](#toggle)
    * [Text input](#text-input)
    * [Radio](#radio)
  * [Paths and folders](#paths-and-folders)
      * [Creating a folder with the forward slash](#creating-a-folder-with-the-forward-slash)
      * [Escaping the forward slash](#escaping-the-forward-slash)
  * [Global path prefix stack](#global-path-prefix-stack)
      * [Folder made by using the stack](#folder-made-by-using-the-stack)
      * [See the current stack for debugging](#see-the-current-stack-for-debugging)
  * [Hide and show anything](#hide-and-show-anything)
  * [Hotkeys](#hotkeys)
  * [Constructor settings](#constructor-settings)
  * [Further reading](#further-reading)
  * [How to contribute](#how-to-contribute)
<!-- TOC -->

## LazyGui is a GUI library for Processing
- **focusing on flexibility**
  - almost no gui logic in `setup()`
  - just ask for values at unique string paths in `draw()`
  - this lets you keep related gui code together in the middle of the action
- **and ease of use**
  - keyboard input for string, float and vector controls
  - customizable look and feel
  - load / save your gui state to disk as json
      - autosave on program exit
      - autoload on program start
  - [hotkeys](#hotkeys) for common actions
      - copy / paste any value or whole folders
      - undo / redo any change
  - [reloading shaders](src/main/java/lazy/ShaderReloader.java) at runtime
  
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
    background(gui.colorPicker("bg").hex);
}
```
The gui displays itself at the end of `draw()` and by default it shows the root folder with an inner "options" folder for tweaking the various gui settings. 

A sketch with the above code should look like this:

![root and options look like this](readme_assets/basic_example.png)

## How do I get values from the GUI?
- getters initialize controls when first called
- setters also initialize controls when first called
- optional default parameters are used when first called and then ignored
- visually, rows of controls are ordered by when they were first initialized
### Slider
![a slider looks like this](readme_assets/slider.png)
```java
// simplest getter
float x = gui.slider("x");

// alternative getters that specify defaults
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
![a plot looks like this](readme_assets/plotXY.png)
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
![a color picker looks like this](readme_assets/colorpicker.png)
```java
// simplest getter
PickerColor myColor = gui.colorPicker("color name");
background(myColor.hex);

// alternative getters that specify the default color
gui.colorPicker("color name", color(36));
gui.colorPicker("color name", grayNorm); // 'norm' meaning float in the range [0, 1]
gui.colorPicker("color name", hueNorm, saturationNorm, brightnessNorm);
gui.colorPicker("color name", hueNorm, saturationNorm, brightnessNorm, alphaNorm);

// setters
gui.colorPickerSet("color name", color(36));
gui.colorPickerHueAdd("color name", hueToAdd);
```
- HSBA color picker with a hex string display
- returns a read-only PickerColor object with an integer 'hex' field 
  - this hex integer is the same thing as the Processing [color datatype](https://processing.org/reference/color_datatype.html)
- copy and paste the hex with mouse over either the hex field or the color preview block

### Gradient picker
![a gradient picker looks like this](https://user-images.githubusercontent.com/25923016/226012034-6ce0ced9-02d2-4288-8e5b-3ced0ee3af0f.gif)

```java
// simple getter
PGraphics gradient = gui.gradient("gradient name");
image(gradient, 0, 0);

// alternative getter that specifies default color(s)
gui.gradient("gradient name", color(255,0,150));
gui.gradient("gradient name", new int[]{color(255,0,150), color(0,150,0), color(0,100,150)});

// special getter for a color inside the gradient at a position in range [0, 1]
// faster than texture.get(x, y) thanks to a color look up table
PickerColor myColor = gui.gradientColorAt("gradient name", positionNorm);
```
- allows you to set the position and value of individual colors and get the result as a PGraphics
- output texture size is always kept equal to sketch size
- try the edge wrapping!


### Button
![a button looks like this](readme_assets/button.png)
```java
// getter that is only true once after being clicked then and switches to false 
if(gui.button("do the thing!")){
    println("it is done");
}
```

### Toggle
![a toggle looks like this](readme_assets/toggle.png)
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
![text input looks like this](readme_assets/text.png)
```java
// simple getter
String userInput = gui.text("text header");

// alternative getters
gui.text("text header", "this default text can be edited");
gui.text("", "this text won't have a header row above it");

// setters
textSet("text header", "content")
```
| Mouse Hotkey | Action under mouse    |
|--------------|-----------------------|
| Enter        | insert new line       |
| Delete       | delete entire string  |
| Backspace    | delete last character |
- typing with mouse over the text appends to its last line

### Radio
![radio looks like this](readme_assets/radio.png)
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

## Paths and folders

The **path**  is the first string parameter to every control element function, and it must be unique.
It exists only in memory to inform the GUI - it's not a directory structure in any file storage.
The forward slash `/` is a reserved character used to make folders, but it can be escaped with `\\` like this: `\\/` which won't separate folders.

#### Creating a folder with the forward slash
![wave folder example](readme_assets/wave_folder.png)
```java
float frq = gui.slider("wave/frequency");
float amp = gui.slider("wave/amplitude");
```

#### Escaping the forward slash
![Escaped forward slash example](readme_assets/escaped_slash.png)
```java
boolean state = gui.toggle("off\\/on");
```

## Global path prefix stack

Repeating the whole path in every control element call can get tiresome, especially with multiple nested levels.
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

## Hide and show anything
You can hide folders and single elements from code, while still receiving their values in code - the only change is visual. 
This is helpful when you have a loop for folders whose paths differ by the index, and you create too many of these folders and then want to hide some of them.
You can also use this to hide the default 'options' or 'saves' folders.

```java 
gui.hide("myPath") // hide anything at this path (the prefix stack applies here like everywhere else)
gui.show("myPath") // reveal anything previously hidden at this path
gui.hideCurrentFolder() // hide the folder at the current path prefix stack
gui.showCurrentFolder() // show the folder at the current path prefix stack if it has been previously hidden 
```

## Folder visuals
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

## Hotkeys

|   Global hotkey   | Action under mouse     |     
|:-----------------:|:-----------------------|
|         H         | Hide/Show GUI          |     
|         D         | Close windows          |     
|         I         | Save screenshot        | 
|     CTRL + Z      | Undo                   | 
|     CTRL + Y      | Redo                   |        
|     CTRL + S      | Save gui state to json |   

| Mouse hotkey | Action on element under mouse |
|:------------:|:------------------------------|
|      R       | Reset value to default        |
|   CTRL + C   | Copy value or folder          |
|   CTRL + V   | Paste to value or folder      |

## Constructor settings
You can initialize your gui with an extra settings object to set various global defaults and affect startup and exit behavior.
Loading a save overwrites these, but you can also disable loading on startup here.

See all the options below:

```java
gui = new LazyGui(this, new LazyGuiSettings()
        
        // LOADING ON STARTUP
        .setLoadLatestSaveOnStartup(false) // set as false to not load anything on startup
        .setLoadSpecificSaveOnStartup("1") // expects filenames like "1" or "auto.json", overrides 'load latest'

        // AUTOSAVE
        .setAutosaveOnExit(true)    // but the shutdown hook only works on graceful exit, for example the ESC button
        .setAutosaveLockGuardEnabled(true) // for not autosaving settings that locked the sketch in an endless loop
        .setAutosaveLockGuardMillisLimit(1000) // millis the last frame must be rendered faster than for autosave to work

        // MOUSE
        .setMouseHideWhenDragging(true) // when dragging a slider for example
        .setMouseConfineToWindow(false)

        // LAYOUT
        .setCellSize(22) // affects the size of the whole gui
        .setMainFontSize(16)
        .setSideFontSize(15)
        .setStartGuiHidden(false) // uncover hidden gui with the 'h' hotkey

        // THEME
        .setThemePreset("dark") // selected preset, one of "dark", "light", "pink", "blue"
        .setThemeCustom(
                color(0, 0, 255),   // window border color
                color(16),       // normal background color
                color(0, 0, 0),      // focused background color
                color(200),     // normal foreground color
                color(255))    // focused foreground color
         // custom theme overrides preset when not null
);

```

## Further reading
- [Javadocs](https://krabcode.github.io/LazyGui/) on github pages
- [Processing examples](src/main/java/examples)
- [IntelliJ examples](src/main/java/examples_intellij) for use in an IDE like IntelliJ IDEA
- [LazySketches](https://github.com/KrabCode/LazySketches) - bigger sketches using this GUI in my other repo
- [How to run this GUI in Kotlin](https://gist.github.com/wrightwriter/98a7c5cdeaccd28bb599f3561de3a52d)

## How to contribute
- Talk to the author on the dedicated library [discord server](https://discord.gg/VBTCsnYMzd)
- Create a new GitHub [issue](https://github.com/KrabCode/LazyGui/issues) if you don't find your problem already in there
- Fork the repository and submit a pull request
