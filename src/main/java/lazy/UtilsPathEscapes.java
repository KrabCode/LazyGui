package lazy;

import static processing.core.PApplet.println;

@SuppressWarnings("RegExpRedundantEscape")
public class UtilsPathEscapes {

    static String REGEX_UNESCAPED_SLASH = "(?<!\\\\)\\/";

    public static void check(String source) {
        String[] split = splitByUnescapedSlashes(source);
        println(source);
        println((Object[]) split);
        println(getDisplayStringWithoutEscapes(split[split.length-1]));
    }

    @Deprecated
    static String[] splitByNaiveSlashes(String source){
        // TODO move from this placeholder, see issue #6
        return source.split("/");
    }

    private static String[] splitByUnescapedSlashes(String source){
        return source.split(REGEX_UNESCAPED_SLASH);
    }

    private static String getDisplayStringWithoutEscapes(String nameWithEscapes){
        return nameWithEscapes.replaceAll("\\\\/", "/");
    }
}

