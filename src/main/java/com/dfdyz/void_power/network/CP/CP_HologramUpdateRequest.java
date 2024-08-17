package com.dfdyz.void_power.network.CP;


import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CP_HologramUpdateRequest {
    public BlockPos te;

    public CP_HologramUpdateRequest(){

    }

    public CP_HologramUpdateRequest(HologramTE te){
        this.te = te.getBlockPos();
    }


    public static CP_HologramUpdateRequest decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        CP_HologramUpdateRequest data = new CP_HologramUpdateRequest();
        data.te = buf.readBlockPos();
        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
    }

    public static void handler(CP_HologramUpdateRequest msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = context.get().getSender().level().getExistingBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                te.returnFullUpdatePack(context.get().getSender());
            }
        });
    }
}
