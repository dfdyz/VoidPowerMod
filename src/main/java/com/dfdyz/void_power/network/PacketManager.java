package com.dfdyz.void_power.network;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.network.SP.SP_UpdateHologram;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketManager {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(VoidPowerMod.MODID, VoidPowerMod.MODID+"_channel")).networkProtocolVersion(() -> {
                return "1.1";
            })
            .clientAcceptedVersions("1.1"::equals).serverAcceptedVersions("1.1"::equals).simpleChannel();

    public PacketManager() {
    }

    public static <MSG> void sendToServer(MSG msg){
        CHANNEL.sendToServer(msg);
    }


    public static <MSG> void sendToClient(MSG message, PacketDistributor.PacketTarget packetTarget) {
        CHANNEL.send(packetTarget, message);
    }

    public static <MSG> void sendToAll(MSG message) {
        sendToClient(message, PacketDistributor.ALL.noArg());
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity) {
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        sendToClient(message, PacketDistributor.PLAYER.with(() -> player));
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayer entity) {
        sendToPlayer(message, entity);
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static <MSG> void sendToAllPlayerTrackingThisBlock(MSG message, BlockEntity te) {
        sendToClient(message, PacketDistributor.TRACKING_CHUNK.with(() -> te.getLevel().getChunkAt(te.getBlockPos())));
    }

    private static int index = 0;

    public static void Init(){
        CHANNEL.registerMessage(index++, SP_UpdateHologram.class, SP_UpdateHologram::encode, SP_UpdateHologram::decode, SP_UpdateHologram::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(index++, SP_UpdateAnimatedPiano.class, SP_UpdateAnimatedPiano::encode, SP_UpdateAnimatedPiano::decode, SP_UpdateAnimatedPiano::onClientMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(index++, CP_UpdateLampColor.class, CP_UpdateLampColor::encode, CP_UpdateLampColor::decode, CP_UpdateLampColor::onServerMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //CHANNEL.registerMessage(index++, SP_UpdateLampColor.class, SP_UpdateLampColor::encode, SP_UpdateLampColor::decode, SP_UpdateLampColor::onClientMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
