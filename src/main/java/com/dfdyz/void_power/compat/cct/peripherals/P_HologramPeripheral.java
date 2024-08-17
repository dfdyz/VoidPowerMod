package com.dfdyz.void_power.compat.cct.peripherals;

import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.network.SP.SP_HologramUpdate;
import com.dfdyz.void_power.utils.Debug;
import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.utils.SyncLocker;
import com.dfdyz.void_power.utils.font.Font;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.impl.shadow.M;
import org.valkyrienskies.core.impl.shadow.S;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class P_HologramPeripheral implements IPeripheral{

    public int dirty_x, dirty_y, dirty_ex, dirty_ey;

    public SyncLocker<Boolean> shouldFullUpdate = new SyncLocker<>(true);

    protected HologramTE te;

    public P_HologramPeripheral(HologramTE te){
        this.te = te;
        //resize(te.width, te.high);
        dirty_x = te.width;
        dirty_ex = 0;
        dirty_y = te.high;
        dirty_ey = 0;
    }

    @LuaFunction
    public final void Resize(int w, int h){
        synchronized (SYNC_LOCK){
            te.resize(w, h);
            dirty_x = te.width;
            dirty_ex = 0;
            dirty_y = te.high;
            dirty_ey = 0;
        }
    }

    @LuaFunction
    public void SetClearColor(IArguments param) throws LuaException {
        te.initColor = ParamUtils.convertColor(param.getInt(0));
    }

    static int DefCol = -1;

    @LuaFunction
    public final void Blit(IArguments param) throws LuaException {
        if(param.count() < 5) throw new LuaException("Need more than 5 argument at, got " + param.count() + ".");
        int ax, ay, w, h;
        ax = param.getInt(0);
        ay = param.getInt(1);
        w = param.getInt(2);
        h = param.getInt(3);
        int mode = param.count() == 6 ? param.getInt(5) : 0;
        try {
            Map<?, ?> src_raw = param.getTable(4);

            int[] buffer = te.buffer;
            int col = -1, idx;
            int edgeD = Math.min(te.high, ay+h);
            if(mode == 1){  //cutout
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * te.width;
                    int edgeR = Math.min(te.width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        if(src_raw.containsKey(idx * 1.0)){
                            col = ParamUtils.convertColor((int) ((Double) src_raw.get(idx * 1.0)).longValue());
                        }
                        if((col & 0xFF) > 0){
                            buffer[offD + x] = col;
                        }
                    }
                }
            }
            else if(mode == 0){ //solid
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * te.width;
                    int edgeR = Math.min(te.width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        if(src_raw.containsKey(idx * 1.0)){
                            col = ParamUtils.convertColor((int) ((Double) src_raw.get(idx * 1.0)).longValue());
                            buffer[offD + x] = col;
                        }
                    }
                }
            }
            else if(mode == 2){ //blend
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * te.width;
                    int edgeR = Math.min(te.width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        if(src_raw.containsKey(idx * 1.0)){
                            col = (int) ((Double) src_raw.get(idx * 1.0)).longValue();
                        }
                        buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(col, ParamUtils.convertColor(buffer[offD + x])));
                    }
                }
            }

            //Debug.PrintIntArray(buffer, te.width);
            MarkDirtyXYWH(ax, ay, w, h);
        }catch (Exception e){
            MarkDirtyXYWH(ax, ay, w, h);
            e.printStackTrace();
            throw new LuaException(e.toString());
        }
    }

    @LuaFunction
    public void Text(IArguments param) throws LuaException {
        int ax = param.getInt(0);
        int ay = param.getInt(1);

        String text = ParamUtils.unicodeToCN(param.getString(2));

        int color = param.getInt(3);
        int mode = param.getInt(4);

        int tmpx = ax;
        int[] buffer = te.buffer;

        if(mode == 0 || mode == 1){ // cutout
            color = ParamUtils.convertColor(color);
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (tmpx > te.width) continue;
                if (ch == '\n') {
                    ay += 16;
                    if (ay > te.high) break;
                    tmpx = ax;
                    continue;
                }
                Font.CharMat cm = Font.getMat(ch);
                int edgeR = Math.min(ay + cm.bitmap.length, te.high);


                for (int y = Math.max(ay, 0); y < edgeR; y++) {
                    int offD = y * te.width;
                    int edgeD = Math.min(te.width, tmpx + cm.width);
                    for (int x = Math.max(tmpx, 0); x < edgeD; x++) {
                        if(cm.bitmap[y-ay][x-tmpx]){
                            buffer[offD + x] = color;
                        }
                    }
                }

                tmpx += cm.width;
                MarkDirtyXYWH(tmpx, ay, cm.width, cm.bitmap.length);
            }
        }
        else if(mode == 2){ // blend
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (tmpx > te.width) continue;
                if (ch == '\n') {
                    ay += 16;
                    if (ay > te.high) break;
                    tmpx = ax;
                    continue;
                }
                Font.CharMat cm = Font.getMat(ch);
                int edgeR = Math.min(ay + cm.bitmap.length, te.high);

                for (int y = Math.max(ay, 0); y < edgeR; y++) {
                    int offD = y * te.width;
                    int edgeD = Math.min(te.width, tmpx + cm.width);
                    for (int x = Math.max(tmpx, 0); x < edgeD; x++) {
                        if(cm.bitmap[y-ay][x-tmpx]){
                            buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(color, ParamUtils.convertColor(buffer[offD + x])));
                        }
                    }
                }

                tmpx += cm.width;
                MarkDirtyXYWH(tmpx, ay, cm.width, cm.bitmap.length);
            }
        }

    }


    @LuaFunction
    public void SetScale(double x, double y){
        synchronized (SYNC_LOCK) {
            te.scalex = (float) x;
            te.scaley = (float) y;
        }
        te.transformDirty.set(true);
    }

    @LuaFunction
    public void Flush(){
        te.needSync.set(true);
    }

    @LuaFunction
    public void SetRotation(double yaw, double pitch, double roll){
        synchronized (SYNC_LOCK) {
            te.rotYaw = (float) yaw;
            te.rotPitch = (float) pitch;
            te.rotRoll = (float) roll;
        }
        te.transformDirty.set(true);
    }


    public void PushEvent(String event, Object... data){
        computers.forEach((computer) ->{
            computer.queueEvent(event, data);
        });
    }

    public void KeyUp(int key){

    }


    @LuaFunction
    public void SetTranslation(double x, double y, double z){
        synchronized (SYNC_LOCK) {
            te.offx = (float) x;
            te.offy = (float) y;
            te.offz = (float) z;
        }
        te.transformDirty.set(true);
    }


    public boolean isDirtyUnsafe(){
        return dirty_x < dirty_ex && dirty_y < dirty_ey;
    }

    @LuaFunction
    public void Fill(IArguments param) throws LuaException {
        if(param.count() < 5) throw new LuaException("Need 5 argument, got " + param.count() + ".");
        int ax, ay, w, h;
        ax = param.getInt(0);
        ay = param.getInt(1);
        w = param.getInt(2);
        h = param.getInt(3);
        int mode = param.count() == 6 ? param.getInt(5) : 0;
        try {
            int color = param.getInt(4);
            int[] buffer = te.buffer;
            int edgeD = Math.min(te.high, ay+h);
            if(mode == 0 || mode == 1){  //solid
                color = ParamUtils.convertColor(color);
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offD = y * te.width;
                    int edgeR = Math.min(te.width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        buffer[offD + x] = color;
                    }
                }
            }
            else if (mode == 2){  //blend
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offD = y * te.width;
                    int edgeR = Math.min(te.width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(color, ParamUtils.convertColor(buffer[offD + x])));
                    }
                }
            }
        } catch (LuaException e) {
            throw new RuntimeException(e);
        }
    }

    final Set<IComputerAccess> computers = Sets.newConcurrentHashSet();

    @GuardedBy("SYNC_LOCK")
    public final Object SYNC_LOCK = new Object();

    int[] PollDirtyBuffer(){
        if(!isDirtyUnsafe()) return null;
        int w = dirty_ex - dirty_x;
        int[] b = new int[w * (dirty_ey-dirty_y)];
        for (int y = dirty_y; y < dirty_ey; y++) {
            int offb = (y-dirty_y) * w - dirty_x;
            int offo = y* te.width;
            for (int x = dirty_x; x < dirty_ex; x++) {
                b[offb + x] = te.buffer[offo + x];
            }
        }
        return b;
    }

    public void SendLazyPack(HologramTE te){
        synchronized (SYNC_LOCK){
           // System.out.println("Dirty Update");
            SP_HologramUpdate p = GetLazyPack(te);

            if(p != null){
                //System.out.println("Dirty Update2");
               // Debug.PrintMsg(p);
                PacketManager.sendToAllPlayerTrackingThisBlock(p, te);
            }
        }
    }

    SP_HologramUpdate GetLazyPack(HologramTE te){
            int[] buffer = PollDirtyBuffer();
            //System.out.println(buffer != null);
            if(buffer == null) return null;
            SP_HologramUpdate pack = new SP_HologramUpdate(
                    te, dirty_x, dirty_y,
                    dirty_ex-dirty_x,
                    dirty_ey-dirty_y,
                    buffer
            );
            dirty_x = te.width;
            dirty_ex = 0;
            dirty_y = te.high;
            dirty_ey = 0;
            return pack;

    }

    public void SendFullPack(HologramTE te){
        synchronized (SYNC_LOCK){
            PacketManager.sendToAllPlayerTrackingThisBlock(new SP_HologramUpdate(te), te);
        }
    }

    public void MarkDirtyXYWH(int x, int y, int w, int h){
        synchronized (SYNC_LOCK){
            dirty_x = Math.min(x, dirty_x);
            dirty_y = Math.min(y, dirty_y);
            dirty_ex = Math.max(x+w, dirty_ex);
            dirty_ey = Math.max(y+h, dirty_ey);
            dirty_x = Math.max(dirty_x, 0);
            dirty_y = Math.max(dirty_y, 0);
            dirty_ex = Math.min(te.width, dirty_ex);
            dirty_ey = Math.min(te.high, dirty_ey);
        }
    }


    @Override
    public String getType() {
        return "hologram";
    }

    void FillBuffer(int ax, int ay, int w, int h, int color){
        int col = ParamUtils.convertColor(color);
        int edgeD = Math.min(te.high, ay + h);
        for(int y = Math.max(ay, 0); y < edgeD; ++y) {
            int offD = y * w;
            int edgeR = Math.min(te.width, ax + w);
            for (int x = Math.max(ax, 0); x < edgeR; ++x) {
                te.buffer[offD + x] = col;
            }
        }
    }

    @LuaFunction
    public void Clear(){
        FillBuffer(0,0,te.width,te.high,te.initColor);
        shouldFullUpdate.set(true);
    }




    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
        System.out.println("A_ " + computer.getID());
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
        System.out.println("D_ " + computer.getID());
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }
}
