package com.dfdyz.void_power.world.blocks.hologram;


import com.dfdyz.void_power.Config;
import com.dfdyz.void_power.client.screen_cache.IScreenCache;
import com.dfdyz.void_power.compat.cct.peripherals.P_HologramPeripheral;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.network.CP.CP_HologramInputEvent;
import com.dfdyz.void_power.network.CP.CP_HologramUpdateRequest;
import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.network.SP.SP_HologramPoseUpdate;
import com.dfdyz.void_power.network.SP.SP_HologramRename;
import com.dfdyz.void_power.network.SP.SP_HologramUpdate_A;
import com.dfdyz.void_power.network.SP.SP_HologramUpdate_B;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.utils.IntBuffer;
import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.utils.SyncLocker;
import com.google.errorprone.annotations.concurrent.GuardedBy;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.dfdyz.void_power.utils.ByteUtils.maxLengthPerPack;

public class HologramTE extends SmartBlockEntity implements MenuProvider, IFrameBuffer{
    public Behavior behavior;

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public P_HologramPeripheral peripheral;
    protected LazyOptional<IPeripheral> peripheralCap;
    private int[] buffer;
    private int[] buffer_last;

    private int width = 16, high = 16;
    protected int initColor = ParamUtils.convertColor(0x00A0FF6F);

    public float offx = 0, offy = 0, offz = 0;
    public float rotYaw = 0, rotPitch = 0, rotRoll = 0;
    public float scalex = 1, scaley = 1;
    public final SyncLocker<Boolean> transformDirty = new SyncLocker<>(false);
    public final SyncLocker<Boolean> needSync = new SyncLocker<>(true);
    public final SyncLocker<Boolean> fullSync = new SyncLocker<>(true);

    public String name = UUID.randomUUID().toString();

    public IScreenCache renderCache;

