package lazy;

import static processing.core.PApplet.println;

@SuppressWarnings("RegExpRedundantEscape")
public class UtilsPathEscapes {

    static String REGEX_UNESCAPED_SLASH = "(?<!\\\\)\\/";

    static String getDisplayStringWithoutEscapes(String nameWithEscapes){
        return nameWithEscapes.replaceAll("\\\\/", "/");
    }

    static String[] splitByUnescapedSlashes(String source){
        return source.split(REGEX_UNESCAPED_SLASH);
    }
}

