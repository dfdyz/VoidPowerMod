package com.dfdyz.void_power.world.blocks.engine_controller;

import com.dfdyz.void_power.registry.VPShapes;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.impl.shadow.F;

public class EngineControllerBlock extends HorizontalDirectionalBlock implements IBE<EngineControllerTE> {
    public static final String ID = "engine_controller";

    public EngineControllerBlock(Properties properties) {
        super(properties);
    }

    public static VoxelShape Shape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0001, 0.0001, 0.0001, 0.9999, 0.9999, 0.9999), BooleanOp.OR);
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VPShapes.ENGINE_CONTROLLER.get(state.getValue(FACING)) ;
    }

    @Override
    public Class<EngineControllerTE> getBlockEntityClass() {
        return EngineControllerTE.class;
    }

    @Override
    public BlockEntityType<? extends EngineControllerTE> getBlockEntityType() {
        return VPTileEntities.ENGINE_CONTROLLER_TE.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }


}
