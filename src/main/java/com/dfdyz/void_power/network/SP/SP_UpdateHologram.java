package com.dfdyz.void_power.network.SP;

import com.dfdyz.void_power.world.blocks.hologram_monitor.HologramScreenTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SP_UpdateHologram {

    public char c;
    public boolean enable;
    public BlockPos te;

    public SP_UpdateHologram(){

    }

    public SP_UpdateHologram(BlockPos b, boolean e, char c){
        this.c=c; this.enable = e; this.te = b;
    }

    public static SP_UpdateHologram decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        SP_UpdateHologram data = new SP_UpdateHologram();
        data.te = buf.readBlockPos();
        data.enable = buf.readBoolean();
        data.c = buf.readChar();
        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        buf.writeBoolean(enable);
        buf.writeChar(c);
    }

    public static void handler(SP_UpdateHologram msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(msg.te);
            if(be instanceof HologramScreenTE te){
                te.SetTrans(msg.enable);
                te.SetTransCol(msg.c);
                te.setTChanged();
            }
        });
    }

}
