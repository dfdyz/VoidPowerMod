package com.dfdyz.void_power.network.SP;


import com.dfdyz.void_power.utils.Debug;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// lazy update
public class SP_HologramUpdate {
    public BlockPos te;
    public boolean lazy = true;
    public short x, y, w, h;
    public int[] buffer;

    public SP_HologramUpdate(){

    }

    // full pack
    public SP_HologramUpdate(HologramTE te){
        this(te, 0,0, te.width, te.high, te.buffer);
        lazy = false;
    }

    // lazy pack
    public SP_HologramUpdate(HologramTE te, int x, int y, int w, int h, int[] lazy_buffer){
        this.te = te.getBlockPos();

        this.x = (short) x;
        this.y= (short) y;
        this.w = (short) w;
        this.h = (short) h;

        this.buffer = lazy_buffer;
    }
    public boolean checkPack(){
        return buffer.length == w*h;
    }

    public static SP_HologramUpdate decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        SP_HologramUpdate data = new SP_HologramUpdate();
        data.te = buf.readBlockPos();
        data.lazy = buf.readBoolean();

        data.x = buf.readShort();
        data.y = buf.readShort();
        data.w = buf.readShort();
        data.h = buf.readShort();

        int len = data.w*data.h;
        data.buffer = new int[len];

        for (int i = 0; i < len; i++) {
            data.buffer[i] = buf.readInt();
        }
        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        buf.writeBoolean(lazy);
        buf.writeShort(x);
        buf.writeShort(y);
        buf.writeShort(w);
        buf.writeShort(h);
        //Debug.PrintIntArray(buffer);
        for (int i = 0; i < buffer.length; i++) {
            buf.writeInt(buffer[i]);
        }
    }

    public static void handler(SP_HologramUpdate msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                te.handleLazyUpdatePack(msg);
            }
        });
    }
}
