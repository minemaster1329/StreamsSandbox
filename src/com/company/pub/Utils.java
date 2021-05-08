package com.company.pub;

import org.javatuples.Pair;

import java.util.List;
import java.util.function.Predicate;

public class Utils {
    public static <T> Integer getFirstMatchingItemIndex(List<T> list, Predicate<T> predicate){
        int output_el = 0;
        boolean found = false;
        for (T element : list){
            if (predicate.test(element)){
                found = true;
                break;
            }
            ++output_el;
        }

        return found ? output_el : -1;
    }
}
