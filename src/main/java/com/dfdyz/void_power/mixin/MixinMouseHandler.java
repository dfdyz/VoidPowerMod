package com.dfdyz.void_power.mixin;

import com.dfdyz.void_power.client.gui.IMouseHandlerExtension;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler implements IMouseHandlerExtension {


    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private boolean mouseGrabbed;

    @Shadow
    private boolean ignoreFirstMove;

    @Shadow
    private double xpos;

    @Shadow
    private double ypos;


    @Override
    public void void_power$releaseMouseOnScreen() {
        if (this.mouseGrabbed) {
            this.mouseGrabbed = false;
            this.xpos = (double) this.minecraft.getWindow().getScreenWidth() / 2;
            this.ypos = (double) this.minecraft.getWindow().getScreenHeight() / 2;
            //InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
        }
    }

    @Override
    public void void_power$grabMouseOnScreen() {
        if (this.minecraft.isWindowActive() && !this.mouseGrabbed) {
            /*if (!Minecraft.ON_OSX) {
                KeyMapping.setAll();
            }*/

            this.mouseGrabbed = true;
            this.xpos = (double) this.minecraft.getWindow().getScreenWidth() / 2;
            this.ypos = (double) this.minecraft.getWindow().getScreenHeight() / 2;
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
            //this.minecraft.setScreen(null);
            this.minecraft.missTime = 10000;
            this.ignoreFirstMove = true;
        }
    }



}
