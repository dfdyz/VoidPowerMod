package com.dfdyz.void_power.world.redstone;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface ISignalSender {
    ResourceLocation getChannel();
    int getPower();
    boolean shouldRemove();
    //void setChannel(ResourceLocation c);
}
