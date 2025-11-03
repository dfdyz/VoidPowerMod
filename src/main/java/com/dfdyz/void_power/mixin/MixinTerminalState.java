package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.patched.IPatchedTerminalState;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import net.minecraft.network.FriendlyByteBuf;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TerminalState.class, remap = false)
public abstract class MixinTerminalState implements IPatchedTerminalState {

    @Unique
    private boolean void_power$TransMode = true;

    @Unique
    private char void_power$TransColor = 'f';

    @Override
    public boolean void_power$GetTransMode() {
        return void_power$TransMode;
    }

    @Override
    public char void_power$GetTransColor() {
        return void_power$TransColor;
    }

    @Override
    public void void_power$SetTransMode(boolean m) {
        void_power$TransMode = m;
    }

    @Override
    public void void_power$SetTransColor(char c) {
        void_power$TransColor = c;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    public void patchedInit(FriendlyByteBuf buf, CallbackInfo ci){
        void_power$TransMode = buf.readBoolean();
        void_power$TransColor = buf.readChar();
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void patchedWrite(FriendlyByteBuf buf, CallbackInfo ci){
        buf.writeBoolean(void_power$TransMode);
        buf.writeChar(void_power$TransColor);
    }
}
