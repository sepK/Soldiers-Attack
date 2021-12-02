package com.tinker.utils;

import org.springframework.util.StringUtils;

/**
 * 字符串工具类
 *
 * @author t.k
 * @date 2021/12/2 16:57
 */
public class StringUtil extends StringUtils {
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }
}
