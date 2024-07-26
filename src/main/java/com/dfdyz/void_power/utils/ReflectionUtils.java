package com.dfdyz.void_power.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static <T> T GetField(Class clazz, String fieldname){
        T val = null;
        try{
            Field field = clazz.getDeclaredField(fieldname);
            field.setAccessible(true);
            val = (T)field.get(clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return val;
    }

    public static <T> T GetField(Object obj, String fieldname){
        T val = null;
        try{
            Field field = obj.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
            val = (T)field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return val;
    }


    public static <T> void SetField(Object obj, String fieldname, T value){
        try{
            Field field = obj.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
