package com.dfdyz.void_power.client.renderer.hud;


import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.client.screen_cache.ScreenCacheImpl;
import com.dfdyz.void_power.registry.VPItems;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.dfdyz.void_power.world.items.VRGlassesItem;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class VRGlassesHUDRenderer {

    public static ItemStack current_item = ItemStack.EMPTY;
    public static BlockPos bp = null;
    public static ResourceLocation dim = null;

    public static boolean On = false;

    static int distance_limit = 24;

    static final ResourceLocation no_signal = new ResourceLocation(VoidPowerMod.MODID, "textures/gui/hud/vr_glasses_no_signal.png");
    public static void render(LocalPlayer player, GuiGraphics guiGraphics, float partialTicks){
        if(!_render(player, guiGraphics, partialTicks)){
            // render no signal
            Window sr = Minecraft.getInstance().getWindow();
            float width = sr.getGuiScaledWidth();
            float height = sr.getGuiScaledHeight();
            if(width == 0 || height == 0)  return;

            float x = 0;
            float y = 0;
            float w = 128;
            float h = 64;

            if(width * h < height * w){
                h = width * h / w;
                w = width;
                y = (height - h) / 2;
            }
            else {
                w = height * w / h;
                h = height;
                x = (width - w) / 2;
            }


            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.setIdentity();
            boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);

            RenderSystem.disableCull();
            if (depthTestEnabled) {
                RenderSystem.disableDepthTest();
            }

            if (!blendEnabled) {
                RenderSystem.enableBlend();
            }

            RenderSystem.setShaderTexture(0, no_signal);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1,1,1, 0.1f);

            Matrix4f matrix4f = poseStack.last().pose();
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, x, y, 0)
                    .uv(0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, x, y + h, 0)
                    .uv(0, 1).endVertex();
            bufferbuilder.vertex(matrix4f, x + w, y + h, 0)
                    .uv(1, 1).endVertex();
            bufferbuilder.vertex(matrix4f, x + w, y, 0)
                    .uv(1, 0).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            poseStack.popPose();
        }
    }

    public static boolean _render(LocalPlayer player, GuiGraphics guiGraphics, float partialTicks){
        //if (player.getItemBySlot(EquipmentSlot.HEAD).getCapability())
        if(!On) return true;
        if(!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return true;

        if(player == null) return false;
        Level level = player.level();
        if(level == null) return false;

        ItemStack ish = player.getItemBySlot(EquipmentSlot.HEAD);

        float alpha = 1;
        if(!ish.is(VPItems.VR_GLASSES.get())){
            ish = player.getMainHandItem();
            alpha = 0.5f;
        }

        if(!ish.is(VPItems.VR_GLASSES.get())){
            current_item = ItemStack.EMPTY;
            bp = null;
            dim = null;
            return true;
        }

        if(ish != current_item){
            bp = VRGlassesItem.getTE(player, ish);
            dim = VRGlassesItem.getDim(ish);
            current_item = ish;
        }

        if(bp == null || dim == null)  return false;
        if(!player.level().dimension().location().equals(dim))  return false;

        BlockEntity te = player.level().getBlockEntity(bp);
        if(te instanceof HologramTE hte){
            if(VSGameUtilsKt.squaredDistanceToInclShips(player, bp.getX(), bp.getY(), bp.getZ()) > distance_limit * distance_limit){
                return false;
            }
            if (hte.getBuffer().length == 0) return false;
            if (hte.renderCache == null)hte.renderCache = new ScreenCacheImpl(hte);

            ResourceLocation tex = hte.renderCache.getTexture();
            if (tex == null)  return false;

            Window sr = Minecraft.getInstance().getWindow();
            float width = sr.getGuiScaledWidth();
            float height = sr.getGuiScaledHeight();
            if(width == 0 || height == 0)  return false;

            float x = 0;
            float y = 0;
            float w = hte.getWidth();
            float h = hte.getHeight();
            if(w == 0 || h == 0)  return false;

            if(width * h < height * w){
                h = width * h / w;
                w = width;
                y = (height - h) / 2;
            }
            else {
                w = height * w / h;
                h = height;
                x = (width - w) / 2;
            }

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.setIdentity();
                boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
                boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);

                RenderSystem.disableCull();
                if (depthTestEnabled) {
                    RenderSystem.disableDepthTest();
                }

                if (!blendEnabled) {
                    RenderSystem.enableBlend();
                }

                RenderSystem.setShaderTexture(0, tex);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1,1,1, alpha);

                Matrix4f matrix4f = poseStack.last().pose();
                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.vertex(matrix4f, x, y, 0)
                        .uv(0, 0).endVertex();
                bufferbuilder.vertex(matrix4f, x, y + h, 0)
                        .uv(0, 1).endVertex();
                bufferbuilder.vertex(matrix4f, x + w, y + h, 0)
                        .uv(1, 1).endVertex();
                bufferbuilder.vertex(matrix4f, x + w, y, 0)
                        .uv(1, 0).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());


            poseStack.popPose();
        }
        return true;
    }

}
