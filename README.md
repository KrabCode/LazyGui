# Lazy Gui

### Why?

---

**Problem**: You're making a processing sketch, and you want to tweak some values. But restarting the sketch slows you down. So you use a processing GUI library, register its control elements in setup() and then ask for their values in draw(). 
But now when you want to add a new control element you need to add code to two unrelated places. This slows you down.

- **Solution**: Just ask for the values in draw() and have the GUI silently take care of the initialization and putting your control inside a window of related controls based on a string path you provide.


**Problem**: You just tweaked some values in your GUI, but now you need to change the code and restart the program. Your GUI changes are lost forever.

- **Solution**: Save the GUI state as a JSON file on demand and on program exit. Load the most recently saved values at control element initialization. This allows you to seamlessly change your code and continue where you left off.

---

#### Faster iteration makes your life easier and your output prettier.

---