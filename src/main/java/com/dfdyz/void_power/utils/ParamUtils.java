package com.dfdyz.void_power.utils;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamUtils {

    public static void isNumber(Object[] a, int arg) throws LuaException {
        if (arg >= a.length || !(a[arg] instanceof Double)) {
            throw new LuaException("Bad argument #" + (arg + 1) + ": (expected Number)");
        }
    }

    public static int getInt(Object[] a, int arg) throws LuaException {
        isNumber(a, arg);
        return (int) Math.floor((Double)a[arg]);
    }

    public static double getDouble(Object[] a, int arg) throws LuaException {
        isNumber(a, arg);
        return (Double)a[arg];
    }

    public static int convertColor(int col){
        return Integer.reverseBytes(col);
    }




    public static float rangeCheck(float x, float l, float r, int idx) throws LuaException {
        if(x > r || x < l) throw new LuaException("Param " + idx + " out of range.");
        return x;
    }


    public static int rangeCheck(int x, int l, int r, int idx) throws LuaException {
        if(x > r || x < l) throw new LuaException("Param " + idx + " out of range.");
        return x;
    }

    public static int blendColor(int up, int down){
        float aup = (up & 0xFF) / 255.f;
        float adown = (down & 0xFF) / 255.f;
        float a = Math.max(0.0001f, adown + aup);
        float a0 = aup / a;
        float a1 = adown / a;
        int color =  ((int) ((1 - (1-aup)*(1-adown)) * 0xFF));
        return color |
                ((int)(((up >> 8) & 0xFF) * a0 + ((down >> 8) & 0xFF) * a1) << 8) |
                ((int)(((up >> 16) & 0xFF) * a0 + ((down >> 16) & 0xFF) * a1) << 16) |
                ((int)(((up >> 24) & 0xFF) * a0 + ((down >> 24) & 0xFF) * a1) << 24);
    }

    public static String unicodeToStr(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static Map<Double,Object> dumpIntArray(int[] array){
        Map<Double,Object> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            map.put(i * 1.0, convertColor(array[i]));
        }
        return map;
    }

    static Pattern channel_pattern = Pattern.compile("^[a-z][a-z0-9_]*:[a-z][a-z0-9_/]*+$");
    public static boolean checkChannel(String s){
        if(channel_pattern.matcher(s).find()){
            return !new ResourceLocation(s).getNamespace().equals("null");
        }
        return false;
    }

}
