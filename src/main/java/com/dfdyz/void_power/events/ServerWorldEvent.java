package com.dfdyz.void_power.events;


import com.dfdyz.void_power.VoidPowerMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

/*
@Mod.EventBusSubscriber(
        modid = VoidPowerMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
 */

public class ServerWorldEvent {
    /*
    @SubscribeEvent
    public static void OnPlayerRightClick(PlayerInteractEvent.RightClickEmpty event){
        System.out.println(FMLEnvironment.dist == Dist.CLIENT ? "ClientClick" : "ServerClick");
        System.out.println(event.getEntity());
    }*/
}
