package com.dfdyz.void_power.client.renderer.tileentities.void_engine;

import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class VoidEngineTEInstance extends KineticBlockEntityVisual<VoidEngineTE> {
    protected final RotatingInstance shaft;
    final Direction direction;
    final Direction opposite;

    private double rotation;
    private double previousRotation;

    public VoidEngineTEInstance(VisualizationContext context, VoidEngineTE blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        opposite = direction.getOpposite();

        shaft = visualizationContext.instancerProvider().instancer(AllInstanceTypes.ROTATING,
                        Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();

        shaft.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, opposite)
                .setChanged();
    }


  /*  public VoidEngineTEInstance(MaterialManager materialManager, VoidEngineTE blockEntity) {
        super(materialManager, blockEntity);
        direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        opposite = direction.getOpposite();
        shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, opposite).createInstance();
        setup(shaft);
    }*/

   /* @Override
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
    }*/

    @Override
    public void update(float partialTick) {
        shaft.setup(blockEntity)
                .setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(shaft);
    }

    @Override
    public void updateLight(float v) {
        relight(pos, shaft);
    }

    @Override
    protected void _delete() {
        shaft.delete();
    }
}
