package com.rekhaninan.common;

public class NshareUtil {


    public static String
    removeNonAlphanumeric(String str)
    {
        // replace the given string
        // with empty string
        // except the pattern "[^a-zA-Z0-9]"
        str = str.replaceAll(
                "[^a-zA-Z]", "");

        // return string
        return str;
    }

}
