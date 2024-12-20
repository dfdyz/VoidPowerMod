package com.dfdyz.void_power.network.CP;

import com.dfdyz.void_power.registry.VPItems;
import com.dfdyz.void_power.world.items.ChannelModifierItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CP_RSI_ChannelModify {

    final ResourceLocation channel;

    public CP_RSI_ChannelModify(ResourceLocation channel){
        this.channel = channel;
    }

    public static CP_RSI_ChannelModify decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        ResourceLocation c = buf.readResourceLocation();
        return new CP_RSI_ChannelModify(c);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(channel);
    }


    public static void handler(CP_RSI_ChannelModify msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if(player != null){
                ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
                //System.out.println("AAA");
                if(item.is(VPItems.CHANNEL_MODIFIER.get())){
                    ChannelModifierItem.setChannel(item, msg.channel);
                    System.out.println("BBB");
                }
                //System.out.println("CCC");
            }
        });
    }

}
