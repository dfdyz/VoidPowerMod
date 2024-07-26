package com.dfdyz.void_power.mixin;

import com.dfdyz.void_power.patched.IPatchedNetTermAccessor;
import com.dfdyz.void_power.patched.IPatchedTermAccessor;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkedTerminal.class, remap = false)
public class MixinNetworkTerminal implements IPatchedNetTermAccessor {

    @Inject(method = "write", at = @At("RETURN"))
    public void patchWrite(FriendlyByteBuf buffer, CallbackInfo ci){
        IPatchedTermAccessor accessor = (IPatchedTermAccessor)this;
        buffer.writeBoolean(accessor.void_power$GetTransMode());
        buffer.writeChar(accessor.void_power$GetTransColor());
    }

    @Inject(method = "read", at = @At("RETURN"))
    public void patchRead(FriendlyByteBuf buffer, CallbackInfo ci){
        IPatchedTermAccessor accessor = (IPatchedTermAccessor)this;
        accessor.void_power$SetTransMode(buffer.readBoolean());
        accessor.void_power$SetTransColor(buffer.readChar());
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    public void patchWriteNBT(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir){
        nbt.putBoolean("trans_mode", ((IPatchedTermAccessor)this).void_power$GetTransMode());
        nbt.putByte("trans_color", (byte) ((IPatchedTermAccessor)this).void_power$GetTransColor());
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    public void patchWriteNBT(CompoundTag nbt, CallbackInfo ci){
        IPatchedTermAccessor accessor = (IPatchedTermAccessor)this;
        accessor.void_power$SetTransMode(nbt.getBoolean("trans_mode"));
        accessor.void_power$SetTransColor((char) nbt.getByte("trans_color"));
    }

    @Override
    public void void_power$SetTransMode2(boolean m) {
        IPatchedTermAccessor accessor = (IPatchedTermAccessor)this;
        accessor.void_power$SetTransMode(m);
        System.out.println("set m");
        this.void_power$setChanged2();
    }

    @Override
    public void void_power$SetTransColor2(char c) {
        IPatchedTermAccessor accessor = (IPatchedTermAccessor)this;
        accessor.void_power$SetTransColor(c);
        System.out.println("set c");
        this.void_power$setChanged2();
    }

    @Override
    public void void_power$setChanged2() {
        if(this instanceof INetTermWrapper t){
            t.void_power$setChanged();
        }
    }
}
