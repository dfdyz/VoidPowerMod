package com.dfdyz.void_power.client.renderer.tileentities.void_engine;

import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class VoidEngineTEInstance extends KineticBlockEntityInstance<VoidEngineTE> {
    protected final RotatingData shaft;
    final Direction direction;
    final Direction opposite;
    public VoidEngineTEInstance(MaterialManager materialManager, VoidEngineTE blockEntity) {
        super(materialManager, blockEntity);
        direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        opposite = direction.getOpposite();
        shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, opposite).createInstance();
        setup(shaft);
    }

    @Override
    protected void remove() {
        shaft.delete();
    }

    @Override
    public void updateLight() {
        relight(pos, shaft);
    }

    @Override
    public void update() {
        updateRotation(shaft);
    }
}
