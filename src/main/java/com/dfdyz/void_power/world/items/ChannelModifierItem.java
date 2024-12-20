package com.dfdyz.void_power.world.items;

import com.dfdyz.void_power.menu.ChannelModifierMenu;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.world.blocks.redstone_link.RSBroadcasterTE;
import com.dfdyz.void_power.world.blocks.redstone_link.RSReceiverTE;
import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelModifierItem extends Item implements MenuProvider {
    public static final String ID = "channel_modifier";

    public ChannelModifierItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND){
            if(!player.level().isClientSide){
                ItemStack heldItem = player.getItemInHand(hand);
                NetworkHooks.openScreen((ServerPlayer) player, this, buf -> {
                    buf.writeItem(heldItem);
                });
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext uoc) {
        Player player = uoc.getPlayer();
        BlockPos bp = uoc.getClickedPos();
        if(player == null || player.level().isClientSide) return InteractionResult.PASS;

        if(!player.isShiftKeyDown()){
            BlockEntity te = player.level().getBlockEntity(bp);
            ResourceLocation c = getChannel(uoc.getItemInHand());

            if(te instanceof RSReceiverTE rte){
                if(canModify(c, rte.getChannel())){
                    player.displayClientMessage(Component.literal("Set channel to [" + c.toString() + "]."), false);
                    rte.setChannel(c);
                }
                else {
                    player.displayClientMessage(Component.literal("You don't have permission to modify this channel."), false);
                }
            } else if (te instanceof RSBroadcasterTE bcte) {
                if(canModify(c, bcte.getChannel())){
                    player.displayClientMessage(Component.literal("Set channel to [" + c.toString() + "]."), false);
                    bcte.setChannel(c);
                }
                else {
                    player.displayClientMessage(Component.literal("You don't have permission to modify this channel."), false);
                }
            }
        }
        return InteractionResult.PASS;
    }

    boolean canModify(ResourceLocation value, ResourceLocation original){
        if(!original.getNamespace().equals(ChannelNetworkHandler.NULL_CHANNEL.getNamespace())){
            //System.out.println(value.getNamespace() + "  " + original.getNamespace());
            return value.getNamespace().equals(original.getNamespace());
        }
        return true;
    }

    public static ResourceLocation getChannel(ItemStack is){
        CompoundTag nbt = is.getOrCreateTag();
        if(nbt.contains("channel")){
            return new ResourceLocation(nbt.getString("channel"));
        }
        return ChannelNetworkHandler.NULL_CHANNEL;
    }

    public static void setChannel(ItemStack is, ResourceLocation c){
        CompoundTag nbt = is.getOrCreateTag();
        nbt.putString("channel", c.toString());
        //System.out.println(c);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Channel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ChannelModifierMenu(VPTileEntities.CHANNEL_MODIFIER_GUI.get(), i, inventory, player.getMainHandItem());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltips, flag);
        CompoundTag nbt = itemStack.getOrCreateTag();
        if(nbt.contains("channel")){
            tooltips.add(Component.literal("Current channel [%s]".formatted(nbt.getString("channel"))));
        }
    }


}
