package com.dfdyz.void_power.network.CP;


import com.dfdyz.void_power.utils.ByteUtils;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.dfdyz.void_power.utils.ByteUtils.decodeString;

public class CP_HologramRename {
    public BlockPos te;
    public String name;


    public CP_HologramRename(){

    }

    public CP_HologramRename(HologramTE te){
        this.te = te.getBlockPos();
        this.name = te.name;
    }


    public static CP_HologramRename decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        CP_HologramRename data = new CP_HologramRename();
        data.te = buf.readBlockPos();

        data.name = decodeString(buf);

        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        ByteUtils.encodeString(buf, name);
    }

    public static void handler(CP_HologramRename msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = context.get().getSender().level().getExistingBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                te.Rename(msg.name);
            }
        });
    }
}
