package toolbox.userInput;

import processing.core.PApplet;

import java.util.ArrayList;

public class UserInputPublisher {
    private static UserInputPublisher singleton;
    private final PApplet app;
    private final ArrayList<UserInputSubscriber> subscribers = new ArrayList<>();
    boolean mousePressed = false;
    boolean pMousePressed = false;
    private UserInputPublisher(PApplet app) {
        this.app = app;
    }

    public static void CreateSingleton(PApplet app) {
        singleton = new UserInputPublisher(app);
    }

    public static UserInputPublisher getInstance() {
        return singleton;
    }

    public static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(subscriber);
    }

    public void update() {
        mousePressed = app.mousePressed;
        detectClick();
        detectMouseRelease();
        detectDrag();
        pMousePressed = mousePressed;
    }

    private void detectDrag() {
        if(mousePressed && pMousePressed &&
                app.mouseX != app.pmouseX &&
                app.mouseY != app.pmouseY){
            publishDrag();
        }
    }

    private void detectClick() {
        if (mousePressed && !pMousePressed) {
            publishClick();
        }
    }

    private void detectMouseRelease() {
        if (!mousePressed && pMousePressed) {
            publishMouseRelease();
        }
    }

    private void publishMouseRelease() {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.onMouseRelease(app.mouseX, app.mouseY);
        }
    }

    private void publishClick() {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.onMouseClick(app.mouseX, app.mouseY);
        }
    }

    private void publishDrag() {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.onMouseDrag(app.mouseX, app.mouseY, app.pmouseX, app.pmouseY);
        }
    }
}
