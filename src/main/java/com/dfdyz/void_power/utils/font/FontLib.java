package com.dfdyz.void_power.utils.font;

import com.google.common.collect.Maps;
import dan200.computercraft.api.lua.LuaException;

import java.util.HashMap;

public class FontLib implements IFontLib{
    public final HashMap<Character, Font.CharMat> char_map = Maps.newHashMap();

    public FontLib(){

    }

    public void put(char ch, boolean[][] charMat){
        char_map.put(ch, new Font.CharMat(charMat));
    }

    public Font.CharMat get(char ch) throws LuaException {
        if(char_map.containsKey(ch)){
            return char_map.get(ch);
        }
        else {
            if(char_map.containsKey('?')){
                return char_map.get('?');
            }
            if(ch >= 256){
                throw new LuaException(String.format(
                        "Char %s is not in lib. And not define default char '?'.",
                        convertToUnicodeStr(ch)
                ));
            }
            else {
                throw new LuaException(String.format("Char %s is not in lib. And not define default char '?'.", ""+ch));
            }
        }
    }


    static String convertToUnicodeStr(char c)
    {
        StringBuffer sb = new StringBuffer(12);
        int j;
        String tmp;
        sb.setLength(0);
        sb.append("\\u");
        j = (c >>>8); //取出高8位
        tmp = Integer.toHexString(j);
        if (tmp.length() == 1) sb.append("0");
        sb.append(tmp);
        j = (c & 0xFF); //取出低8位
        tmp = Integer.toHexString(j);
        if (tmp.length() == 1)
            sb.append("0");
        sb.append(tmp);
        return (new String(sb));
    }


}
