package sorm.utils;

public class StringUtils {
    /**
     * 第一个字母变大写 abc Abc
     * @param s  字符串
     * @return
     */
    public static String firstChar2Uppercase(String s){
        return s.toUpperCase().substring(0,1)+s.substring(1);
    }
}
