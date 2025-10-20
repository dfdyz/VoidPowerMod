package com.dfdyz.void_power.client.renderer.tileentities.hologram;

import com.dfdyz.void_power.client.renderer.tileentities.SimpleBlockVisual;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HologramInstance extends SimpleBlockVisual<HologramTE> {


    public HologramInstance(VisualizationContext context, HologramTE blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
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
