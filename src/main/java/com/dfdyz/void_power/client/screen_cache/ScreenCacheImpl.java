package com.dfdyz.void_power.client.screen_cache;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public class ScreenCacheImpl implements IScreenCache {

    private final DynamicTexture dynTex;
    private final ResourceLocation loc;
    private NativeImage image;
    private IntBuffer buffer;

    private HologramTE be;
    private boolean needsUpdate;

    public ScreenCacheImpl(HologramTE be) {
        this.be = be;
        this.needsUpdate = true;
        dynTex = new DynamicTexture(be.width, be.high, true);
        loc = Minecraft.getInstance().getTextureManager().register(VoidPowerMod.MODID, dynTex);
    }

    public void invalidate() {
        needsUpdate = true;
    }

    public void cleanup() {
        be = null;
        Minecraft.getInstance().getTextureManager().release(loc);
    }

    public ResourceLocation getTexture() {
        if (be == null || be.buffer.length == 0) return null;
        Minecraft mc = Minecraft.getInstance();
        if(mc.getTextureManager().getTexture(loc) == null)
            mc.getTextureManager().register(loc, dynTex);
        if (needsUpdate) {
            load(be.width, be.high, be.buffer);
            needsUpdate = false;
        }
        return loc;
    }

    private void load(int w, int h, int[] img) {
        //int h = img.length / w;
        if (img.length != w * h) {
            System.err.println("Attempting to load an invalid texture");
            return;
        }
        if (image == null) {
            image = new NativeImage(w, h, false);
            getBuffer();
        } else if(image.getWidth() != w || image.getHeight() != h) {
            image.close();
            image = new NativeImage(w, h, false);
            getBuffer();
        }
        buffer.rewind();
        buffer.put(img);
        dynTex.upload();
    }

    private void getBuffer() {
        dynTex.setPixels(image);
        TextureUtil.prepareImage(dynTex.getId(), image.getWidth(), image.getHeight());
        buffer = MemoryUtil.memIntBuffer(image.pixels, image.getWidth() * image.getHeight());
    }

}
