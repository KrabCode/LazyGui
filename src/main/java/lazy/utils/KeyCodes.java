package lazy.utils;


import java.util.ArrayList;

@SuppressWarnings("CommentedOutCode")
public
class KeyCodes {
    public static final int DELETE = 147;
    public static final int CTRL_C = 67;
    public static final int CTRL_V = 86;
    static final int CTRL_Z = 89;
    static final int CTRL_Y = 90;
    public static final int CTRL_S = 83;

    private static final ArrayList<Integer> ignoredKeyCodes = new ArrayListBuilder<Integer>().add(157).build();

    public static boolean isKeyCodeIgnored(int keyCode){
        return ignoredKeyCodes.contains(keyCode);
    }
/*
 // find keycodes in processing:

    void setup(){
        size(200,200);
    }

    void draw(){

    }

    void keyPressed(){
      println(keyCode);
    }
 */
}
