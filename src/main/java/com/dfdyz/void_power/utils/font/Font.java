package com.dfdyz.void_power.utils.font;


import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Font {

    public static final HashMap<Character,CharMat> Char_Mat_MAP = Maps.newHashMap();

    public static class CharMat{
        public final boolean[][] bitmap;
        public final int width;
        public boolean get(int pos){
            return bitmap[pos / 16][pos % 16];
        }

        public CharMat(boolean[][] bitmap){
            this.bitmap = bitmap;
            width = bitmap[0].length;
        }

        public void Print(){
            for (int i = 0; i < bitmap.length; i++) {
                for (int j = 0; j < width; j++) {
                    System.out.print(bitmap[i][j] ? "@" : " ");
                }
                System.out.println("\n");
            }
        }

        public CharMat(){
            this.bitmap = new boolean[16][8];
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    bitmap[i][j] = false;
                }
            }
            width = 8;
        }
    }

    public static final CharMat UNKNOWN = new CharMat();

    public static CharMat getMat(char ch){
        if(Char_Mat_MAP.containsKey(ch)){
            return Char_Mat_MAP.get(ch);
        }
        else {
            if(ch < 0x80){
                // Default ASCII char
                return UNKNOWN;
            }
            else {
                try{
                    boolean[][] arr = new boolean[all_16_32][all_16_32];
                    int byteCount;
                    int lCount;
                    int[] code = getByteCode("" + ch);
                    byte[] data = read(code[0], code[1]);
                    byteCount = 0;
                    for (int line = 0; line < all_16_32; line++) {
                        lCount = 0;
                        for (int k = 0; k < all_2_4; k++) {
                            for (int j = 0; j < 8; j++) {
                                if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                                    arr[line][lCount] = true;
                                } else {
                                    arr[line][lCount] = false;
                                }
                                lCount++;
                            }
                            byteCount++;
                        }
                    }
                    CharMat cm = new CharMat(arr);
                    Char_Mat_MAP.put(ch, cm);
                    return cm;
                }
                catch (Exception e){
                    //e.printStackTrace();
                    Char_Mat_MAP.put(ch, UNKNOWN);
                    return UNKNOWN;
                }
            }
        }
    }

    private final static String ENCODE = "GB2312";
    //private final static String ZK16 = "HZK16";

    //private static boolean[][] arr;
    static int all_16_32 = 16;
    static int all_2_4 = 2;
    static int all_32_128 = 32;

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ResultOfMethodCallIgnored"})
    protected static byte[] read(int areaCode, int posCode) throws IOException {
        byte[] data = null;
        try {
            int area = areaCode - 0xa0;
            int pos = posCode - 0xa0;

            InputStream in = Minecraft.getInstance().getResourceManager()
                    .getResource(
                            new ResourceLocation("void_power","vp_font/hzk16.bin")
                    )
                    .get().open();
            long offset = all_32_128 * ((area - 1) * 94 + pos - 1);
            in.skip(offset);
            data = new byte[all_32_128];
            in.read(data, 0, all_32_128);
            in.close();
        } catch (Exception ex) {
            throw ex;
        }
        return data;
    }

    protected static int[] getByteCode(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes(ENCODE);
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }
    public static InputStream hzkFile;

    public static void Init(){
        try {
            hzkFile = Minecraft.getInstance().getResourceManager()
                    .getResource(
                            new ResourceLocation("void_power","vp_font/hzk16.bin")
                    )
                    .get().open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ASCII.Init();
    }

    static void PrintChar(char ch){
        getMat(ch).Print();
    }

}
