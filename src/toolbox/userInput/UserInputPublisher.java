package toolbox.userInput;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import processing.core.PApplet;
import processing.core.PSurface;
import toolbox.GlobalState;

import java.util.ArrayList;

import static processing.core.PApplet.floor;

public class UserInputPublisher implements KeyListener, MouseListener {
    private static UserInputPublisher singleton;
    private final ArrayList<UserInputSubscriber> subscribers = new ArrayList<>();
    private UserInputSubscriber focused = null;

    float previousMouseX = -1;
    float previousMouseY = -1;

    private UserInputPublisher() {
        registerListeners();
    }

    private void registerListeners() {
        PSurface surface = GlobalState.app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            com.jogamp.newt.opengl.GLWindow window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());
            window.addKeyListener(this);
            window.addMouseListener(this);
        } else {
            System.out.println("Please use P2D or P3D in your size() or fullScreen() in order to use h Toolbox");
        }
    }

    public static void createSingleton(PApplet app) {
        if (singleton == null) {
            singleton = new UserInputPublisher();
        }
    }

    public static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(subscriber);
        singleton.focused = subscriber;
    }

    public static void setFocus(UserInputSubscriber subscriber) {
        singleton.focused = subscriber;
    }

    // TODO reuse code using callbacks instead of duplicated code below this

    @Override
    public void keyPressed(KeyEvent e) {
        if (focused != null) {
            focused.keyPressed(e);
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (focused != null) {
            focused.keyReleased(e);
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.keyReleased(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (focused != null) {
            focused.mouseClicked(e, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseClicked(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (focused != null) {
            focused.mousePressed(e, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mousePressed(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (focused != null) {
            focused.mouseReleased(e, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseReleased(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        float px = previousMouseX == -1 ? e.getX() : previousMouseX;
        float py = previousMouseY == -1 ? e.getY() : previousMouseY;
        if (focused != null) {
            focused.mouseMoved(e, e.getX(), e.getY(), px, py);
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseMoved(e, e.getX(), e.getY(), px, py);
            if (e.isConsumed()) {
                break;
            }
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float px = previousMouseX == -1 ? e.getX() : previousMouseX;
        float py = previousMouseY == -1 ? e.getY() : previousMouseY;
        if (focused != null) {
            focused.mouseDragged(e, e.getX(), e.getY(), px, py);
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseDragged(e, e.getX(), e.getY(), px, py);
            if (e.isConsumed()) {
                break;
            }
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        int value = floor(e.getRotation()[1]);
        if (focused != null) {
            focused.mouseWheelMoved(e, value, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseWheelMoved(e, value, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (focused != null) {
            focused.mouseEntered(e, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseEntered(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (focused != null) {
            focused.mouseExited(e, e.getX(), e.getY());
        }
        for (UserInputSubscriber subscriber : subscribers) {
            if (subscriber.equals(focused)) {
                continue;
            }
            subscriber.mouseExited(e, e.getX(), e.getY());
            if (e.isConsumed()) {
                break;
            }
        }
    }

}
