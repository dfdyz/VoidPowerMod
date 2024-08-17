package com.dfdyz.void_power.network.CP;


import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CP_HologramInputEvent {
    public BlockPos te;
    public String name;
    public Object[] param;

    public CP_HologramInputEvent(){

    }

    public CP_HologramInputEvent(HologramTE te, String event, Object... param){
        this.te = te.getBlockPos();
        this.name = event;
        this.param = param;
    }

    static void encodeString(FriendlyByteBuf buf, String str){
        short len = (short) Math.min(str.length(), Short.MAX_VALUE);
        buf.writeShort(len);
        for (int i = 0; i < len; i++) {
            buf.writeChar(str.charAt(i));
        }
    }

    static String decodeString(FriendlyByteBuf buf){
        String str = "";
        int len = buf.readShort();
        for (int i = 0; i < len; ++i){
            str += buf.readChar();
        }
        return str;
    }

    public static CP_HologramInputEvent decode(FriendlyByteBuf buf) {
        //System.out.println("Received_DEC");
        CP_HologramInputEvent data = new CP_HologramInputEvent();
        data.te = buf.readBlockPos();

        data.name = decodeString(buf);
        data.param = new Object[buf.readShort()];

        Object obj;
        char type;
        for(int i = 0; i < data.param.length; ++i){
            type = buf.readChar();
            if(type == 'i'){
                obj = buf.readInt();
            }
            else if(type == 'd'){
                obj = buf.readDouble();
            }
            else if(type == 'f'){
                obj = buf.readFloat();
            }
            else if(type == 's'){
                obj = decodeString(buf);
            }
            else if(type == 'b'){
                obj = buf.readBoolean();
            }
            else{
                obj = null;
            }

            data.param[i] = obj;
        }

        //System.out.println("ClientMsg");

        /*
        for (int i = 0; i < data.param.length; i++) {
            System.out.println(data.param[i]);
        }*/

        return data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(te);

        encodeString(buf, name);
        buf.writeShort(param.length);

        Object obj;
        for (int i = 0; i < param.length; i++) {
            obj = param[i];
            if(obj instanceof Integer _int){
                buf.writeChar('i');
                buf.writeInt(_int);
            }
            else if(obj instanceof Double _double){
                buf.writeChar('d');
                buf.writeDouble(_double);
            }
            else if(obj instanceof Float _float){
                buf.writeChar('f');
                buf.writeDouble(_float);
            }
            else if(obj instanceof String str){
                buf.writeChar('s');
                encodeString(buf, str);
            }
            else if(obj instanceof Boolean bool){
                buf.writeChar('b');
                buf.writeBoolean(bool);
            }
            else {
                throw new RuntimeException("Unsupport type.");
            }
        }
    }

    public static void handler(CP_HologramInputEvent msg, Supplier<NetworkEvent.Context> context){
        NetworkEvent.Context ctx = context.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            BlockEntity be = context.get().getSender().level().getExistingBlockEntity(msg.te);
            if(be instanceof HologramTE te){
                if(te.peripheral != null){
                    te.peripheral.PushEvent(msg.name, msg.param);
                }
            }
        });
    }
}
