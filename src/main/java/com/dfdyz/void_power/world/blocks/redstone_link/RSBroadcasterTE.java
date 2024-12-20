package com.dfdyz.void_power.world.blocks.redstone_link;

import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import com.dfdyz.void_power.world.redstone.IChannelHolder;
import com.dfdyz.void_power.world.redstone.ISignalSender;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RSBroadcasterTE extends SmartBlockEntity implements ISignalSender, IChannelHolder {
    protected int power;
    protected ResourceLocation channel = ChannelNetworkHandler.NULL_CHANNEL;

    public RSBroadcasterTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    public void initialize() {
        super.initialize();
        if(level.isClientSide) return;
        ChannelNetworkHandler.addSender(level, this);
        ChannelNetworkHandler.updateChannel(level, channel);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(level.isClientSide) return;
        ChannelNetworkHandler.removeSender(level, this);
        ChannelNetworkHandler.updateChannel(level, channel);
    }

    public void transmit(int power){
        if (this.power != power){
            this.power = power;
            ChannelNetworkHandler.updateChannel(level, channel);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public ResourceLocation getChannel() {
        return channel;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public boolean shouldRemove() {
        return isRemoved();
    }

    @Override
    public void setChannel(ResourceLocation c) {
        if(c != channel && level != null && !level.isClientSide){
            ChannelNetworkHandler.removeSender(level, this);
            channel = c;
            ChannelNetworkHandler.addSender(level, this);
        }
        else {
            channel = c;
        }
    }
}
