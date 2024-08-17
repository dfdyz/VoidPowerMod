package com.dfdyz.void_power.client.renderer.debug;

import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RaycastRender {

    public static void RenderColliderPanel(HologramTE te, Camera camera){
/*
        Vec3 camPos = camera.getPosition();
        float f = (float)(Mth.lerp(pt, this.xo, this.x) - camPos.x());
        float f1 = (float)(Mth.lerp(pt, this.yo, this.y) - camPos.y());
        float f2 = (float)(Mth.lerp(pt, this.zo, this.z) - camPos.z());







        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);

//		RenderSystem.disableTexture();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0F);

        bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        bufferBuilder.vertex(matrix, pos2.x, pos2.y, pos2.z).color(f1, f2, f3, 0.5F).normal(pos2.x - pos1.x, pos2.y - pos1.y, pos2.z - pos1.z).endVertex();
        bufferBuilder.vertex(matrix, pos1.x, pos1.y, pos1.z).color(f1, f2, f3, 0.5F).normal(pos2.x - pos1.x, pos2.y - pos1.y, pos2.z - pos1.z).endVertex();
        tesselator.end();
        poseStack.popPose();
*/



    }




}
