package com.dfdyz.void_power.events;


import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;


@Mod.EventBusSubscriber(
        modid = VoidPowerMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ServerWorldEvent {

    @SubscribeEvent
    public static void OnServerStart(ServerStartingEvent event){
        //System.out.println("Restart RS net");
        ChannelNetworkHandler.reset();
    }
}
