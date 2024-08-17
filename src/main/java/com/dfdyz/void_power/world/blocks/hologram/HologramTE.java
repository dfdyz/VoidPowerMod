package com.dfdyz.void_power.world.blocks.hologram;


import com.dfdyz.void_power.client.screen_cache.IScreenCache;
import com.dfdyz.void_power.compat.cct.peripherals.P_HologramPeripheral;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.network.CP.CP_HologramInputEvent;
import com.dfdyz.void_power.network.CP.CP_HologramRename;
import com.dfdyz.void_power.network.CP.CP_HologramUpdateRequest;
import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.network.SP.SP_HologramPoseUpdate;
import com.dfdyz.void_power.network.SP.SP_HologramRename;
import com.dfdyz.void_power.network.SP.SP_HologramUpdate;
import com.dfdyz.void_power.utils.Debug;
import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.utils.SyncLocker;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class HologramTE extends SmartBlockEntity implements MenuProvider {
    public Behavior behavior;

    public P_HologramPeripheral peripheral;
    protected LazyOptional<IPeripheral> peripheralCap;
    public int[] buffer;
    public int width = 16, high = 16;
    public int initColor = 0x00A0FF6F;

    public float offx = 0, offy = 0, offz = 0;
    public float rotYaw = 0, rotPitch = 0, rotRoll = 0;
    public float scalex = 1, scaley = 1;
    public final SyncLocker<Boolean> transformDirty = new SyncLocker<>(false);

    public final SyncLocker<Boolean> needSync = new SyncLocker<>(true);

    public String name = UUID.randomUUID().toString();

    public IScreenCache renderCache;

    public HologramTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        resize(width, high);
    }

    public P_HologramPeripheral getPeripheral(){
        if(peripheral == null){
            System.out.println("New at " + getBlockPos().toShortString());
            peripheral = new P_HologramPeripheral(this);
        }
        return peripheral;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            getPeripheral();
            if(peripheralCap == null || !peripheralCap.isPresent())
                peripheralCap =  LazyOptional.of(() -> this.peripheral);
            return peripheralCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public Direction getDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSync(){
        if(needSync.getThenSet(false)){
            // todo
            // sync packet
            PacketManager.sendToServer(new CP_HologramUpdateRequest(this));
        }
    }

    public void serverSync(){
        if(peripheral == null) return;
        if(needSync.getThenSet(false)){
            //todo
            //sync when screen update
            if(peripheral.shouldFullUpdate.getThenSet(false)){
                peripheral.SendFullPack(this);
            }
            else {
                peripheral.SendLazyPack(this);

            }
            //peripheral.SendFullPack(this);
        }
        if(transformDirty.getThenSet(false)){
            PacketManager.sendToAllPlayerTrackingThisBlock(new SP_HologramPoseUpdate(this), this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(renderCache != null) renderCache.cleanup();
        renderCache = null;
    }

    public void SendInputPack(String event, Object... data){
        PacketManager.sendToServer( new CP_HologramInputEvent(
                this, event, data
        ));
    }

    @Override
    public void initialize() {
        super.initialize();
        needSync.set(true);
        transformDirty.set(true);
        if(peripheral != null) peripheral.shouldFullUpdate.set(true);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behavior = new Behavior(this);
        behaviours.add(behavior);
    }

    public void BlitBuffer(int ax, int ay, int w, int h, int[] src){
        int[] buffer = this.buffer;
        int edgeD = Math.min(high, ay+h);
        for(int y = Math.max(ay, 0); y < edgeD; ++y){
            int offO = (y - ay) * w;
            int offD = y * width;
            int edgeR = Math.min(width, ax+w);
            for(int x = Math.max(ax, 0); x < edgeR; ++x){
                buffer[offD + x] = src[offO + x - ax];
            }
        }
    }

    public int[] MergeBuffer(int w, int h, int[] org, int[] dist){
        int[] d = dist;
        int init = ParamUtils.convertColor(initColor);
        if(d == null){
            d =  new int[w*h];
            for (int i = 0; i < w*h; i++) {
                d[i] = init;
            }
        }
        for(int y = 0; y < high && y < h; ++y){
            int offO = y * width;
            int offD = y * w;
            for(int x = 0; x < width && x < w; ++x){
                d[offD + x] = org[offO + x];
            }
        }
        return d;
    }

    void FillBuffer(int ax, int ay, int w, int h, int color){
        color = ParamUtils.convertColor(color);
        int[] buffer = this.buffer;
        for(int y = Math.max(ay, 0); y < high && y < h; ++y) {
            int offD = y * w;
            for (int x = Math.max(ax, 0); x < width && x < w; ++x) {
                buffer[offD + x] = color;
            }
        }
    }

    public void resize(int w, int h){
        if(buffer == null){
            buffer = new int[w * h];
            FillBuffer(0,0,w,h,initColor);
            //Debug.PrintIntArray(buffer);
        }
        if(w != width || h != high){
            buffer = MergeBuffer(w,h,buffer, null);
            width = w;
            high = h;
            if(peripheral != null) {
                peripheral.shouldFullUpdate.set(true);
            }
        }
    }

    public void handleLazyUpdatePack(SP_HologramUpdate msg){
        //System.out.println("Received Update Pack");
        if(!msg.lazy){
            resize(msg.w, msg.h);
        }
        try{
            BlitBuffer(msg.x, msg.y, msg.w, msg.h, msg.buffer);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(renderCache != null) renderCache.invalidate();
        //Debug.PrintMsg(msg);
        //Debug.PrintIntArray(buffer, width);
        //System.out.println("Received Update Pack 2");
    }

    public void returnFullUpdatePack(ServerPlayer player){
        PacketManager.sendToPlayer(new SP_HologramUpdate(this), player);
        PacketManager.sendToPlayer(new SP_HologramPoseUpdate(this), player);
    }

    public void Rename(String str){
        name = str;
        if(level == null) return;
        if(!level.isClientSide){
            PacketManager.sendToAllPlayerTrackingThisBlock(new SP_HologramRename(this), this);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Hologram");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new HologramMenu(i, this);
    }

    public static class Behavior extends BlockEntityBehaviour{
        public static final BehaviourType<Behavior> TYPE = new BehaviourType<>();

        HologramTE te;
        public Behavior(HologramTE be) {
            super(be);
            te = be;
        }

        @Override
        public BehaviourType<?> getType() {
            return TYPE;
        }

        @Override
        public void read(CompoundTag nbt, boolean clientPacket) {
            if(nbt.contains("w")){
                te.resize(nbt.getShort("w"), nbt.getShort("h"));
            }
            if(nbt.contains("offx")){
                te.offx = nbt.getFloat("offx");
                te.offy = nbt.getFloat("offy");
                te.offz = nbt.getFloat("offz");
                te.rotYaw = nbt.getFloat("yaw");
                te.rotPitch = nbt.getFloat("pitch");
                te.rotRoll = nbt.getFloat("roll");
            }

            if(nbt.contains("scalex")){
                te.scalex = nbt.getFloat("scalex");
                te.scaley = nbt.getFloat("scaley");
            }

            if(nbt.contains("initcol")){
                te.initColor = nbt.getInt("initcol");
            }

            if(nbt.contains("_name")){
                te.name = nbt.getString("_name");
            }
        }



        @Override
        public void write(CompoundTag nbt, boolean clientPacket) {
            nbt.putShort("w", (short) te.width);
            nbt.putShort("h", (short) te.high);

            nbt.putFloat("offx",  te.offx);
            nbt.putFloat("offy",  te.offy);
            nbt.putFloat("offz",  te.offz);

            nbt.putFloat("yaw",  te.rotYaw);
            nbt.putFloat("pitch",  te.rotPitch);
            nbt.putFloat("roll",  te.rotRoll);

            nbt.putFloat("scalex", te.scalex);
            nbt.putFloat("scaley", te.scaley);
            nbt.putInt("initcol", te.initColor);

            nbt.putString("_name", te.name);
        }

        @Override
        public void tick() {
            if (te.level == null) return;
            if(te.level.isClientSide){
                te.clientSync();
            }
            else {
                te.serverSync();
            }

            super.tick();
        }
    }
}
