package com.dfdyz.void_power.utils;

import java.util.Arrays;

public class IntBuffer {

    final int[] buf;
    int top = 0;

    public IntBuffer(int capability) {
        this.buf = new int[capability];
    }

    public void push(int value){
        buf[top++] = value;
    }

    public int getCount(){
        return top;
    }

    public void clear(){
        top = 0;
    }

    public int[] getCutData(){
        return Arrays.copyOf(buf, top);
    }

}
