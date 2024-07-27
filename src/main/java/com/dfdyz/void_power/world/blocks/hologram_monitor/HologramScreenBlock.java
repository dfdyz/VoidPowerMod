package com.dfdyz.void_power.world.blocks.hologram_monitor;

import com.dfdyz.void_power.registry.VPShapes;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.simibubi.create.foundation.utility.VoxelShaper;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorEdgeState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class HologramScreenBlock extends MonitorBlock {
    public static final String ID = "hologram_screen";


    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter blockReader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(ORIENTATION);
        if(direction != Direction.NORTH) return VPShapes.HOLO_SCREEN_O.get(state.getValue(FACING));
        MonitorEdgeState link_state = state.getValue(STATE);
        VoxelShaper shaper = VPShapes.ScreenStateShapeMap.get(link_state);
        return shaper.get(state.getValue(FACING).getOpposite());
    }

    public static VoxelShape Shape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 7.5 / 16.0, 1, 1, 8.5 / 16.0), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape ShapeO(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 7.5 / 16.0, 0, 1, 8.5 / 16.0, 1), BooleanOp.OR);
        return shape;
    }

    public HologramScreenBlock(Properties settings) {
        super(settings, ModRegistry.BlockEntities.MONITOR_ADVANCED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return VPTileEntities.HOLOGRAM_SCREEN_TE.create(blockPos, blockState);
    }
}
