package com.dfdyz.void_power.utils;


import java.util.Map;

public class Debug {

    public static void PrintIntArray(int[] array){
        PrintIntArray(array, 16);
    }

    public static void PrintIntArray(int[] array, int line){
        StringBuilder a= new StringBuilder("{");
        for (int i = 0; i < array.length; i++) {
            if(i % line == 0) a.append("\n");
            a.append(Integer.toHexString(array[i])).append(", ");
        }
        a.append("\n}");
        System.out.println(a);
    }

    public static void PrintMap(Map<?,?> map){
        map.entrySet().forEach((entry -> {
            System.out.println(entry.getKey() + ", " + ((int)(double)entry.getValue()));
        }));
    }

}
