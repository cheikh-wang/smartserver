package com.smartserver.core.util;

import java.util.List;

/**
 * author: cheikh.wang on 17/4/11
 * email: wanghonghi@126.com
 */
public class StringUtil {

    public static String implode(String[] pieces, String glue) {
        StringBuffer sb = new StringBuffer();
        if (pieces != null && pieces.length > 0) {
            for (int i = 0; i < pieces.length; i++) {
                if (i != 0) {
                    sb.append(glue);
                }
                sb.append(pieces[i]);
            }
        }
        return sb.toString();
    }

    public static String implode(List<String> pieces, String glue) {
        if (pieces == null) {
            return "";
        }
        return implode(pieces.toArray(new String[]{}), glue);
    }

    public static String trim(String source, String glue) {
        while (source.startsWith(glue)) {
            source = source.substring(1);
        }
        while (source.endsWith(glue)) {
            source = source.substring(0, source.length() - 1);
        }
        return source;
    }
}
