package com.chinanetcenter.api.util;

/**
 * Created by fuyz on 2014-10-08.
 */
public class StringUtil {

    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtil.isEmpty(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
