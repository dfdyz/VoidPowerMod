package com.dfdyz.void_power.network.SP;


import com.dfdyz.void_power.utils.ByteUtils;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.dfdyz.void_power.utils.ByteUtils.decodeString;

public class SP_HologramRename {
    public BlockPos te;
    public String name;


    public SP_HologramRename(){

    }

    public SP_HologramRename(HologramTE te){
        this.te = te.getBlockPos();
        this.name = te.name;
    }


    public static SP_HologramRename decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        SP_HologramRename data = new SP_HologramRename();
        data.te = buf.readBlockPos();
        data.name = decodeString(buf);
        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        ByteUtils.encodeString(buf, name);
    }

    public static void handler(SP_HologramRename msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getExistingBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                System.out.println("RENAME");
                te.Rename(msg.name);
                System.out.println("SUCESS RENAME");
            }
        });
    }
}
