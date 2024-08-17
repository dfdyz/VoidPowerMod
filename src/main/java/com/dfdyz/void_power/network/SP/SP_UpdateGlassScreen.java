package com.dfdyz.void_power.network.SP;

import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SP_UpdateGlassScreen {

    public char c;
    public boolean enable;
    public BlockPos te;

    public SP_UpdateGlassScreen() {

    }

    public SP_UpdateGlassScreen(BlockPos b, boolean e, char c) {
        this.c = c;
        this.enable = e;
        this.te = b;
    }

    public static SP_UpdateGlassScreen decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        SP_UpdateGlassScreen data = new SP_UpdateGlassScreen();
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

    public static void handler(SP_UpdateGlassScreen msg, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(msg.te);
            if (be instanceof GlassScreenTE te) {
                te.SetTrans(msg.enable);
                te.SetTransCol(msg.c);
                te.setTChanged();
            }
        });
    }

}
