package com.dfdyz.void_power.client.renderer.tileentities;

import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.simibubi.create.foundation.block.IBE;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class SimpleBlockVisual<T extends BlockEntity> implements BlockEntityVisual<T> {
    public SimpleBlockVisual(VisualizationContext context, T blockEntity, float partialTick) {

    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

    }

    @Override
    public void update(float v) {

    }

    @Override
    public void delete() {

    }
}
