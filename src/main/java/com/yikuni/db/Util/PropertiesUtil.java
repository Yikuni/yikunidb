package com.yikuni.db.Util;

import java.util.List;

public class PropertiesUtil {
    public static String getListString(List<String> list){
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String str: list){
            builder.append(str);
            i++;
            if (i != list.size()){
                builder.append(',');
            }
        }
        return builder.toString();
    }

}
