package com.dfdyz.void_power.client.renderer;

import com.dfdyz.void_power.Config;
import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.events.ClientRegisterEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;

import java.util.function.Supplier;

public class VPRenderTypes {

    public static final ResourceLocation FONT = new ResourceLocation(VoidPowerMod.MODID, "textures/block/term_font.png");

    public static final ResourceLocation FONT_NEG = new ResourceLocation(VoidPowerMod.MODID, "textures/block/term_font_neg.png");

    //public static final ResourceLocation FONT = new ResourceLocation(VoidPowerMod.MODID, "textures/block/term_font.png");
    public static final RenderType TERMINAL = getText(VPRenderTypes.FONT);
    public static final RenderType TERMINAL_NEG = getText(VPRenderTypes.FONT_NEG);
    public static final RenderType HOLOGRAM = getHologram(FixedWidthFontRenderer.FONT);


    private static RenderType getText(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(VPRenderTypes::text))
                .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
                .setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                //.setOverlayState(RenderType.OV)
                .createCompositeState(false);
        return RenderType.create("cct_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
    }

    public static RenderType getHologram(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(VPRenderTypes::text))
                .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
                .setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                //.setOverlayState(RenderType.OV)
                .createCompositeState(false);
        return RenderType.create("cct_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
    }


    private static class CustomizableTextureState extends RenderStateShard.TextureStateShard
    {
        private CustomizableTextureState(ResourceLocation resLoc, Supplier<Boolean> blur, Supplier<Boolean> mipmap)
        {
            super(resLoc, blur.get(), mipmap.get());
            this.setupState = () -> {
                this.blur = blur.get();
                this.mipmap = mipmap.get();
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                texturemanager.getTexture(resLoc).setFilter(this.blur, this.mipmap);
                RenderSystem.setShaderTexture(0, resLoc);
            };
        }
    }

    public static ShaderInstance textShader;

    public static ShaderInstance text(){
        return Config.ForceUseVanillaShader ? textShader : RenderTypes.getTerminalShader();
    }

    public static ShaderInstance textNeg(){
        return Config.ForceUseVanillaShader ? textShader : RenderTypes.getTerminalShader();
    }
}
