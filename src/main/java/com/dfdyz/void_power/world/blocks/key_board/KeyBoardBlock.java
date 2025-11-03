package com.dfdyz.void_power.world.blocks.key_board;

import com.dfdyz.void_power.client.gui.KeyBoardGUI;
import com.dfdyz.void_power.registry.VPShapes;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.google.common.collect.ImmutableMap;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class KeyBoardBlock extends HorizontalDirectionalBlock implements IBE<KeyBoardTE> {
    public static final String ID = "key_board";
    public KeyBoardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    public static VoxelShape Shape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape,
                Shapes.box(
                        0.0001, 0.0001, 0.0001,
                        0.9999, 0.9999 / 8, 0.9999),
                BooleanOp.OR);
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> p_152459_) {
        return super.getShapeForEachState(p_152459_);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
        return VPShapes.KEYBOARD.get(state.getValue(FACING));
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level,
                                 @NotNull BlockPos pos, @NotNull Player player,
                                 @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        if(level.isClientSide && !player.isShiftKeyDown()){
            if(level.getBlockEntity(pos) instanceof KeyBoardTE te){
                Minecraft.getInstance().setScreen(new KeyBoardGUI(Component.literal(""), te));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public Class<KeyBoardTE> getBlockEntityClass() {
        return KeyBoardTE.class;
    }

    @Override
    public BlockEntityType<? extends KeyBoardTE> getBlockEntityType() {
        return VPTileEntities.KEYBOARD_TE.get();
    }
}
