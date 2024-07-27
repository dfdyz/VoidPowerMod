package com.dfdyz.void_power.client.renderer.tileentities.hologram_monitor;


import com.mojang.blaze3d.vertex.VertexConsumer;
import dan200.computercraft.client.render.text.DirectFixedWidthFontRenderer;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.core.terminal.Palette;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.core.util.Colour;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class ScreenRenderUtils {
    private static void drawChar(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, int index, byte[] colour) {
        if (index != 0 && index != 32) {
            int column = index % 16;
            int row = index / 16;
            int xStart = 1 + column * 8;
            int yStart = 1 + row * 11;
            quad(emitter, x, y, x + 6.0F, y + 9.0F, 0.0F, colour, (float)xStart / 256.0F, (float)yStart / 256.0F, (float)(xStart + 6) / 256.0F, (float)(yStart + 9) / 256.0F);
        }
    }

    private static void drawQuad(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, float width, float height, Palette palette, char colourIndex, char noBG_colourIdx) {
        if(noBG_colourIdx != colourIndex){
            byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(colourIndex, Colour.BLACK));
            quad(emitter, x, y, x + width, y + height, 0.0F, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F);
        }
    }

    private static void drawQuad(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, float width, float height, Palette palette, char colourIndex) {
        byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(colourIndex, Colour.BLACK));
        quad(emitter, x, y, x + width, y + height, 0.0F, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F);
    }


    public static void drawString(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, TextBuffer text, TextBuffer textColour, Palette palette) {
        for(int i = 0; i < text.length(); ++i) {
            byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(textColour.charAt(i), Colour.BLACK));
            int index = text.charAt(i);
            if (index > 255) {
                index = '?';
            }

            drawChar(emitter, x + (float)(i * 6), y, index, colour);
        }

    }

    public static void drawTerminalForeground(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, Terminal terminal) {
        Palette palette = terminal.getPalette();
        int height = terminal.getHeight();

        for(int i = 0; i < height; ++i) {
            float rowY = y + (float)(9 * i);
            drawString(emitter, x, rowY, terminal.getLine(i), terminal.getTextColourLine(i), palette);
        }

    }

    private static void drawBackground(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, TextBuffer backgroundColour, Palette palette, float leftMarginSize, float rightMarginSize, float height, char noBG_colourIdx) {
        if (leftMarginSize > 0.0F) {
            drawQuad(emitter, x - leftMarginSize, y, leftMarginSize, height, palette, backgroundColour.charAt(0), noBG_colourIdx);
        }

        if (rightMarginSize > 0.0F) {
            drawQuad(emitter, x + (float)(backgroundColour.length() * 6), y, rightMarginSize, height, palette, backgroundColour.charAt(backgroundColour.length() - 1), noBG_colourIdx);
        }

        int blockStart = 0;
        char blockColour = 0;

        for(int i = 0; i < backgroundColour.length(); ++i) {
            char colourIndex = backgroundColour.charAt(i);
            if (colourIndex != blockColour) {
                if (blockColour != 0) {
                    drawQuad(emitter,
                            x + (float)(blockStart * 6),
                            y,
                            (float)(6 * (i - blockStart)),
                            height, palette, blockColour, noBG_colourIdx);
                }

                blockColour = colourIndex;
                blockStart = i;
            }
        }

        if (blockColour != 0) {
            drawQuad(emitter, x + (float)(blockStart * 6), y, (float)(6 * (backgroundColour.length() - blockStart)), height, palette, blockColour, noBG_colourIdx);
        }
    }


    public static void drawTerminalBackground(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, Terminal terminal, float topMarginSize, float bottomMarginSize, float leftMarginSize, float rightMarginSize, char noBG_color) {
        Palette palette = terminal.getPalette();
        int height = terminal.getHeight();
        drawBackground(emitter, x, y - topMarginSize, terminal.getBackgroundColourLine(0), palette, leftMarginSize, rightMarginSize, topMarginSize, noBG_color);
        drawBackground(emitter, x, y + (float) (height * 9), terminal.getBackgroundColourLine(height - 1), palette, leftMarginSize, rightMarginSize, bottomMarginSize, noBG_color);;



        for (int i = 0; i < height; ++i) {
            float rowY = y + (float) (9 * i);
            drawBackground(emitter, x, rowY, terminal.getBackgroundColourLine(i), palette, leftMarginSize, rightMarginSize, 9.0F, noBG_color);
        }

    }

    public static void drawCursor(DirectFixedWidthFontRenderer.QuadEmitter emitter, float x, float y, Terminal terminal) {
        if (FixedWidthFontRenderer.isCursorVisible(terminal)) {
            byte[] colour = terminal.getPalette().getRenderColours(15 - terminal.getTextColour());
            drawChar(emitter, x + (float) (terminal.getCursorX() * 6), y + (float) (terminal.getCursorY() * 9), 95, colour);
        }

    }

    public static int getVertexCount(Terminal terminal) {
        return (terminal.getHeight() + 2) * (terminal.getWidth() + 2) * 2;
    }

    private static void quad(DirectFixedWidthFontRenderer.QuadEmitter buffer, float x1, float y1, float x2, float y2, float z, byte[] rgba, float u1, float v1, float u2, float v2) {
        buffer.quad(x1, y1, x2, y2, z, rgba, u1, v1, u2, v2);
    }


    static void quad(ByteBuffer buffer, float x1, float y1, float x2, float y2, float z, byte[] rgba, float u1, float v1, float u2, float v2) {
        int position = buffer.position();
        long addr = MemoryUtil.memAddress(buffer);
        if (position >= 0 && 112 <= buffer.limit() - position) {
            if ((addr & 3L) != 0L) {
                throw new IllegalStateException("Memory is not aligned");
            } else if (rgba.length != 4) {
                throw new IllegalStateException();
            } else {
                MemoryUtil.memPutFloat(addr + 0L, x1);
                MemoryUtil.memPutFloat(addr + 4L, y1);
                MemoryUtil.memPutFloat(addr + 8L, z);
                MemoryUtil.memPutByte(addr + 12L, rgba[0]);
                MemoryUtil.memPutByte(addr + 13L, rgba[1]);
                MemoryUtil.memPutByte(addr + 14L, rgba[2]);
                MemoryUtil.memPutByte(addr + 15L, (byte)255);
                MemoryUtil.memPutFloat(addr + 16L, u1);
                MemoryUtil.memPutFloat(addr + 20L, v1);
                MemoryUtil.memPutShort(addr + 24L, (short)240);
                MemoryUtil.memPutShort(addr + 26L, (short)240);
                MemoryUtil.memPutFloat(addr + 28L, x1);
                MemoryUtil.memPutFloat(addr + 32L, y2);
                MemoryUtil.memPutFloat(addr + 36L, z);
                MemoryUtil.memPutByte(addr + 40L, rgba[0]);
                MemoryUtil.memPutByte(addr + 41L, rgba[1]);
                MemoryUtil.memPutByte(addr + 42L, rgba[2]);
                MemoryUtil.memPutByte(addr + 43L, (byte)255);
                MemoryUtil.memPutFloat(addr + 44L, u1);
                MemoryUtil.memPutFloat(addr + 48L, v2);
                MemoryUtil.memPutShort(addr + 52L, (short)240);
                MemoryUtil.memPutShort(addr + 54L, (short)240);
                MemoryUtil.memPutFloat(addr + 56L, x2);
                MemoryUtil.memPutFloat(addr + 60L, y2);
                MemoryUtil.memPutFloat(addr + 64L, z);
                MemoryUtil.memPutByte(addr + 68L, rgba[0]);
                MemoryUtil.memPutByte(addr + 69L, rgba[1]);
                MemoryUtil.memPutByte(addr + 70L, rgba[2]);
                MemoryUtil.memPutByte(addr + 71L, (byte)255);
                MemoryUtil.memPutFloat(addr + 72L, u2);
                MemoryUtil.memPutFloat(addr + 76L, v2);
                MemoryUtil.memPutShort(addr + 80L, (short)240);
                MemoryUtil.memPutShort(addr + 82L, (short)240);
                MemoryUtil.memPutFloat(addr + 84L, x2);
                MemoryUtil.memPutFloat(addr + 88L, y1);
                MemoryUtil.memPutFloat(addr + 92L, z);
                MemoryUtil.memPutByte(addr + 96L, rgba[0]);
                MemoryUtil.memPutByte(addr + 97L, rgba[1]);
                MemoryUtil.memPutByte(addr + 98L, rgba[2]);
                MemoryUtil.memPutByte(addr + 99L, (byte)255);
                MemoryUtil.memPutFloat(addr + 100L, u2);
                MemoryUtil.memPutFloat(addr + 104L, v1);
                MemoryUtil.memPutShort(addr + 108L, (short)240);
                MemoryUtil.memPutShort(addr + 110L, (short)240);
                buffer.position(position + 112);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }




    // Empty Terminal
    private static void quad(FixedWidthFontRenderer.QuadEmitter c, float x1, float y1, float x2, float y2, float z, byte[] rgba, float u1, float v1, float u2, float v2, int light) {
        Matrix4f poseMatrix = c.poseMatrix();
        VertexConsumer consumer = c.consumer();
        byte r = rgba[0];
        byte g = rgba[1];
        byte b = rgba[2];
        byte a = rgba[3];

        // pos col tex light_map normal pa
        consumer.vertex(poseMatrix, x1, y1, z).color(r, g, b, a).uv(u1, v1).uv2(light).endVertex();
        consumer.vertex(poseMatrix, x1, y2, z).color(r, g, b, a).uv(u1, v2).uv2(light).endVertex();
        consumer.vertex(poseMatrix, x2, y2, z).color(r, g, b, a).uv(u2, v2).uv2(light).endVertex();
        consumer.vertex(poseMatrix, x2, y1, z).color(r, g, b, a).uv(u2, v1).uv2(light).endVertex();
    }

    private static byte byteColour(float c) {
        return (byte)((int)(c * 255.0F));
    }

    private static final byte[] NORMAL;
    static {
        NORMAL = new byte[]{byteColour(0.3f), byteColour(0.3f), byteColour(0.5f), byteColour(0.3f)};
    }

    public static void drawEmptyTerminal(FixedWidthFontRenderer.QuadEmitter emitter, float x, float y, float width, float height) {
        drawQuad(emitter, x, y, 0.0F, width, height, NORMAL, 15728880);
    }


    public static void drawQuad(FixedWidthFontRenderer.QuadEmitter emitter, float x, float y, float z, float width, float height, byte[] colour, int light) {
        quad(emitter, x, y, x + width, y + height, z, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F, light);
    }

}
