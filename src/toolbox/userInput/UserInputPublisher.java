package toolbox.userInput;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import processing.core.PApplet;
import processing.core.PSurface;
import processing.opengl.PSurfaceJOGL;

import java.util.ArrayList;

import static processing.core.PApplet.floor;

public class UserInputPublisher implements KeyListener, MouseListener {
    private static UserInputPublisher singleton;
    private final ArrayList<UserInputSubscriber> subscribers = new ArrayList<>();
    float previousMouseX = -1;
    float previousMouseY = -1;

    private UserInputPublisher(PApplet app) {
        registerListeners(app);
    }

    private void registerListeners(PApplet app) {
        PSurface surface = app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            com.jogamp.newt.opengl.GLWindow window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());
            window.addKeyListener(this);
            window.addMouseListener(this);
        } else {
            System.out.println("Please use P2D or P3D in your size() or fullScreen() in order to use Toolbox");
        }
    }

    public static void CreateSingleton(PApplet app) {
        if(singleton == null){
            singleton = new UserInputPublisher(app);
        }
    }

    public static void subscribe(UserInputSubscriber subscriber) {
        singleton.subscribers.add(subscriber);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyPressed(e);
        }
//        System.out.println("keyPressed " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.keyReleased(e);
        }
//        System.out.println("keyReleased " + e.getKeyCode());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseClicked(e.getX(), e.getY());
        }
//        System.out.println("mouseClicked");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mousePressed(e.getX(), e.getY());
        }
//        System.out.println("mousePressed [ " + e.getX() + ", " +  e.getY() + " ]");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseReleased(e.getX(), e.getY());
        }
//        System.out.println("mouseReleased [ " + e.getX() + ", " +  e.getY() + " ]");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseMoved(e.getX(), e.getY(),
                    previousMouseX == -1 ? e.getX() : previousMouseX,
                    previousMouseY == -1 ? e.getY() : previousMouseY
            );
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseDragged(e.getX(), e.getY(),
                    previousMouseX == -1 ? e.getX() : previousMouseX,
                    previousMouseY == -1 ? e.getY() : previousMouseY
            );
        }
        previousMouseX = e.getX();
        previousMouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseWheelMoved(floor(e.getRotation()[1]));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseEntered(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        for (UserInputSubscriber subscriber : subscribers) {
            subscriber.mouseExited(e.getX(), e.getY());
        }
    }

}
