package lazy;


interface UserInputSubscriber  {
    // everything is default empty
    // because I only want the implementing classes to
    // use what methods they want and not mention the rest

    default void keyPressed(LazyKeyEvent e) {

    }

    default void keyReleased(LazyKeyEvent e) {

    }

    default void mousePressed(LazyMouseEvent e) {

    }

    default void mouseReleased(LazyMouseEvent e) {

    }

    default void mouseMoved(LazyMouseEvent e) {

    }

    default void mouseDragged(LazyMouseEvent e) {

    }

    default void mouseWheelMoved(LazyMouseEvent e) {

    }
}
