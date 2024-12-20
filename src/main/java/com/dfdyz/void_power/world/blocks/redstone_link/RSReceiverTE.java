package com.dfdyz.void_power.world.blocks.redstone_link;

import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import com.dfdyz.void_power.world.redstone.IChannelHolder;
import com.dfdyz.void_power.world.redstone.ISignalReceiver;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RSReceiverTE extends SmartBlockEntity implements ISignalReceiver, IChannelHolder {
    protected int power;
    protected ResourceLocation channel = ChannelNetworkHandler.NULL_CHANNEL;
    public RSReceiverTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) return;

        BlockState blockState = getBlockState();
        //boolean receivedSignalChanged = false;
        if ((getPower() > 0) != blockState.getValue(RSBroadcasterBlock.POWERED)) {
            //receivedSignalChanged = true;
            level.setBlockAndUpdate(worldPosition, blockState.cycle(RSBroadcasterBlock.POWERED));
        }
        /*
        if (receivedSignalChanged) {
            level.blockUpdated(worldPosition, level.getBlockState(worldPosition)
                    .getBlock());
        }

         */
    }


    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if(clientPacket) return;
        compound.putInt("power", power);
        compound.putString("channel", channel.toString());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if(clientPacket) return;
        power = compound.getInt("power");
        if (compound.contains("channel")){
            ResourceLocation c = new ResourceLocation(compound.getString("channel"));
            setChannel(c);
        }
    }

    @Override
    public void setChannel(ResourceLocation c){
        if(c != channel && level != null && !level.isClientSide){
            ChannelNetworkHandler.removeReceiver(level, this);
            channel = c;
            ChannelNetworkHandler.addReceiver(level, this);
            power = ChannelNetworkHandler.getPower(level, channel);
        }
        else {
            channel = c;
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if(level.isClientSide) return;
        power = ChannelNetworkHandler.getPower(level, channel);
        ChannelNetworkHandler.addReceiver(level, this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(level.isClientSide) return;
        power = ChannelNetworkHandler.getPower(level, channel);
        ChannelNetworkHandler.removeReceiver(level, this);
    }

    @Override
    public boolean shouldRemove() {
        return isRemoved();
    }

    @Override
    public ResourceLocation getChannel() {
        return channel;
    }

    public int getPower(){
        return power;
    }

    @Override
    public void updatePower(int power) {
        if(this.power != power){
            this.power = power;
        }
    }
}
