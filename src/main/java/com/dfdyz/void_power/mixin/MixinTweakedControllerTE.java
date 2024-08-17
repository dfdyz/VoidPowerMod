package com.dfdyz.void_power.mixin;


import com.dfdyz.void_power.Config;
import com.getitemfromblock.create_tweaked_controllers.block.TweakedLecternControllerBlockEntity;
import com.getitemfromblock.create_tweaked_controllers.controller.ControllerRedstoneOutput;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = TweakedLecternControllerBlockEntity.class, remap = false)
public abstract class MixinTweakedControllerTE {

    @Shadow
    private ControllerRedstoneOutput output;

    @Inject(method = "stopUsing", at = @At("HEAD"), remap = false)
    public void ClearStatePatch(Player player, CallbackInfo ci){
        if(Config.ResetControllerWhileLeft)
            output.Clear();
    }

}
