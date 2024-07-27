package com.dfdyz.void_power.client.renderer.tileentities.hologram_monitor;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dan200.computercraft.client.render.text.DirectFixedWidthFontRenderer;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.core.terminal.Palette;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.core.util.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

@SuppressWarnings("deprecation")
public class MCPipelineRenderer {

    /*
    public static float M_U0, M_V0, M_U1, M_V1;

    static final ResourceLocation texture = new ResourceLocation("void_power:block/term_font");

    static TextureAtlasSprite sprite;
    public static void SetUpUV(){
        sprite = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(texture);
    }

    public static final int Emissive_Light = 15728880;
    private static void drawChar(VertexConsumer emitter, Matrix4f mat, float x, float y, int index, byte[] colour) {
        if (index != 0 && index != 32) {
            int column = index % 16;
            int roll = index / 16;
            int xStart = 1 + column * 8;
            int yStart = 1 + roll * 11;
            quad(emitter, mat, x, y, x + 6.0F, y + 9.0F, 0.0F, colour, (float)xStart / 256.0F, (float)yStart / 256.0F, (float)(xStart + 6) / 256.0F, (float)(yStart + 9) / 256.0F);
        }
    }

    private static void quad(VertexConsumer buffer, Matrix4f mat,
                             float x1, float y1,
                             float x2, float y2,
                             float z,
                             byte[] rgba,
                             float u1, float v1,
                             float u2, float v2) {
        //buffer.quad(x1, y1, x2, y2, z, rgba, u1, v1, u2, v2);

        Vector4f v = mat.transform(new Vector4f(x1,y1,z,1));
        System.out.println(v);


        buffer.vertex(mat, x1, y1, z).color(rgba[0], rgba[1], rgba[2], 255).uv(sprite.getU(u1), sprite.getV(v1)).uv2(Emissive_Light).normal(1,0,0);
        buffer.vertex(mat, x1, y2, z).color(rgba[0], rgba[1], rgba[2], 255).uv(sprite.getU(u1), sprite.getV(v2)).uv2(Emissive_Light).normal(1,0,0);
        buffer.vertex(mat, x2, y2, z).color(rgba[0], rgba[1], rgba[2], 255).uv(sprite.getU(u2), sprite.getV(v2)).uv2(Emissive_Light).normal(1,0,0);
        buffer.vertex(mat, x1, y1, z).color(rgba[0], rgba[1], rgba[2], 255).uv(sprite.getU(u2), sprite.getV(v1)).uv2(Emissive_Light).normal(1,0,0);


        buffer.vertex(mat, x1, y1, z).color(rgba[0], rgba[1], rgba[2], 255).uv(u1, v1).uv2(Emissive_Light);//.normal(1,0,0);
        buffer.vertex(mat, x1, y2, z).color(rgba[0], rgba[1], rgba[2], 255).uv(u1, v2).uv2(Emissive_Light);//.normal(1,0,0);
        buffer.vertex(mat, x2, y2, z).color(rgba[0], rgba[1], rgba[2], 255).uv(u2, v2).uv2(Emissive_Light);//.normal(1,0,0);
        buffer.vertex(mat, x1, y1, z).color(rgba[0], rgba[1], rgba[2], 255).uv(u2, v1).uv2(Emissive_Light);//.normal(1,0,0);
    }

    private static void drawQuad(VertexConsumer emitter, Matrix4f mat, float x, float y, float width, float height, Palette palette, char colourIndex, char noBG_colourIdx) {
        if(noBG_colourIdx != colourIndex){
            byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(colourIndex, Colour.BLACK));
            quad(emitter, mat,x, y, x + width, y + height, 0.0F, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F);
        }
    }


    private static void drawQuad(VertexConsumer emitter, Matrix4f mat, float x, float y, float width, float height, Palette palette, char colourIndex) {
        byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(colourIndex, Colour.BLACK));
        quad(emitter, mat, x, y, x + width, y + height, 0.0F, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F);
    }


    public static void drawString(VertexConsumer emitter, Matrix4f mat, float x, float y, TextBuffer text, TextBuffer textColour, Palette palette) {
        for(int i = 0; i < text.length(); ++i) {
            byte[] colour = palette.getRenderColours(FixedWidthFontRenderer.getColour(textColour.charAt(i), Colour.BLACK));
            int index = text.charAt(i);
            if (index > 255) {
                index = '?';
            }

            drawChar(emitter, mat, x + (float)(i * 6), y, index, colour);
        }

    }

    public static void drawTerminalForeground(VertexConsumer emitter, Matrix4f mat, float x, float y, Terminal terminal) {
        Palette palette = terminal.getPalette();
        int height = terminal.getHeight();

        for(int i = 0; i < height; ++i) {
            float rowY = y + (float)(9 * i);
            drawString(emitter, mat, x, rowY, terminal.getLine(i), terminal.getTextColourLine(i), palette);
        }
    }

    private static void drawBackground(VertexConsumer emitter, Matrix4f mat, float x, float y, TextBuffer backgroundColour, Palette palette, float leftMarginSize, float rightMarginSize, float height, char noBG_colourIdx) {
        if (leftMarginSize > 0.0F) {
            drawQuad(emitter, mat, x - leftMarginSize, y, leftMarginSize, height, palette, backgroundColour.charAt(0), noBG_colourIdx);
        }

        if (rightMarginSize > 0.0F) {
            drawQuad(emitter, mat, x + (float)(backgroundColour.length() * 6), y, rightMarginSize, height, palette, backgroundColour.charAt(backgroundColour.length() - 1), noBG_colourIdx);
        }

        int blockStart = 0;
        char blockColour = 0;

        for(int i = 0; i < backgroundColour.length(); ++i) {
            char colourIndex = backgroundColour.charAt(i);
            if (colourIndex != blockColour) {
                if (blockColour != 0) {
                    drawQuad(emitter, mat,
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
            drawQuad(emitter, mat, x + (float)(blockStart * 6), y, (float)(6 * (backgroundColour.length() - blockStart)), height, palette, blockColour, noBG_colourIdx);
        }
    }


    public static void drawTerminalBackground(VertexConsumer emitter, Matrix4f mat, float x, float y, Terminal terminal, float topMarginSize, float bottomMarginSize, float leftMarginSize, float rightMarginSize, char noBG_color) {
        Palette palette = terminal.getPalette();
        int height = terminal.getHeight();
        drawBackground(emitter, mat, x, y - topMarginSize, terminal.getBackgroundColourLine(0), palette, leftMarginSize, rightMarginSize, topMarginSize, noBG_color);
        drawBackground(emitter, mat, x, y + (float) (height * 9), terminal.getBackgroundColourLine(height - 1), palette, leftMarginSize, rightMarginSize, bottomMarginSize, noBG_color);
        ;

        for (int i = 0; i < height; ++i) {
            float rowY = y + (float) (9 * i);
            drawBackground(emitter, mat, x, rowY, terminal.getBackgroundColourLine(i), palette, leftMarginSize, rightMarginSize, 9.0F, noBG_color);
        }

    }

    public static void drawCursor(VertexConsumer emitter, Matrix4f mat, float x, float y, Terminal terminal) {
        if (FixedWidthFontRenderer.isCursorVisible(terminal)) {
            byte[] colour = terminal.getPalette().getRenderColours(15 - terminal.getTextColour());
            drawChar(emitter, mat, x + (float) (terminal.getCursorX() * 6), y + (float) (terminal.getCursorY() * 9), 95, colour);
        }

    }

    private static void quad(VertexConsumer consumer, Matrix4f mat, float x1, float y1, float x2, float y2, float z, byte[] rgba, float u1, float v1, float u2, float v2, int light) {
        byte r = rgba[0];
        byte g = rgba[1];
        byte b = rgba[2];
        byte a = rgba[3];

        // pos col tex light_map normal pa
        consumer.vertex(mat, x1, y1, z).color(r, g, b, a).uv(u1, v1).uv2(light).endVertex();
        consumer.vertex(mat, x1, y2, z).color(r, g, b, a).uv(u1, v2).uv2(light).endVertex();
        consumer.vertex(mat, x2, y2, z).color(r, g, b, a).uv(u2, v2).uv2(light).endVertex();
        consumer.vertex(mat, x2, y1, z).color(r, g, b, a).uv(u2, v1).uv2(light).endVertex();
    }

    private static byte byteColour(float c) {
        return (byte)((int)(c * 255.0F));
    }

    private static final byte[] NORMAL;
    static {
        NORMAL = new byte[]{byteColour(0.3f), byteColour(0.3f), byteColour(0.5f), byteColour(0.3f)};
    }

    public static void drawEmptyTerminal(VertexConsumer vertexConsumer, float x, float y, float width, float height) {
        drawQuad(emitter, x, y, 0.0F, width, height, NORMAL, 15728880);
    }


    public static void drawQuad(VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, byte[] colour, int light) {
        quad(emitter, x, y, x + width, y + height, z, colour, 0.9765625F, 0.9765625F, 0.984375F, 0.984375F, light);
    }
*/

}
