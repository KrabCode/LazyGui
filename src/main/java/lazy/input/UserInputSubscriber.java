package lazy.input;


public interface UserInputSubscriber  {
    // everything is default empty
    // because I only want the implementing classes to
    // use what methods they want and not mention the rest

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void keyPressed(LazyKeyEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void keyReleased(LazyKeyEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void mousePressed(LazyMouseEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void mouseReleased(LazyMouseEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void mouseMoved(LazyMouseEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void mouseDragged(LazyMouseEvent e) {

    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param e event
     */
    default void mouseWheelMoved(LazyMouseEvent e) {

    }
}
