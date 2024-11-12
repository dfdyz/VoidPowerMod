package com.dfdyz.void_power.world.blocks.hologram;

public class DefaultFrameBufferImpl implements IFrameBuffer{
    protected final int w,h;
    protected final int[] buffer;
    protected int initColor = 0x000000FF;

    public DefaultFrameBufferImpl(int w, int h){
        this.w = w;
        this.h = h;
        this.buffer = new int[w*h];
    }


    @Override
    public int[] getBuffer() {
        return buffer;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public IFrameBuffer resize(int w, int h) {
        return new DefaultFrameBufferImpl(w,h);
    }

    @Override
    public int getInitColor() {
        return initColor;
    }

    @Override
    public void setInitColor(int col) {
        initColor = col;
    }
}
