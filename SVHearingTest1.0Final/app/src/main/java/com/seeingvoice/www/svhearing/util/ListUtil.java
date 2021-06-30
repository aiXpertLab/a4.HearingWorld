package com.seeingvoice.www.svhearing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date:2019/8/16
 * Time:9:23
 * auther:zyy
 */
public class ListUtil {

    private static int sum = 0;

    public static <T>String ListAverage(List<T> list){
        int size = list.size();
        for (T item:list) {
            sum += Integer.valueOf(String.valueOf(item));
        }
        return String.valueOf(sum/size);
    }

    public static <T>List<T> ArrayToList(T[] array){
        return new ArrayList<>(Arrays.asList(array));
    }
}
