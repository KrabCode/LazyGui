package toolbox.userInput;

import com.jogamp.newt.event.KeyEvent;

public interface UserInputSubscriber  {

    default void keyPressed(KeyEvent keyEvent) {

    }

    default void keyReleased(KeyEvent keyEvent) {

    }

    default void mouseClicked(float x, float y) {

    }

    default void mouseEntered(float x, float y) {

    }

    default void mouseExited(float x, float y) {

    }

    default void mousePressed(float x, float y) {

    }

    default void mouseReleased(float x, float y) {

    }

    default void mouseMoved(float x, float y, float px, float py) {

    }

    default void mouseDragged(float x, float y, float px, float py) {

    }

    default void mouseWheelMoved(int dir) {

    }
}
