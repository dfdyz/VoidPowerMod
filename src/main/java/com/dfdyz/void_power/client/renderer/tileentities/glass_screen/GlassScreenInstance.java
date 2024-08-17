package com.dfdyz.void_power.client.renderer.tileentities.glass_screen;

import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;

public class GlassScreenInstance extends BlockEntityInstance<GlassScreenTE> {
    public GlassScreenInstance(MaterialManager materialManager, GlassScreenTE blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected void remove() {

    }
}
