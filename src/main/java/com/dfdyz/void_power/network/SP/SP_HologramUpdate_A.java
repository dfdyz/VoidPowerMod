package com.dfdyz.void_power.network.SP;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static com.dfdyz.void_power.utils.ByteUtils.*;

@SuppressWarnings("rawtypes")
public class SP_HologramUpdate_A {
    public final BlockPos te;
    public final short width, height;
    public final int[] buffer;

    public SP_HologramUpdate_A(HologramTE te, int[] buffer){
        this.te = te.getBlockPos();
        width = (short) te.getWidth();
        height = (short) te.getHeight();
        this.buffer = buffer;
    }

    public SP_HologramUpdate_A(FriendlyByteBuf buf){
        te = buf.readBlockPos();
        width = buf.readShort();
        height = buf.readShort();
        int[] buffer = null;

        byte[] compressed = buf.readByteArray();

        try {
            buffer = decompress(compressed);
        } catch (IOException e) {
            System.out.println("decompress Filed");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        //System.out.println("Compressed size: " + compressed.length / 1024.f + "kb");
        //System.out.println("Data size: " + buffer.length * 4 / 1024.f + "kb");

        this.buffer = buffer;

        /*
        ranges = buf.readVarIntArray();
        elems = buf.readVarIntArray();
         */
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        buf.writeShort(width);
        buf.writeShort(height);
        try {
            buf.writeByteArray(compress(buffer));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static SP_HologramUpdate_A decode(FriendlyByteBuf buf){
        return new SP_HologramUpdate_A(buf);
    }

    public void handle(HologramTE te){
        int curr = 0;
        int[] bf = te.getBuffer();
        while (curr < buffer.length){
            int offset = buffer[curr++];
            int len = buffer[curr++];
            if (len >= 0) System.arraycopy(buffer, curr, bf, offset, len);
            curr += len;
        }
    }

    public static void handler(SP_HologramUpdate_A msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                te.resize(msg.width, msg.height);
                //System.out.println("Handle A.");
                try{
                    msg.handle(te);
                } catch (Exception e) {

                    e.printStackTrace();
                }
                te.UpdateRenderCache();
                //System.out.println("handled A");
            }
        });
    }
}
