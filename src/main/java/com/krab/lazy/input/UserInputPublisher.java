package com.krab.lazy.input;

import com.krab.lazy.stores.GlobalReferences;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.stores.UndoRedoStore;
import com.krab.lazy.utils.KeyCodes;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Internal LazyGui class used to register with PApplet user input events.
 * Must be public for PApplet to be able to reach it, but not meant to be used or even looked at by library users.
 */
public class UserInputPublisher {
    public static boolean mouseFallsThroughThisFrame = false;
    private static UserInputPublisher singleton;
    private final CopyOnWriteArrayList<UserInputSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private float prevX = -1, prevY = -1;

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new UserInputPublisher();
        }
    }

    private UserInputPublisher() {
        registerListeners();
    }

    private void registerListeners() {
        // the reference passed here is the only reason to have this be a singleton instance rather than a fully static class with no instance
        GlobalReferences.app.registerMethod("keyEvent", this);
        GlobalReferences.app.registerMethod("mouseEvent", this);
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
        LazyKeyEvent e = new LazyKeyEvent(event);
        if(e.isControlDown() && e.getKeyCode() == KeyCodes.Z){
            UndoRedoStore.undo();
            e.consume();
            return;
        }
        if(e.isControlDown() && e.getKeyCode() == KeyCodes.Y){
            UndoRedoStore.redo();
            e.consume();
            return;
        }
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    void keyReleased(KeyEvent event) {
        LazyKeyEvent e = new LazyKeyEvent(event);
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
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY, event.getButton());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mousePressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
        updateMouseFallThroughState(e);
    }

    void mouseReleased(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY, event.getButton());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
        updateMouseFallThroughState(e);
    }

    void mouseMoved(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY, event.getButton());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseMoved(e);
            if (e.isConsumed()) {
                break;
            }
        }
        updateMouseFallThroughState(e);
    }

    void mouseDragged(MouseEvent event) {
        LazyMouseEvent e = new LazyMouseEvent(event.getX(), event.getY(), prevX, prevY, event.getButton());
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseDragged(e);
            if (e.isConsumed()) {
                break;
            }
        }
        updateMouseFallThroughState(e);
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
        updateMouseFallThroughState(e);
    }

    private void updateMouseFallThroughState(LazyMouseEvent e) {
        mouseFallsThroughThisFrame = !e.isConsumed() || LayoutStore.isGuiHidden();
    }
}
