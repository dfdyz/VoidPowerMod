package com.dfdyz.void_power.client.renderer.tileentities.hologram;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.client.screen_cache.ScreenCacheImpl;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class HologramRenderer extends SafeBlockEntityRenderer<HologramTE> {

    private static final Matrix3f IDENTITY_NORMAL = new Matrix3f().identity();
    public HologramRenderer(BlockEntityRendererProvider.Context context){
        super();
    }

    static final ResourceLocation tmp = new ResourceLocation(VoidPowerMod.MODID, "textures/block/void_engine_0.png");

    @Override
    protected void renderSafe(HologramTE te, float partialTicks, PoseStack stack, MultiBufferSource bufferSource, 
                              int plight, int overlay) {

        if (te.buffer.length == 0)return;
        if (te.renderCache == null)te.renderCache = new ScreenCacheImpl(te);

        ResourceLocation tex = te.renderCache.getTexture();
        if (tex == null)return;

        stack.pushPose();
        Direction facing = te.getDirection();
        stack.translate(0.5, 0.5, 0.5);

        if (facing.getAxis() != Direction.Axis.Y) {
            stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        } else {
            stack.mulPose(Axis.XP.rotationDegrees((float)(-facing.getStepY() * 90)));
        }

        stack.translate(0, 1, 0);

        Matrix4f model = new Matrix4f();
        model.translate(te.offx, te.offy, te.offz);
        model.rotate(Axis.YP.rotationDegrees(te.rotYaw));
        model.rotate(Axis.XP.rotationDegrees(te.rotPitch));
        model.rotate(Axis.ZP.rotationDegrees(te.rotRoll));

        stack.mulPoseMatrix(model);

        Matrix4f mat = stack.last().pose();
        Matrix3f nor = stack.last().normal();
        Vector3f n = new Vector3f((float)facing.getStepX(), (float)facing.getStepY(), (float)facing.getStepZ());
        n.mul(nor);
        VertexConsumer buf = bufferSource.getBuffer(RenderType.entityTranslucent(tex));

        float z = 0F;
        float w = te.width / 32.f * te.scalex;
        float h = te.high / 32.f * te.scaley;

        int light = 0xf000f0;
        buf.vertex(mat, w, h, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).overlayCoords(overlay).uv2(light).normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(mat, -w, h, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(light).normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(mat, -w, -h, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).overlayCoords(overlay).uv2(light).normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(mat, w, -h, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).overlayCoords(overlay).uv2(light).normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
        stack.popPose();

        /*
        stack.pushPose();
        Direction facing = te.getDirection();
        stack.translate(0.5d, 0.5d, 0.5d);

       // if (facing.getAxis() != Direction.Axis.Y)
            stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        //else {
            //stack.mulPose(Axis.XP.rotationDegrees(-facing.getStepY() * 90));
        //}

        // screen pos setup
        stack.translate(-0.5d, -0.5d, -0.5d);
        //stack.translate(0, 2, 0);

        //VertexConsumer buf = bufferSource.getBuffer(VPRenderTypes.HOLOGRAM);

        Matrix4f mat = stack.last().pose();
        Matrix3f nor = stack.last().normal();
        Vector3f n = new Vector3f(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        n.mul(nor);


        VertexConsumer buf = bufferSource.getBuffer(RenderType.entityTranslucent(tmp));

        float z = 1.001f;

        buf.vertex(mat, 1, 1, z)
                .color(1F, 1F, 1F, 1F).uv(1, 0)
                .overlayCoords(overlay)
                .uv2(0xf000f0)
                .normal(nor, 0.0F, 0.0F, 1.0F);
        buf.vertex(mat, 0, 1, z)
                .color(1F, 1F, 1F, 1F).uv(0, 0)
                .overlayCoords(overlay)
                .uv2(0xf000f0)
                .normal(nor, 0.0F, 0.0F, 1.0F);
        buf.vertex(mat, 0, 0, z)
                .color(1F, 1F, 1F, 1F).uv(0, 1)
                .overlayCoords(overlay)
                .uv2(0xf000f0)
                .normal(nor, 0.0F, 0.0F, 1.0F);
        buf.vertex(mat, 1, 0, z)
                .color(1F, 1F, 1F, 1F).uv(1, 1)
                .overlayCoords(overlay)
                .uv2(0xf000f0)
                .normal(nor, 0.0F, 0.0F, 1.0F);

        stack.popPose();*/

    }
}
