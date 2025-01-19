package com.dfdyz.void_power.mixin;

import com.dfdyz.void_power.patched.IPatchedTermAccessor;
import dan200.computercraft.core.terminal.Terminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Terminal.class, remap = false)
public class MixinTerminal implements IPatchedTermAccessor {
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
}
