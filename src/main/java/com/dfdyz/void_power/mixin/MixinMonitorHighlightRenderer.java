package com.dfdyz.void_power.mixin;

import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.render.monitor.MonitorHighlightRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MonitorHighlightRenderer.class, remap = false)
public class MixinMonitorHighlightRenderer {

    @Inject(method = "drawHighlight", at = @At("HEAD"), cancellable = true)
    private static void renderPatcher(PoseStack transformStack, MultiBufferSource bufferSource, Camera camera, BlockHitResult hit, CallbackInfoReturnable<Boolean> cir){
        Level world = camera.getEntity().getCommandSenderWorld();
        //Entity entity = camera.getEntity();
        LocalPlayer player = Minecraft.getInstance().player;
        BlockPos pos = hit.getBlockPos();
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof GlassScreenTE) {
            if(player != null){
                if(player.getMainHandItem().isEmpty()){
                    cir.setReturnValue(true);
                    cir.cancel();
                    return;
                }
            }
            cir.setReturnValue(false);
            cir.cancel();
        }
    }


}
