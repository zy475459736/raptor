package com.ppdai.raptor.codegen2.java.util;

public final class StringUtils {
    public static String captureName(String value)
    {
        char[] ch = value.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static String lowerName(String value)
    {
        char[] ch = value.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }
}
