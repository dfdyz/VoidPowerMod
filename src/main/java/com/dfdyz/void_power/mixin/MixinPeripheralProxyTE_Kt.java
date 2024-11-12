package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.Config;
import com.dfdyz.void_power.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import site.siredvin.peripheralworks.common.blockentity.PeripheralProxyBlockEntity;
import site.siredvin.peripheralworks.common.configuration.PeripheralWorksConfig;

@Mixin(value = PeripheralProxyBlockEntity.class, remap = false)
public abstract class MixinPeripheralProxyTE_Kt {
    @Inject(method = "isPosApplicable", at = @At("HEAD"), cancellable = true)
    public void distCheckVSPatch(BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if(Config.UnlimitDistance){
            cir.setReturnValue(true);
            cir.cancel();
        }
        /*
        BlockEntity te = (BlockEntity)(Object)this;
        Level level = te.getLevel();
        int max = PeripheralWorksConfig.INSTANCE.getPeripheralProxyMaxRange();
        if(level instanceof ServerLevel serverLevel){
            cir.setReturnValue(VSUtils.GetBlockDistanceSqrBetween(serverLevel, te.getBlockPos(), pos)
                    < max * max);
            //System.out.println(VSUtils.GetBlockDistanceSqrBetween(serverLevel, te.getBlockPos(), pos));
            cir.cancel();
        }*/
    }
}
