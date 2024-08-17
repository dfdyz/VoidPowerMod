package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.Config;
import dan200.computercraft.core.computer.computerthread.ComputerThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ComputerThread.class, remap = false)
public abstract class MixinComputerThread {


    @Inject(method = "scaledPeriod", at = @At("RETURN"), cancellable = true)
    public void scaledPeriodPatch(CallbackInfoReturnable<Long> cir){
        cir.setReturnValue((long) (cir.getReturnValue() * Config.DefaultMinPeriodFactor));
        cir.cancel();
    }

}
