package lazy;

import lazy.input.LazyKeyEvent;
import lazy.input.UserInputPublisher;
import lazy.input.UserInputSubscriber;

import static lazy.stores.GlobalReferences.gui;

class HiddenEventSubscriber implements UserInputSubscriber {

    static HiddenEventSubscriber singleton;

    private HiddenEventSubscriber() {}

    public static void initSingleton() {
        if(singleton == null){
            singleton = new HiddenEventSubscriber();
        }
        UserInputPublisher.subscribe(singleton);
    }

    /**
     * Method subscribed to PApplet input events, not meant for library users.
     * @param keyEvent current key event
     */
    @Override
    public void keyPressed(LazyKeyEvent keyEvent) {
        gui.handleHotkeyInteraction(keyEvent);
    }

}
