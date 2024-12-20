package com.dfdyz.void_power.client.gui;

import com.dfdyz.void_power.menu.ChannelModifierMenu;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.registry.VPItems;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.world.items.ChannelModifierItem;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelModifierGUI extends Screen implements MenuAccess<ChannelModifierMenu> {

    final ChannelModifierMenu menu;
    EditBox channel_editor;
    Button set_channel;

    public ChannelModifierGUI(ChannelModifierMenu menu, Inventory inventory, Component component) {
        super(component);
        this.menu = menu;
    }

    Pattern channel_pattern = Pattern.compile("^[a-z][a-z0-9_]*:[a-z][a-z0-9_/]*+$");


    @Override
    protected void init() {
        super.init();
        channel_editor = addRenderableWidget(
                new EditBox(font, width / 2 - 128, height / 2 - 8, 256, 16,
                        Component.literal("CHANNEL"))
        );

        channel_editor.setResponder((s -> {
            set_channel.active = ParamUtils.checkChannel(s);
        }));

        set_channel = addRenderableWidget(Button.builder(Component.literal("Set"),
                        this::SetChannel)
                .pos(width / 2 - 128, height / 2 + 9)
                .size(256,16)
                .build());

        //System.out.println("channel: " + menu.channel.toString());
        channel_editor.setValue(menu.channel.toString());
    }

    private void SetChannel(Button b){
        menu.channel = new ResourceLocation(channel_editor.getValue());
        ItemStack item = menu.player.getItemInHand(InteractionHand.MAIN_HAND);
        if(item.is(VPItems.CHANNEL_MODIFIER.get())){
            ChannelModifierItem.setChannel(item, menu.channel);
        }
        menu.SendHandUpdatePack();
    }

    @Override
    public @NotNull ChannelModifierMenu getMenu() {
        return menu;
    }
}
