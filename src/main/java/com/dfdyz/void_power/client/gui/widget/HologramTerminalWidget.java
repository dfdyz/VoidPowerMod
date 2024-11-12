package com.dfdyz.void_power.client.gui.widget;

import com.dfdyz.void_power.client.screen_cache.ScreenCacheImpl;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import dan200.computercraft.core.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public class HologramTerminalWidget extends AbstractWidget {
    private static final Component DESCRIPTION = Component.translatable("gui.void_power.terminal");
    HologramTE te;

    private final BitSet keysDown = new BitSet(256);

    public HologramTerminalWidget(HologramTE te, int x, int y, int w, int h) {
        super(x, y, w, h, DESCRIPTION);
        this.te = te;
    }

    public boolean ShouldResize(){
        return te.getWidth() != this.width || te.getHeight() != this.height;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            if (te.getBuffer().length == 0)return;
            if (te.renderCache == null)te.renderCache = new ScreenCacheImpl(te);
            ResourceLocation tex = te.renderCache.getTexture();;
            if (tex == null)return;
            int w = this.getWidth();
            int h = this.getHeight();
            guiGraphics.blit(tex, this.getX(), this.getY(), 0.0F, 0.0F, w,h, w,h);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }


    private boolean inRegion(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (key >= 0 && this.keysDown.get(key)) {
            this.keysDown.set(key, false);
            te.SendInputPack("vp_key_released", te.name, key);
        }

        return true;
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        if (ch >= ' ' && ch <= '~' || ch >= 160 && ch <= 255) {
            te.SendInputPack("vp_char", te.name, Character.toString(ch));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == 256) {
            return false;
        } else if (Screen.isPaste(key)) {
            this.paste();
            return true;
        } else {
            if (key >= 0) {
                this.keysDown.set(key);
                te.SendInputPack("vp_key_pressed", te.name, key);

            }
            return true;
        }
    }

    private void paste() {
        String clipboard = StringUtil.normaliseClipboardString(Minecraft.getInstance().keyboardHandler.getClipboard());
        te.SendInputPack("vp_paste", te.name, clipboard);

    }


    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            for(int key = 0; key < this.keysDown.size(); ++key) {
                if (this.keysDown.get(key)) {
                    te.SendInputPack("vp_key_released", te.name, key);
                }
            }

            this.keysDown.clear();

            if (this.lastMouseButton >= 0) {
                te.SendInputPack("vp_mouse_clicked", te.name, lastMouseButton + 1, lastMouseX, lastMouseY);

                this.lastMouseButton = -1;
            }
        }
    }


    int lastMouseButton = -1;
    int lastMouseX = -1, lastMouseY = -1;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.inRegion(mouseX, mouseY)) {
            return false;
        } else if (button >= 0 && button <= 2) {
            int pxX = (int)Math.floor((mouseX - this.getX()) / width * te.getWidth());
            int pxY = (int)Math.floor((mouseY - this.getY()) / height * te.getHeight());

            te.SendInputPack("vp_mouse_clicked", te.name, button + 1, pxX, pxY);

            this.lastMouseButton = button;
            this.lastMouseX = pxX;
            this.lastMouseY = pxY;
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.inRegion(mouseX, mouseY)) {
            return false;
        } else if (button >= 0 && button <= 2) {
            int pxX = (int)Math.floor((mouseX - this.getX()) / width * te.getWidth());
            int pxY = (int)Math.floor((mouseY - this.getY()) / height * te.getHeight());

            if (this.lastMouseButton == button) {
                te.SendInputPack("vp_mouse_released", te.name, button + 1, pxX, pxY);

                this.lastMouseButton = -1;
            }

            this.lastMouseX = pxX;
            this.lastMouseY = pxY;
            return true;
        } else {
            return false;
        }
    }



    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!this.inRegion(mouseX, mouseY)) {
            return false;
        } else if (delta != 0.0) {
            int pxX = (int)Math.floor((mouseX - this.getX()) / width * te.getWidth());
            int pxY = (int)Math.floor((mouseY - this.getY()) / height * te.getHeight());
            te.SendInputPack("vp_mouse_scrolled", te.name, delta, pxX, pxY);

            this.lastMouseX = pxX;
            this.lastMouseY = pxY;
            return true;
        } else {
            return false;
        }
    }

}
