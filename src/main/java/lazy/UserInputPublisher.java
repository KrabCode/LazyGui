package lazy;

import processing.event.MouseEvent;

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
        updatePreviousMousePositionBeforeHandling(event);
        switch(event.getAction()){
            case MouseEvent.MOVE:
                mouseMoved(event);
                break;
            case MouseEvent.PRESS:
                mousePressed(event);
                break;
            case MouseEvent.RELEASE:
                mouseReleased(event);
                break;
            case MouseEvent.DRAG:
                mouseDragged(event);
                break;
            case MouseEvent.WHEEL:
                mouseWheel(event);
                break;
        }
        updatePreviousMousePositionAfterHandling(event);
    }


    public void mousePressed(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mousePressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    void mouseReleased(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    float prevX = -1, prevY = -1;

    void mouseMoved(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseMoved(e);
            if (e.isConsumed()) {
                break;
            }
        }
        mouseFallsThroughThisFrame = !e.isConsumed();
    }

    private void updatePreviousMousePositionAfterHandling(MouseEvent event) {
        prevX = event.getX();
        prevY = event.getY();
    }

    private void updatePreviousMousePositionBeforeHandling(MouseEvent e) {
        if(prevX == -1){
            prevX = e.getX();
        }
        if(prevY == -1){
            prevY = e.getY();
        }
    }

    void mouseDragged(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY);
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
