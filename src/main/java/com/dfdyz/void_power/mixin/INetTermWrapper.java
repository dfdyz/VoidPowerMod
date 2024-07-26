package com.dfdyz.void_power.mixin;


import dan200.computercraft.core.terminal.Terminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Terminal.class, remap = false)
public interface INetTermWrapper {

    @Invoker("setChanged")
    void void_power$setChanged();

}
