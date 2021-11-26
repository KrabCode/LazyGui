package toolbox.userInput;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import processing.core.PSurface;
import toolbox.global.State;

import java.util.ArrayList;

import static processing.core.PApplet.floor;
import static processing.core.PApplet.println;

public class UserInputPublisher implements KeyListener, MouseListener {
    private static UserInputPublisher singleton;
    private final ArrayList<UserInputSubscriber> subscribers = new ArrayList<>();

    float previousMouseX = -1;
    float previousMouseY = -1;

    private UserInputPublisher() {
        registerListeners();
    }

    private synchronized void registerListeners() {
        PSurface surface = State.app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            com.jogamp.newt.opengl.GLWindow window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());

            window.addKeyListener(this);
            window.addMouseListener(this);
        } else {
            System.out.println("Please use P2D or P3D in your size() or fullScreen() in order to use h Toolbox");
        }
    }

    public synchronized static void createSingleton() {
        if (singleton == null) {
            singleton = new UserInputPublisher();
        }
    }

    public synchronized static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(subscriber);
    }

    public synchronized static void setFocus(UserInputSubscriber subscriber){
        singleton.subscribers.remove(subscriber);
        singleton.subscribers.add(0, subscriber);
    }


    @Override
    public synchronized void keyPressed(KeyEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void mouseClicked(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseClicked(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mousePressed(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        UserInputSubscriber hungrySubscriber = null;
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseReleased(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                hungrySubscriber = subscriber;
                break;
            }
        }
//        println(""+hungrySubscriber);
    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
        float px = previousMouseX == -1 ? e.getX() : previousMouseX;
        float py = previousMouseY == -1 ? e.getY() : previousMouseY;
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseMoved(e, e.getX(), e.getY(), px, py);
            if (e.isConsumed()) {
                break;
            }
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        float px = previousMouseX == -1 ? e.getX() : previousMouseX;
        float py = previousMouseY == -1 ? e.getY() : previousMouseY;
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseDragged(e, e.getX(), e.getY(), px, py);
            if (e.isConsumed()) {
                break;
            }
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public synchronized void mouseWheelMoved(MouseEvent e) {
        int value = floor(e.getRotation()[1]);
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseWheelMoved(e, value, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void mouseEntered(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseEntered(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public synchronized void mouseExited(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseExited(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

}
