package com.dfdyz.void_power.world.blocks.redstone_link;

import com.dfdyz.void_power.registry.VPShapes;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.dfdyz.void_power.world.blocks.redstone_link.RSBroadcasterBlock.POWERED;

public class RSReceiverBlock extends Block implements IBE<RSReceiverTE> {
    public static final String ID = "redstone_receiver";
    public RSReceiverBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getBlockEntityOptional(blockAccess, pos).map(RSReceiverTE::getPower)
                .orElse(0);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() == oldState.getBlock() || isMoving)
            return;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;
        if (!worldIn.getBlockTicks()
                .willTickThisTick(pos, this))
            worldIn.scheduleTick(pos, this, 0);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return VPShapes.FULL_BLOCK;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level worldIn,
                                          BlockPos pos,
                                          Player player,
                                          InteractionHand handIn,
                                          BlockHitResult hit) {
        if(!worldIn.isClientSide){
            if(handIn == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()){
                BlockEntity te = worldIn.getBlockEntity(pos);
                ResourceLocation channel = ChannelNetworkHandler.NULL_CHANNEL;
                if(te instanceof RSReceiverTE rte){
                    channel = rte.getChannel();
                } else if (te instanceof RSBroadcasterTE bcte) {
                    channel = bcte.getChannel();
                }

                player.displayClientMessage(
                        Component.literal("Channel [??:%s]".formatted(channel.getPath()))
                        , false
                );
            }
        }
        return InteractionResult.PASS;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<RSReceiverTE> getBlockEntityClass() {
        return RSReceiverTE.class;
    }

    @Override
    public BlockEntityType<? extends RSReceiverTE> getBlockEntityType() {
        return VPTileEntities.RS_RECEIVER_TE.get();
    }
}
