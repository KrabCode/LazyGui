package lazy.utils;


@SuppressWarnings("RegExpRedundantEscape")
public class NodePaths {

    static final String REGEX_UNESCAPED_SLASH = "(?<!\\\\)\\/";

    public static String getDisplayStringWithoutEscapes(String nameWithEscapes){
        return nameWithEscapes.replaceAll("\\\\/", "/");
    }

    public static String[] splitByUnescapedSlashes(String source){
        return source.split(REGEX_UNESCAPED_SLASH);
    }

    public static String getPathWithoutName(String pathWithName) {
        String[] split = NodePaths.splitByUnescapedSlashes(pathWithName);
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
            if (i < split.length - 2) {
                sum.append("/");
            }
        }
        return sum.toString();
    }
}

