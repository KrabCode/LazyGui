package lazy;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public interface UserInputSubscriber  {
    // everything is default empty
    // because I only want the implementing classes to
    // use what methods they want and not mention the rest

    default void keyPressed(KeyEvent keyEvent) {

    }

    default void keyReleased(KeyEvent keyEvent) {

    }

    default void mouseClicked(MouseEvent e, float x, float y) {

    }

    default void mouseEntered(MouseEvent e,float x, float y) {

    }

    default void mouseExited(MouseEvent e,float x, float y) {

    }

    default void mousePressed(MouseEvent e,float x, float y) {

    }

    default void mouseReleased(MouseEvent e,float x, float y) {

    }

    default void mouseMoved(MouseEvent e,float x, float y, float px, float py) {

    }

    default void mouseDragged(MouseEvent e,float x, float y, float px, float py) {

    }

    default void mouseWheelMoved(MouseEvent e,int dir, float x, float y) {

    }
}
