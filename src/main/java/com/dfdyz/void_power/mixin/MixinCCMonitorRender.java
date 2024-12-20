package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.GlassScreenRenderState;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.render.monitor.MonitorBlockEntityRenderer;
import dan200.computercraft.client.render.monitor.MonitorRenderState;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MonitorBlockEntityRenderer.class, remap = false)
public abstract class MixinCCMonitorRender {

    @Inject(method = "render(Ldan200/computercraft/shared/peripheral/monitor/MonitorBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"), cancellable = true
    )
    private void renderPatch(MonitorBlockEntity monitor, float partialTicks, PoseStack transform, MultiBufferSource bufferSource, int lightmapCoord, int overlayLight, CallbackInfo ci){
        ClientMonitor originTerminal = monitor.getOriginClientMonitor();
        if(originTerminal == null){
            ci.cancel();
        }
        else {
            // if state is not created and not type for cc monitor.
            MonitorBlockEntity te = originTerminal.getOrigin();
            if(te instanceof GlassScreenTE gste){
            }
        }
    }


}
