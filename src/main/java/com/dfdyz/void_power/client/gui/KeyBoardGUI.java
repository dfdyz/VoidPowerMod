package com.dfdyz.void_power.client.gui;

import com.dfdyz.void_power.network.CP.CP_KeyBoardInputEvent;
import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.world.blocks.key_board.KeyBoardTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashSet;

public class KeyBoardGUI extends Screen {

    final KeyBoardTE te;
    final HashSet<Integer> pressedKeys = new HashSet<Integer>();

    public KeyBoardGUI(Component component, KeyBoardTE te) {
        super(component);
        this.te = te;
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics) {

    }



    private StringWidget text;
    @Override
    protected void init() {
        super.init();

        assert minecraft != null;
        assert minecraft.player != null;

        var mouseHandler = (IMouseHandlerExtension) minecraft.mouseHandler;
        mouseHandler.void_power$grabMouseOnScreen();
        //minecraft.player.displayClientMessage(Component.literal("Keyboard Active."), true);

        int mid_x = width / 2;

        text = new StringWidget(mid_x - 200, height - 68,
                400, 24,
                Component.translatable("gui.void_power.key_board.actived"),
                Minecraft.getInstance().font);


        addRenderableWidget(text);
    }

    @Override
    public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
        super.resize(p_96575_, p_96576_, p_96577_);

        int mid_x = width / 2;
        text.setPosition(mid_x - 200, height - 100);
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if(key == 256){ // ESC
            return super.keyPressed(key, scancode, modifiers);
        }

        sendEvent("key", key, pressedKeys.contains(key));

        // paste
        if(isPaste(key)){
            assert this.minecraft != null;
            String clipboard = this.minecraft.keyboardHandler.getClipboard();
            sendEvent("paste", clipboard);
        }

        pressedKeys.add(key);

        return true;
    }

    @Override
    public final boolean charTyped(char ch, int modifiers){
        sendEvent("char", ch);
        return super.charTyped(ch, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        sendEvent("key_up", key);
        pressedKeys.remove(key);
        return super.keyReleased(key, scancode, modifiers);
    }

    void sendEvent(String event, Object... data){
        if (te == null) return;
        PacketManager.sendToServer(new CP_KeyBoardInputEvent(te, event, data
        ));
    }

    @Override
    public void tick() {
        if(te == null || te.isRemoved()) {
            onClose();
            return;
        }

        if(!te.canPlayerUse(getMinecraft().player)){
            onClose();
            return;
        }

        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        pressedKeys.removeIf((key) -> {
            sendEvent("key", key, false);
            return true;
        });
        assert minecraft != null;
        var mouseHandler = (IMouseHandlerExtension) minecraft.mouseHandler;
        mouseHandler.void_power$releaseMouseOnScreen();
        super.onClose();
    }
}
