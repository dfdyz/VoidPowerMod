package com.dfdyz.void_power.client.renderer.tileentities.void_engine;

import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineBlock;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class VoidEngineTERenderer extends KineticBlockEntityRenderer<VoidEngineTE> {

    public VoidEngineTERenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    protected void renderSafe(VoidEngineTE be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;

        Direction direction = be.getBlockState()
                .getValue(VoidEngineBlock.HORIZONTAL_FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));
        //int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction));

        SuperByteBuffer shaftHalf =
                CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction.getOpposite());
        //SuperByteBuffer fanInner =
        //       CachedBufferer.partialFacing(AllPartialModels.ENCASED_FAN_INNER, be.getBlockState(), direction.getOpposite());

        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
        //kineticRotationTransform(fanInner, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
    }


}
