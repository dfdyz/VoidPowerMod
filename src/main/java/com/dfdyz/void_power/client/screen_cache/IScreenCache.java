package com.dfdyz.void_power.client.screen_cache;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public interface IScreenCache {
    void invalidate();
    void cleanup();
    ResourceLocation getTexture();
}
