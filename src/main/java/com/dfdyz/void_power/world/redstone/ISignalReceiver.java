package com.dfdyz.void_power.world.redstone;

import net.minecraft.resources.ResourceLocation;

public interface ISignalReceiver {

    boolean shouldRemove();
    ResourceLocation getChannel();
    void updatePower(int power);
    //void setChannel(ResourceLocation c);

}
