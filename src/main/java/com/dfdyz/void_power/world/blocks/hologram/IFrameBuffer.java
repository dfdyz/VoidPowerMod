package com.dfdyz.void_power.world.blocks.hologram;


public interface IFrameBuffer {
    int[] getBuffer();
    int getWidth();
    int getHeight();

    IFrameBuffer resize(int w, int h);

    int getInitColor();
    void setInitColor(int col);

    default boolean isTE(){
        return false;
    }
}
