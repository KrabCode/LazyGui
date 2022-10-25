package lazy;

import processing.core.PApplet;
import processing.event.MouseEvent;

import java.awt.event.MouseWheelEvent;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserInputPublisher {
    static boolean mouseFallsThroughThisFrame = false;
    private static UserInputPublisher singleton;
    private final CopyOnWriteArrayList<UserInputSubscriber> subscribers = new CopyOnWriteArrayList<>();

    private UserInputPublisher() {
        registerListeners();
    }

    private void registerListeners() {
        State.app.registerMethod("keyPressed", this);
        State.app.registerMethod("keyReleased", this);
        State.app.registerMethod("mouseEvent", this);
    }

    static void createSingleton() {
        if (singleton == null) {
            singleton = new UserInputPublisher();
        }
    }

    static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(0, subscriber);
    }

    static void setFocus(UserInputSubscriber subscriber){
        singleton.subscribers.remove(subscriber);
        singleton.subscribers.add(0, subscriber);
    }

    public void keyPressed() {
        LazyKeyEvent e = new LazyKeyEvent(State.app.keyCode, State.app.key);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    public void keyReleased() {
        LazyKeyEvent e = new LazyKeyEvent(State.app.keyCode, State.app.key);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }


    public void mouseEvent(MouseEvent event) {
        switch(event.getAction()){
            case MouseEvent.MOVE:
                mouseMoved();
                break;
            case MouseEvent.PRESS:
                mousePressed();
                break;
            case MouseEvent.RELEASE:
                mouseReleased();
                break;
            case MouseEvent.DRAG:
                mouseDragged();
                break;
            case MouseEvent.WHEEL:
                mouseWheel(event);
                break;
        }
    }


    public void mousePressed() {
        LazyMouseEvent e = new LazyMouseEvent();
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mousePressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    void mouseReleased() {
        LazyMouseEvent e = new LazyMouseEvent();
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    void mouseMoved() {
        LazyMouseEvent e = new LazyMouseEvent();
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseMoved(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    void mouseDragged() {
        LazyMouseEvent e = new LazyMouseEvent();
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseDragged(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    void mouseWheel(MouseEvent event) {
        int value = - event.getCount();
        LazyMouseEvent e = new LazyMouseEvent(value);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseWheelMoved(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }
}
