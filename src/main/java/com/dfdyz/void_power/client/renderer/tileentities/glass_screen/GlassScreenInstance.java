package com.dfdyz.void_power.client.renderer.tileentities.glass_screen;

import com.dfdyz.void_power.client.renderer.tileentities.SimpleBlockVisual;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


public class GlassScreenInstance extends SimpleBlockVisual<GlassScreenTE> {
    public GlassScreenInstance(VisualizationContext context, GlassScreenTE blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }
}
