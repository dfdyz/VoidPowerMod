package com.dfdyz.void_power.menu;

import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.logging.Level;

public class HologramMenu extends AbstractContainerMenu {

    public final HologramTE te;

    // crate on client
    public HologramMenu(MenuType<?> type, int windowId, Inventory inv, @Nullable FriendlyByteBuf buffer) {
        super(VPTileEntities.HOLOGRAM_GUI.get(), windowId);
        te = getTe(buffer);
    }

    HologramTE getTe(FriendlyByteBuf buf){
        if(buf == null) return null;
        BlockPos bp = buf.readBlockPos();
        ClientLevel level = Minecraft.getInstance().level;

        BlockEntity blockEntity = level.getBlockEntity(bp);
        if(blockEntity instanceof HologramTE te){
            return te;
        }
        return null;
    }

    public HologramMenu(int windowId, HologramTE te){
        super(VPTileEntities.HOLOGRAM_GUI.get(), windowId);
        this.te = te;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return te != null && !te.isRemoved() && te.canPlayerUse(player);
    }
}
