package com.dfdyz.void_power.events;


import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.client.renderer.hud.VRGlassesHUDRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.valkyrienskies.core.impl.shadow.M;


@Mod.EventBusSubscriber(
        modid = VoidPowerMod.MODID,
        value = {Dist.CLIENT}
)
public class RenderEvent {


    @SubscribeEvent
    public static void OnPlayerRightClick(RenderGuiEvent.Pre event){
        GuiGraphics gg = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        float pt = event.getPartialTick();

        VRGlassesHUDRenderer.render(player, gg, pt);
    }



}