    public HologramTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        resize(width, high);
    }

    public P_HologramPeripheral getPeripheral(){
        if(peripheral == null){
            //System.out.println("New at " + getBlockPos().toShortString());
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
            PacketManager.sendToServer(new CP_HologramUpdateRequest(this));
        }
    }

    /*
    public Vec3 TryClick(Player player){
        // W.I.P.
        return Vec3.ZERO;
    }
     */

    public void UpdateRenderCache(){
        if(renderCache != null){
            renderCache.invalidate();
        }
    }

    int force_full_sync_ticker = 0;
    public void serverSync(){
        if(peripheral == null) return;
        if(force_full_sync_ticker < Config.ForceFullUpdateTick * 3){
            ++force_full_sync_ticker;
        }
        if(fullSync.getThenSet(false)){
            if(Config.EnableForceFullUpdate && force_full_sync_ticker >= Config.ForceFullUpdateTick){
                force_full_sync_ticker = 0;
                FullSyncPack();
            }
        }
        else if(needSync.getThenSet(false)){
            if(Config.EnableForceFullUpdate && force_full_sync_ticker >= Config.ForceFullUpdateTick * 3){
                force_full_sync_ticker = 0;
                FullSyncPack();
            }
            else {
                NoFullSyncPack();
            }
        }
        if(transformDirty.getThenSet(false)){
            PacketManager.sendToAllPlayerTrackingThisBlock(new SP_HologramPoseUpdate(this), this);
        }
    }

    public void FullSyncPack(){
       // System.out.println("Full Update");
        int[] buffer = this.buffer;
        if(buffer_last == null){
            if(getLevel() != null && !getLevel().isClientSide){
                this.buffer_last = buffer.clone();
            }
            return;
        }
        int offset = 0;

        while (offset < buffer.length){
            int[] bf;

            if(buffer.length > offset + maxLengthPerPack){
                bf = new int[maxLengthPerPack+2];
                System.arraycopy(buffer, offset, bf, 2, maxLengthPerPack);
                bf[0] = offset;
                bf[1] = maxLengthPerPack;
            }
            else {
                int l = buffer.length - offset;
                bf = new int[l + 2];
                System.arraycopy(buffer, offset, bf, 2, l);
                bf[0] = offset;
                bf[1] = l;
            }

            offset += maxLengthPerPack;

            PacketManager.sendToAllPlayerTrackingThisBlock(new SP_HologramUpdate_A(
                    this,
                    bf
            ), this);
        }

        System.arraycopy(buffer, 0, buffer_last, 0, buffer.length);
    }

    public void NoFullSyncPack(){
        //System.out.println("NoFull Update");
        int[] buffer = this.buffer;
        int[] buffer_last = this.buffer_last;

        if(buffer_last == null){
            if(getLevel() != null && !getLevel().isClientSide){
                this.buffer_last = buffer.clone();
            }
            return;
        }

        // todo
        // 统计变更并计算判断需要什么方法来更新
        int dirty_len = 0;
        int range_start = -1, range_len = 0;
        int capability = maxLengthPerPack;

        // 数据格式  offset, len, c_0, c_1, ....., c_(len-1)
        IntBuffer range_buffer = new IntBuffer(maxLengthPerPack + 16);

        // 数据格式  offset, color
        IntBuffer sparse_buffer = new IntBuffer(maxLengthPerPack + 4);

        int currColor;
        boolean dirty;

        for (int i = 0; i < buffer.length; i++) {
            currColor = buffer[i];
            dirty = currColor != buffer_last[i];
            if(dirty){ // 变更
                ++dirty_len;

                if(range_start < 0){
                    range_start = i;
                    range_len = 0;
                }

                //System.out.print(i + " ");
            }

            if(range_start >= 0) ++range_len;

            if(dirty){
                if(range_len - dirty_len == 1){
                    dirty_len = range_len;
                }
            }
            else {
                if(range_len > 0){
                    if(range_len - dirty_len >= 2){
                        // ? 1 0 0 ? ?
                        // 变更情况 ? 1 0 0 ? ? ?
                        // 需要写入
                        if(dirty_len > 1){
                            // 变更情况 ? 1 1 0 0 ? ? ?
                            // 截断并记录
                            //System.out.println("Push");
                            // 当前包足够
                            //System.out.printf("Push Range %d, %d\n", range_start, range_start + dirty_len);
                            if(dirty_len + 2 < capability){
                                range_buffer.push(range_start);
                                range_buffer.push(dirty_len);
                                range_buffer.push(buffer, range_start, dirty_len);
                                capability -= dirty_len + 2;
                            }
                            else { // 包满了
                                //System.out.println("Overflow");
                                while (dirty_len > 0){
                                    int len = Math.min(dirty_len, capability);
                                    range_buffer.push(range_start);
                                    range_buffer.push(len);
                                    range_buffer.push(buffer, range_start, len);

                                    dirty_len -= len;
                                    // 发包
                                    //System.out.println("FP");
                                    if(len == capability){
                                        PacketManager.sendToAllPlayerTrackingThisBlock(
                                                new SP_HologramUpdate_A(this, range_buffer.getCutData()),
                                                this);

                                        range_buffer.clear();
                                        capability = maxLengthPerPack;
                                        range_start += len;
                                    }
                                    else {
                                        capability -= len + 2;
                                    }
                                }
                            }
                        }
                        else {
                            // 变更情况 ? 0 1 0 0 ? ? ? (只变更了一位)
                            // 记录离散点
                            //System.out.printf("Push Point %d\n", range_start);
                            sparse_buffer.push(range_start);
                            sparse_buffer.push(buffer[range_start]);

                            // todo
                            // 如果离散点缓冲满了，发包
                            if(sparse_buffer.getCount() >= maxLengthPerPack){
                                PacketManager.sendToAllPlayerTrackingThisBlock(
                                        new SP_HologramUpdate_B(this, sparse_buffer.getCutData()),
                                        this);

                                sparse_buffer.clear();
                            }
                        }
                        dirty_len = 0;
                        range_start = -1;
                        range_len = 0;
                    }
                }
            }
        }

        if(sparse_buffer.getCount() > 0){
            PacketManager.sendToAllPlayerTrackingThisBlock(
                    new SP_HologramUpdate_B(this, sparse_buffer.getCutData()),
                    this);
            sparse_buffer.clear();
        }

        if(range_start >= 0){
            //System.out.println("Overflow end.");
            if(dirty_len + 2 < capability){
                range_buffer.push(range_start);
                range_buffer.push(dirty_len);
                range_buffer.push(buffer, range_start, dirty_len);

                PacketManager.sendToAllPlayerTrackingThisBlock(
                        new SP_HologramUpdate_A(this, range_buffer.getCutData()),
                        this);

                range_buffer.clear();
            }
            else {
                //System.out.println("Overflow2");
                while (dirty_len > 0){
                    int len = Math.min(dirty_len, capability);
                    range_buffer.push(range_start);
                    range_buffer.push(len);
                    range_buffer.push(buffer, range_start, len);
                    dirty_len -= capability;
                    range_start += capability;
                    // 发包
                    //System.out.println("FP2");
                    PacketManager.sendToAllPlayerTrackingThisBlock(
                            new SP_HologramUpdate_A(this, range_buffer.getCutData()),
                            this);

                    range_buffer.clear();
                }
            }
        }
        else {
            //System.out.println("FP2");
            if(range_buffer.getCount() > 0){
                PacketManager.sendToAllPlayerTrackingThisBlock(
                        new SP_HologramUpdate_A(this, range_buffer.getCutData()),
                        this);
                range_buffer.clear();
            }
        }
        System.arraycopy(buffer, 0, buffer_last, 0, buffer.length);
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
            if (edgeR - Math.max(ax, 0) >= 0)
                System.arraycopy(src, offO + Math.max(ax, 0) - ax, buffer, offD + Math.max(ax, 0), edgeR - Math.max(ax, 0));
        }
    }

    public int[] MergeBuffer(int w, int h, int[] org, int[] dist, int initColor){
        int[] d = dist;
        if(d == null){
            d =  new int[w*h];
            for (int i = 0; i < w*h; i++) {
                d[i] = initColor;
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

    void FillBuffer(int ax, int ay, int w, int h, int raw_color){
        int[] buffer = this.buffer;
        for(int y = Math.max(ay, 0); y < high && y < h; ++y) {
            int offD = y * w;
            for (int x = Math.max(ax, 0); x < width && x < w; ++x) {
                buffer[offD + x] = raw_color;
            }
        }
    }

    @Override
    public IFrameBuffer resize(int w, int h){
        if(buffer == null){
            buffer = new int[w * h];
            FillBuffer(0,0,w,h,initColor);
        }
        if(buffer_last == null && buffer != null){
            if(getLevel() != null && !getLevel().isClientSide){
                buffer_last = buffer.clone();
            }
        }


        if(w != width || h != high){
            buffer = MergeBuffer(w,h,buffer, null, initColor);
            if(getLevel() != null && !getLevel().isClientSide){
                buffer_last = MergeBuffer(w,h, buffer_last, null, initColor+1);
            }
            width = w;
            high = h;
        }
        return this;
    }

    @Override
    public int getInitColor() {
        return initColor;
    }

    @Override
    public void setInitColor(int col) {
        initColor = col;
    }

    @Override
    public void remove() {
        super.remove();

        peripheral = null;
        peripheralCap = null;
    }

    //todo
    public void returnFullUpdatePack(ServerPlayer player){
        int[] buffer = this.buffer;
        int offset = 0;

        while (offset < buffer.length){
            int[] bf;

            if(buffer.length > offset + maxLengthPerPack){
                bf = new int[maxLengthPerPack+2];
                System.arraycopy(buffer, offset, bf, 2, maxLengthPerPack);
                bf[0] = offset;
                bf[1] = maxLengthPerPack;
            }
            else {
                int l = buffer.length - offset;
                bf = new int[l + 2];
                System.arraycopy(buffer, offset, bf, 2, l);
                bf[0] = offset;
                bf[1] = l;
            }

            offset += maxLengthPerPack;

            PacketManager.sendToPlayer(
                    new SP_HologramUpdate_A(
                            this,
                            bf
                    ),
                    player);
        }
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
        return VPTileEntities.HOLOGRAM_GUI.create(i, inventory);
    }

    @Override
    public int[] getBuffer() {
        return buffer;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return high;
    }

    @Override
    public boolean isTE(){
        return true;
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
