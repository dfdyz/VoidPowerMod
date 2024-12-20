package com.dfdyz.void_power.world.blocks.hologram;

import com.dfdyz.void_power.client.gui.HologramGUI;
import com.dfdyz.void_power.registry.VPShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.dfdyz.void_power.registry.VPTileEntities.HOLOGRAM_TE;

public class HologramBlock extends HorizontalDirectionalBlock implements IBE<HologramTE> {

    public static final String ID = "hologram";

    public HologramBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        return VPShapes.HOLOGRAM.get(state.getValue(FACING)) ;
    }

    @Override
    public Class getBlockEntityClass() {
        return HologramTE.class;
    }

    @Override
    public BlockEntityType getBlockEntityType() {
        return HOLOGRAM_TE.get();
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level,
                                 @NotNull BlockPos pos, @NotNull Player player,
                                 @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        if(!level.isClientSide && !player.isShiftKeyDown()){
            if(level.getBlockEntity(pos) instanceof HologramTE te){
                //Minecraft.getInstance().setScreen(new HologramGUI(te));
                NetworkHooks.openScreen((ServerPlayer) player, te, te.getBlockPos());
                return InteractionResult.SUCCESS;

                //level.getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(/*范围自己设置*/), LivingEntity::isAlive);

            }
        }
        return InteractionResult.PASS;
    }
}
