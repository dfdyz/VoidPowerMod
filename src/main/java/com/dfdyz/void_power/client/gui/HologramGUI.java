package com.dfdyz.void_power.client.gui;

import com.dfdyz.void_power.client.gui.widget.HologramTerminalWidget;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.network.CP.CP_HologramRename;
import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class HologramGUI extends Screen implements MenuAccess<HologramMenu> {
    HologramTE te;
    HologramTerminalWidget htw;

    EditBox name_editor;
    Button set_name;

    float terminal_scale = 1;
    final HologramMenu menu;

    public HologramGUI(HologramMenu menu, Inventory inventory, Component component) {
        super(component);
        this.menu = menu;
        if(menu != null){
            te = menu.te;
        }
    }

    float GetScale(){
        float w = 0.5f,h = 0.25f;
        
        int wi = te.getWidth();
        int hi = te.getHeight();
        
        
        if (hi <= 64) h = 2;
        else if (hi <= 128) h = 1;
        else if (hi <= 256) h = 0.5f;

        if (wi <= 64) w = 4;
        else if (wi <= 128) w = 2;
        else if (wi <= 256) w = 1;

        return Math.min(w, h);
    }

    HologramTerminalWidget getTerminal(){
        terminal_scale = GetScale();
        int wi = te.getWidth();
        int hi = te.getHeight();
        int w = (int) (wi * terminal_scale);
        int h = (int) (hi * terminal_scale);
        return addRenderableWidget(
                new HologramTerminalWidget(te, (width - w) / 2, (height - h) / 2, w, h)
        );
    }

    @Override
    protected void init() {
        super.init();
        htw = addRenderableWidget(getTerminal());

        name_editor = addRenderableWidget(
                new EditBox(font, width / 2 - 128 - 20, 1, 254, 16,
                Component.literal("NAME"))
        );

        name_editor.setValue(te.name);

        set_name = addRenderableWidget(Button.builder(Component.literal("Set"), this::ChangeName)
                .pos(width / 2 + 128-20, 1)
                .size(40,18)
                .build());

        setInitialFocus(htw);
    }

    public void ChangeName(Button b){
        te.Rename(name_editor.getValue());
        PacketManager.sendToServer(new CP_HologramRename(te));
    }

    @Override
    public void tick() {
        super.tick();
        if(te == null || te.isRemoved()) {
            Minecraft.getInstance().setScreen(null);
            return;
        }
        if(htw.ShouldResize()){
            terminal_scale = GetScale();
            int w = (int) (te.getWidth() * terminal_scale);
            int h = (int) (te.getHeight() * terminal_scale);
            htw.setX((width - w) / 2);
            htw.setY((height - h) / 2);
            htw.setHeight(h);
            htw.setWidth(w);

            if(htw.getY() + 29 < set_name.getHeight()){
                name_editor.setY(htw.getY() - 30);
                set_name.setY(htw.getY() - 31);
            }
            else {
                name_editor.setY(1);
                set_name.setY(1);
            }

        }
        //htw.setFocused(true);
    }

    @Override
    protected void setInitialFocus(GuiEventListener guiEventListener) {
        super.setInitialFocus(guiEventListener);
    }

    @Override
    public @NotNull HologramMenu getMenu() {
        return menu;
    }
}
