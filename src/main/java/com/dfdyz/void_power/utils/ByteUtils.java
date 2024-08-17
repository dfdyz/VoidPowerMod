package com.dfdyz.void_power.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.Map;

public class ByteUtils {

    public static void encodeString(FriendlyByteBuf buf, String str){
        short len = (short) Math.min(str.length(), Short.MAX_VALUE);
        buf.writeShort(len);
        for (int i = 0; i < len; i++) {
            buf.writeChar(str.charAt(i));
        }
    }

    public static String decodeString(FriendlyByteBuf buf){
        String str = "";
        int len = buf.readShort();
        for (int i = 0; i < len; ++i){
            str += buf.readChar();
        }
        return str;
    }

}
