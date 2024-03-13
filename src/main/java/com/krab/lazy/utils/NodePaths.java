package com.krab.lazy.utils;


@SuppressWarnings("RegExpRedundantEscape")
public class NodePaths {

    static final String REGEX_UNESCAPED_SLASH_LOOKBEHIND = "(?<!\\\\)\\/";
    static final String REGEX_UNESCAPED_SLASH_LOOKAROUND = "(?<!\\\\)(?=\\/)";

    public static String getDisplayStringWithoutEscapes(String nameWithEscapes){
        return nameWithEscapes.replaceAll("\\\\/", "/");
    }

    public static String[] splitByUnescapedSlashes(String source){
        return source.split(REGEX_UNESCAPED_SLASH_LOOKBEHIND);
    }

    public static String[] splitByUnescapesSlashesWithoutRemovingThem(String source){
        return source.split(REGEX_UNESCAPED_SLASH_LOOKAROUND);
    }

    public static String getPathWithoutName(String pathWithName) {
        String[] split = NodePaths.splitByUnescapesSlashesWithoutRemovingThem(pathWithName);
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
        }
        return sum.toString();
    }

    public static String getNameWithoutPrefixSlash(String name) {
        return name.replaceAll(REGEX_UNESCAPED_SLASH_LOOKBEHIND, "");
    }

    public static String getPathWithoutTrailingSlash(String path){
        if(path.endsWith("/")){
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}

