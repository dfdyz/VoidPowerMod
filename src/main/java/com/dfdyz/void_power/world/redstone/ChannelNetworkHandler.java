package com.dfdyz.void_power.world.redstone;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.sun.jna.platform.unix.X11;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChannelNetworkHandler {
    //public static final ChannelNetworkHandler INSTANCE = new ChannelNetworkHandler();
    static final Map<LevelAccessor, Network> networks = Maps.newIdentityHashMap();
    public static final ResourceLocation NULL_CHANNEL = new ResourceLocation("null:null");

    public static void reset(){
        networks.forEach((k, v) -> {
            v.close();
        });
        networks.clear();
    }

    public static Network getOrCreateNetwork(@NotNull LevelAccessor level){
        if(networks.containsKey(level)){
            return networks.get(level);
        }
        else {
            Network n = new Network();
            networks.put(level, new Network());
            return n;
        }
    }

    public static int getPower(@NotNull LevelAccessor level, ResourceLocation channel){
        return getOrCreateNetwork(level).getOrCreateChannel(channel).getPower();
    }
    public static void addReceiver(@NotNull LevelAccessor level, ISignalReceiver r){
        getOrCreateNetwork(level).getOrCreateChannel(r.getChannel()).addReceiver(r);
    }
    public static void removeReceiver(@NotNull LevelAccessor level, ISignalReceiver r){
        getOrCreateNetwork(level).getOrCreateChannel(r.getChannel()).removeReceiver(r);
    }
    public static void addSender(@NotNull LevelAccessor level, ISignalSender s){
        getOrCreateNetwork(level).getOrCreateChannel(s.getChannel()).addSender(s);
    }
    public static void removeSender(@NotNull LevelAccessor level, ISignalSender s){
        getOrCreateNetwork(level).getOrCreateChannel(s.getChannel()).removeSender(s);
    }
    public static void updateChannel(@NotNull LevelAccessor level, ResourceLocation c){
        getOrCreateNetwork(level).getOrCreateChannel(c).updatePower(true);
    }

    public static class Network {
        private final Map<ResourceLocation, Channel> channels = Maps.newHashMap();

        public Network(){
            channels.put(NULL_CHANNEL, new NullChannel());
            //channels.put(NULL_CHANNEL, new Channel(NULL_CHANNEL));
        }

        public Channel getOrCreateChannel(ResourceLocation id){
            if(!channels.containsKey(id)){
                Channel c = new Channel(id);
                channels.put(id, c);
                return c;
            }
            return channels.get(id);
        }

        public void close(){
            channels.forEach((k,v) -> {
                v.close();
            });
            channels.clear();
        }
    }

    public static class NullChannel extends Channel{
        public NullChannel() {
            super(NULL_CHANNEL);
        }
        @Override
        public void addSender(ISignalSender sender){}
        @Override
        public void removeSender(ISignalSender sender) {}
        @Override
        public void addReceiver(ISignalReceiver receiver){}
        @Override
        public void removeReceiver(ISignalReceiver receiver){}
        @Override
        public void updatePower(boolean updateReceiver) {}
        @Override
        public int getMaxPower(){return 0;}
    }

    public static class Channel{
        public final ResourceLocation id;
        private final Set<ISignalSender> senders = Sets.newIdentityHashSet();
        private final Set<ISignalReceiver> receivers = Sets.newIdentityHashSet();

        private int maxPower = 0;

        public Channel(ResourceLocation id){
            this.id = id;
        }

        public void addSender(ISignalSender sender){
            senders.add(sender);
            updatePower(true);
        }

        public void removeSender(ISignalSender sender) {
            senders.remove(sender);
            updatePower(true);
        }

        public void addReceiver(ISignalReceiver receiver){
            receivers.add(receiver);
        }

        public void removeReceiver(ISignalReceiver receiver){
            receivers.remove(receiver);
        }

        public void updatePower(boolean updateReceiver) {
            senders.removeIf(ISignalSender::shouldRemove);
            int maxPower = getPower();
            if (this.maxPower != maxPower && updateReceiver){
                receivers.removeIf(ISignalReceiver::shouldRemove);
                receivers.forEach((r) -> {
                    r.updatePower(maxPower);
                });
            }
            this.maxPower = maxPower;
        }

        private int getPower(){
            if (senders.isEmpty()) return 0;
            return senders.stream().max(Comparator.comparingInt(ISignalSender::getPower)).get().getPower();
        }

        public int getMaxPower(){
            return maxPower;
        }

        public void close(){
            senders.clear();
            receivers.clear();
        }
    }


}
