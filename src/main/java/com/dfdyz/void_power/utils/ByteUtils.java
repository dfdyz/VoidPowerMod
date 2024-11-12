package com.dfdyz.void_power.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

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

    public static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        byte[] compressedData = new byte[data.length];
        int compressedDataLength = deflater.deflate(compressedData);

        return Arrays.copyOf(compressedData, compressedDataLength);
    }

    public static byte[] compress(int[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {
            for (int number : data) {
                deflaterOutputStream.write((number >> 24) & 0xFF);
                deflaterOutputStream.write((number >> 16) & 0xFF);
                deflaterOutputStream.write((number >> 8) & 0xFF);
                deflaterOutputStream.write(number & 0xFF);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static int[] decompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(new java.io.ByteArrayInputStream(compressedData))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inflaterInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
        }
        byte[] decompressedData = byteArrayOutputStream.toByteArray();

        int[] result = new int[decompressedData.length / 4];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((decompressedData[i * 4] & 0xFF) << 24) |
                    ((decompressedData[i * 4 + 1] & 0xFF) << 16) |
                    ((decompressedData[i * 4 + 2] & 0xFF) << 8) |
                    (decompressedData[i * 4 + 3] & 0xFF);
        }
        return result;
    }

    public static final int maxLengthPerPack = 256 * 256 - 16;
}
