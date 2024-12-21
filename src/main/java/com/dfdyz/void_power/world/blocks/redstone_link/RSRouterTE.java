package com.dfdyz.void_power.world.blocks.redstone_link;

import com.dfdyz.void_power.compat.cct.peripherals.P_RSRouter;
import com.dfdyz.void_power.utils.SyncLocker;
import com.dfdyz.void_power.world.redstone.ChannelNetworkHandler;
import com.dfdyz.void_power.world.redstone.ISignalReceiver;
import com.dfdyz.void_power.world.redstone.ISignalSender;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class RSRouterTE extends SmartBlockEntity {
    private P_RSRouter peripheral;
    private LazyOptional<IPeripheral> peripheralCap;

    public final ResourceLocation[] channels;
    public final boolean[] modes; // true = input_mode, false = output_mode
    public final ITickableHolder[] holders;


    static final int maxChannels = 16;

    public final SyncLocker<Boolean> dirty = new SyncLocker<>(true);
    final int[] powers = new int[maxChannels];

    public RSRouterTE(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        channels = new ResourceLocation[maxChannels];
        modes = new boolean[maxChannels];
        holders = new ITickableHolder[maxChannels];
        Arrays.fill(channels, ChannelNetworkHandler.NULL_CHANNEL);
        Arrays.fill(modes, false);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            if(this.peripheral == null){
                this.peripheral = new P_RSRouter(this);
            }
            if(peripheralCap == null || !peripheralCap.isPresent())
                peripheralCap =  LazyOptional.of(() -> this.peripheral);
            return peripheralCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public int getPortMode(int i) throws LuaException {
        if(i< 0 || i >= maxChannels) throw new LuaException("Port id out of range. max is 0-"+ (maxChannels-1));
        ITickableHolder h = holders[i];
        if(h == null){
            return 0; // closed
        }
        else if(h instanceof ISignalReceiver){
            return 1;
        }
        else {
            return 2;
        }
    }

    public boolean openPort(int i, ResourceLocation channel, boolean mode) throws LuaException {
        if(i< 0 || i >= maxChannels) throw new LuaException("Port id out of range. max is 0-"+ (maxChannels-1));
        if(holders[i] != null){
            return false;
        }

        channels[i] = channel;
        modes[i] = mode;

        if(modes[i]){ // receiver
            //System.out.println("Add Receiver on:" + channel);
            holders[i] = new RouterReceiver(this, channel);
            ChannelNetworkHandler.addReceiver(level, (ISignalReceiver) holders[i]);

        }
        else { // sender
            //System.out.println("Add Sender on:" + channel);
            holders[i] = new RouterSender(this, channel);
            ChannelNetworkHandler.addSender(level, (ISignalSender) holders[i]);
        }

        return true;
    }

    public void closePort(int i) throws LuaException {
        if(i< 0 || i >= maxChannels) throw new LuaException("Port id out of range. max is 0-"+ (maxChannels-1));
        channels[i] = ChannelNetworkHandler.NULL_CHANNEL;
    }

    public int getPower(int p) throws LuaException {
        if(p< 0 || p >= maxChannels) throw new LuaException("Port id out of range. max is 0-"+ (maxChannels-1));
        ITickableHolder h = holders[p];
        if(h != null){
            return h.getPower();
        }
        return -1;
    }

    public void process(){
        if(!dirty.getThenSet(true)){
            //level.scheduleTick(getBlockPos(), VPBlocks.RS_ROUTER_BLOCK.get(), 0);
        }
    }

    public boolean setPower(int port, int power) throws LuaException {
        if(port< 0 || port >= maxChannels) throw new LuaException("Port id out of range 0-"+ (maxChannels-1));
        if(power < 0 || power > 15) throw new LuaException("Power out of range 0-15");
        ITickableHolder h = holders[port];
        if(h instanceof RouterSender s){
            s.setPower(power);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide) return;

        if(!dirty.getThenSet(false)) return;
        for (int i = 0; i < channels.length; i++) {
            ResourceLocation channel = channels[i];
            boolean is_empty = channel.equals(ChannelNetworkHandler.NULL_CHANNEL);
            if(!is_empty && holders[i] == null){
                if(modes[i]){ // receiver
                    holders[i] = new RouterReceiver(this, channel);
                    ChannelNetworkHandler.addReceiver(level, (ISignalReceiver) holders[i]);
                }
                else { // sender
                    holders[i] = new RouterSender(this, channel);
                    ChannelNetworkHandler.addSender(level, (ISignalSender) holders[i]);
                    holders[i].setPower(powers[i]);
                }
            }
            else if(is_empty && holders[i] != null){
                ITickableHolder h = holders[i];
                if(h instanceof ISignalReceiver r){
                    ChannelNetworkHandler.removeReceiver(level, r);
                }
                else if(h instanceof ISignalSender s){
                    ChannelNetworkHandler.removeSender(level, s);
                    ChannelNetworkHandler.updateChannel(level, s.getChannel());
                }
                holders[i] = null;
            }
        }

        ITickableHolder h;
        for (ITickableHolder holder : holders) {
            h = holder;
            if (h != null) {
                h.updateSelf();
            }
        }

    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if(clientPacket) return;
        ListTag saved_channels = new ListTag();
        ITickableHolder h;
        for (int i = 0; i < holders.length; ++i) {
            h = holders[i];
            if(h != null){
                CompoundTag saved_channel = new CompoundTag();
                saved_channel.putString("c", h.getChannel().toString());
                saved_channel.putBoolean("m", h instanceof ISignalReceiver);
                saved_channel.putByte("pw", (byte) h.getPower());
                saved_channel.putByte("pr", (byte) i);
                saved_channels.add(saved_channel);
            }
        }
        tag.put("saved_channels", saved_channels);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if(clientPacket) return;
        if(tag.contains("saved_channels")){
            ListTag saved_channels = tag.getList("saved_channels" , Tag.TAG_COMPOUND);
            CompoundTag t;
            for (int i = 0; i < saved_channels.size(); i++) {
                t = saved_channels.getCompound(i);
                int p = t.getByte("pr");
                int pw = t.getByte("pw");
                ResourceLocation c = new ResourceLocation(t.getString("c"));
                boolean m = t.getBoolean("m");
                if(holders[p] != null) continue;
                channels[p] = c;
                modes[p] = m;
                powers[p] = pw;
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        for (ITickableHolder h : holders) {
            if(h != null){
                if(h instanceof ISignalReceiver r){
                    //System.out.println("Remove Receiver on:" + h.getChannel());
                    ChannelNetworkHandler.removeReceiver(level, r);
                }
                else if(h instanceof ISignalSender s){
                    //System.out.println("Remove Sender on:" + h.getChannel());
                    ChannelNetworkHandler.removeSender(level, s);
                    ChannelNetworkHandler.updateChannel(level, s.getChannel());
                }
            }
        }
        Arrays.fill(holders, null);
    }

    static class RouterSender implements ISignalSender,ITickableHolder {
        boolean dirty = true;

        boolean removed = false;
        ResourceLocation channel;
        ResourceLocation channelNew;
        RSRouterTE parent;
        int power = 0;

        RouterSender(RSRouterTE parent, ResourceLocation c){
            channel = c;
            channelNew = c;
            this.parent = parent;
        }

        @Override
        public void updateSelf(){
            if(channel == ChannelNetworkHandler.NULL_CHANNEL) return;

            if(channel != channelNew){
                ChannelNetworkHandler.removeSender(parent.level, this);
                ChannelNetworkHandler.updateChannel(parent.level, channel);
                channel = channelNew;
                ChannelNetworkHandler.addSender(parent.level, this);
                ChannelNetworkHandler.updateChannel(parent.level, channel);
                dirty = false;
            }

            if(dirty){
                ChannelNetworkHandler.updateChannel(parent.level, channel);
                dirty = false;
            }
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
            return removed || parent == null || parent.isRemoved();
        }

        //@Override
        void setChannel(ResourceLocation c) {
            channelNew = c;
        }

        @Override
        public void setPower(int p){
            if(power != p){
                power = p;
                dirty = true;
            }
        }
    }

    static class RouterReceiver implements ISignalReceiver,ITickableHolder {
        boolean removed = false;
        ResourceLocation channel;
        ResourceLocation channelNew;
        RSRouterTE parent;
        int power = 0;

        RouterReceiver(RSRouterTE parent, ResourceLocation c){
            channel = c;
            channelNew = c;
            this.parent = parent;
        }

        @Override
        public void updateSelf(){
            if(channel == ChannelNetworkHandler.NULL_CHANNEL) return;
            if(channel != channelNew){
                ChannelNetworkHandler.removeReceiver(parent.level, this);
                channel = channelNew;
                ChannelNetworkHandler.addReceiver(parent.level, this);
                power = ChannelNetworkHandler.getPower(parent.level, channel);
            }
        }

        @Override
        public int getPower() {
            return power;
        }

        @Override
        public ResourceLocation getChannel() {
            return channel;
        }

        @Override
        public void setPower(int power) {
            this.power = power;
        }

        @Override
        public void updatePower(int power) {
            this.power = power;
        }

        @Override
        public boolean shouldRemove() {
            return removed || parent == null || parent.isRemoved();
        }

        //@Override
        void setChannel(ResourceLocation c) {
            channelNew = c;
        }
    }

    interface ITickableHolder{
        void updateSelf();
        int getPower();
        ResourceLocation getChannel();
        void setPower(int power);
    }
}
