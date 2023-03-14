package lazy;

import lazy.input.LazyKeyEvent;
import lazy.input.UserInputPublisher;
import lazy.input.UserInputSubscriber;
import lazy.stores.HotkeyStore;

/**
 * Singleton class listening for global hotkeys that fall through all of the potential visual controls under the mouse unconsumed.
 */
class HotkeySubscriber implements UserInputSubscriber {

    static HotkeySubscriber singleton;

    private HotkeySubscriber() {}

    public static void initSingleton() {
        if(singleton == null){
            singleton = new HotkeySubscriber();
        }
        UserInputPublisher.subscribe(singleton);
    }

    @Override
    public void keyPressed(LazyKeyEvent keyEvent) {
        HotkeyStore.handleHotkeyInteraction(keyEvent);
    }

}
