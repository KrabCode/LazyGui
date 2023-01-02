package lazy.input;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.stores.Globals.app;

/**
 * Internal LazyGui class used to register with PApplet user input events.
 * Must be public for PApplet to be able to reach it, but not meant to be used or even looked at by library users.
 */
public class UserInputPublisher {
    public static boolean mouseFallsThroughThisFrame = false;
    private static UserInputPublisher singleton;
    private final CopyOnWriteArrayList<UserInputSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private float prevX = -1, prevY = -1;

    private UserInputPublisher() {
        registerListeners();
    }

    private void registerListeners() {
        app.registerMethod("keyEvent", this);
        app.registerMethod("mouseEvent", this);
    }

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new UserInputPublisher();
        }
    }

    public static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(0, subscriber);
    }

    public static void setFocus(UserInputSubscriber subscriber){
        singleton.subscribers.remove(subscriber);
        singleton.subscribers.add(0, subscriber);
    }

    /**
     * Method used for subscribing to processing keyboard input events, not meant to be used by the library user.
     * @param event key event
     */
    @SuppressWarnings("unused")
    public void keyEvent(KeyEvent event){
        switch(event.getAction()){
            case KeyEvent.PRESS:
                keyPressed(event);
                break;
            case KeyEvent.RELEASE:
                keyReleased(event);
                break;
        }
    }

    void keyPressed(KeyEvent event) {
        LazyKeyEvent e = new LazyKeyEvent(event.getKeyCode(), event.getKey());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    void keyReleased(KeyEvent event) {
        LazyKeyEvent e = new LazyKeyEvent(event.getKeyCode(), event.getKey());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    /**
     * Method used for subscribing to processing mouse input events, not meant to be used by the library user.
     * @param event mouse event
     */
    @SuppressWarnings("unused")
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

    void mousePressed(MouseEvent event) {
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
