package com.dfdyz.void_power.world.blocks.redstone_link;

import com.dfdyz.void_power.registry.VPShapes;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

public class RSRouterBlock extends Block implements IBE<RSRouterTE> {
    public static final String ID = "redstone_router";

    public RSRouterBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VPShapes.FULL_BLOCK;
    }

    @Override
    public Class<RSRouterTE> getBlockEntityClass() {
        return RSRouterTE.class;
    }

    @Override
    public BlockEntityType<? extends RSRouterTE> getBlockEntityType() {
        return VPTileEntities.RS_ROUTER_TE.get();
    }
}
