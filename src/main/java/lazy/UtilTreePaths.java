package lazy;


@SuppressWarnings("RegExpRedundantEscape")
public class UtilTreePaths {

    static final String REGEX_UNESCAPED_SLASH = "(?<!\\\\)\\/";

    static String getDisplayStringWithoutEscapes(String nameWithEscapes){
        return nameWithEscapes.replaceAll("\\\\/", "/");
    }

    static String[] splitByUnescapedSlashes(String source){
        return source.split(REGEX_UNESCAPED_SLASH);
    }

    static String getPathWithoutName(String pathWithName) {
        String[] split = UtilTreePaths.splitByUnescapedSlashes(pathWithName);
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

