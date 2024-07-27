package com.dfdyz.void_power.events;


import com.dfdyz.void_power.VoidPowerMod;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(
        modid = VoidPowerMod.MODID,
        value = {Dist.CLIENT},
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientRegisterEvent {

    static ShaderInstance textShader;

    public static ShaderInstance text(){
        return textShader;
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        "void_power/rendertype_text",
                        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
                ),
                (s) -> {
                    textShader = s;
                });
    }






}
