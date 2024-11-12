package com.dfdyz.void_power.network;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.network.CP.CP_HologramInputEvent;
import com.dfdyz.void_power.network.CP.CP_HologramRename;
import com.dfdyz.void_power.network.CP.CP_HologramUpdateRequest;
import com.dfdyz.void_power.network.SP.*;
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
                return PacketManager.VERSION;
            })
            .clientAcceptedVersions(PacketManager.VERSION::equals).serverAcceptedVersions(PacketManager.VERSION::equals).simpleChannel();

    static final String VERSION = "1.3";

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
        CHANNEL.registerMessage(index++, SP_UpdateGlassScreen.class,
                SP_UpdateGlassScreen::encode, SP_UpdateGlassScreen::decode,
                SP_UpdateGlassScreen::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(index++, SP_HologramUpdate_A.class,
                SP_HologramUpdate_A::encode, SP_HologramUpdate_A::decode,
                SP_HologramUpdate_A::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(index++, SP_HologramUpdate_B.class,
                SP_HologramUpdate_B::encode, SP_HologramUpdate_B::decode,
                SP_HologramUpdate_B::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(index++, SP_HologramPoseUpdate.class,
                SP_HologramPoseUpdate::encode, SP_HologramPoseUpdate::decode,
                SP_HologramPoseUpdate::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(index++, CP_HologramUpdateRequest.class,
                CP_HologramUpdateRequest::encode, CP_HologramUpdateRequest::decode,
                CP_HologramUpdateRequest::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(index++, CP_HologramInputEvent.class,
                CP_HologramInputEvent::encode, CP_HologramInputEvent::decode,
                CP_HologramInputEvent::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));


        CHANNEL.registerMessage(index++, SP_HologramRename.class,
                SP_HologramRename::encode, SP_HologramRename::decode,
                SP_HologramRename::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(index++, CP_HologramRename.class,
                CP_HologramRename::encode, CP_HologramRename::decode,
                CP_HologramRename::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //CHANNEL.registerMessage(index++, SP_UpdateAnimatedPiano.class, SP_UpdateAnimatedPiano::encode, SP_UpdateAnimatedPiano::decode, SP_UpdateAnimatedPiano::onClientMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(index++, CP_UpdateLampColor.class, CP_UpdateLampColor::encode, CP_UpdateLampColor::decode, CP_UpdateLampColor::onServerMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //CHANNEL.registerMessage(index++, SP_UpdateLampColor.class, SP_UpdateLampColor::encode, SP_UpdateLampColor::decode, SP_UpdateLampColor::onClientMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
