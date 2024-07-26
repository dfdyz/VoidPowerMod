package com.dfdyz.void_power.client.renderer.tileentities.hologram_monitor;

import com.dfdyz.void_power.world.blocks.hologram_monitor.HologramScreenTE;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dan200.computercraft.client.FrameInfo;
import dan200.computercraft.client.integration.ShaderMod;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.monitor.MonitorRenderState;
import dan200.computercraft.client.render.text.DirectFixedWidthFontRenderer;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.client.render.vbo.DirectVertexBuffer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.peripheral.monitor.MonitorRenderer;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_HEIGHT;
import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_WIDTH;
import static dan200.computercraft.core.util.Nullability.assertNonNull;

public class HologramScreenInstance extends BlockEntityInstance<HologramScreenTE> {
    public HologramScreenInstance(MaterialManager materialManager, HologramScreenTE blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected void remove() {

    }
}
