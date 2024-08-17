package com.dfdyz.void_power.network.SP;


import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// lazy update
public class SP_HologramPoseUpdate {
    public BlockPos te;
    public float x, y, z, yaw, pitch, roll, sx, sy;


    public SP_HologramPoseUpdate(){

    }

    public SP_HologramPoseUpdate(HologramTE te){
        this.te = te.getBlockPos();
        x = te.offx;
        y = te.offy;
        z = te.offz;
        yaw = te.rotYaw;
        pitch = te.rotPitch;
        roll = te.rotRoll;
        sx = te.scalex;
        sy = te.scaley;
    }



    public static SP_HologramPoseUpdate decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        SP_HologramPoseUpdate data = new SP_HologramPoseUpdate();
        data.te = buf.readBlockPos();

        data.x = buf.readFloat();
        data.y = buf.readFloat();
        data.z = buf.readFloat();
        data.yaw = buf.readFloat();
        data.pitch = buf.readFloat();
        data.roll = buf.readFloat();
        data.sx = buf.readFloat();
        data.sy = buf.readFloat();


        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeFloat(roll);
        buf.writeFloat(sx);
        buf.writeFloat(sy);
    }

    public static void handler(SP_HologramPoseUpdate msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                te.offx = msg.x;
                te.offy = msg.y;
                te.offz = msg.x;
                te.rotYaw = msg.yaw;
                te.rotPitch = msg.pitch;
                te.rotRoll = msg.roll;
                te.scalex = msg.sx;
                te.scaley = msg.sy;
            }
        });
    }
}
