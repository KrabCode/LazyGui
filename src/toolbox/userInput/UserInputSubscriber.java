package toolbox.userInput;

public interface UserInputSubscriber {

    void onMouseClick(float x, float y);

    void onMouseDrag(float x, float y, float px, float py);

    void onMouseRelease(int x, int y);
}
