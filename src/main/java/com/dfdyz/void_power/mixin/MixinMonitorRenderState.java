package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.GlassScreenRenderState;
import dan200.computercraft.client.render.monitor.MonitorRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MonitorRenderState.class, remap = false)
public abstract class MixinMonitorRenderState {

    @Inject(method = "destroyAll", at = @At("TAIL"))
    private static void DestroyAllPatch(CallbackInfo ci){
        GlassScreenRenderState.destroyAll();
    }

}
