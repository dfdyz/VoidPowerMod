package com.dfdyz.void_power.compat.cct.peripherals;

import com.dfdyz.void_power.Config;
import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.utils.font.DefaultFont;
import com.dfdyz.void_power.utils.font.Font;
import com.dfdyz.void_power.utils.font.FontLib;
import com.dfdyz.void_power.utils.font.IFontLib;
import com.dfdyz.void_power.world.blocks.hologram.DefaultFrameBufferImpl;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.dfdyz.void_power.world.blocks.hologram.IFrameBuffer;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class P_HologramPeripheral implements IPeripheral{

    protected final HashMap<String, FontLib> fontlibs = new HashMap<>();

    protected HologramTE te;

    private IFrameBuffer currentBuffer;

    protected IFrameBuffer[] buffers;

    public P_HologramPeripheral(HologramTE te){
        this.te = te;
        //resize(currentBuffer.getWidth(), currentBuffer.getHigh());
        currentBuffer = te;
        buffers = new IFrameBuffer[Config.HologramMaxBufferCount];
        Arrays.fill(buffers, null);
    }

    @LuaFunction
    public final int CreateFrameBuffer(int w, int h) throws LuaException {
        int idx = -1;

        if(w > Config.holo_w_mx*2 || h > Config.holo_h_mx*2) throw new LuaException("Max resolution is %d x %d, out of range.".formatted(Config.holo_w_mx*2, Config.holo_h_mx*2));

        for (int i = 0; i < buffers.length; i++) {
            if(buffers[i] == null){
                buffers[i] = new DefaultFrameBufferImpl(w,h);
                idx = i;
                break;
            }
        }
        return idx;
    }

    @LuaFunction
    public final int GetMaxFrameBufferCount(){
        return buffers.length;
    }

    @LuaFunction
    public final int GetFrameBufferCount(){
        int cnt = 0;
        for (IFrameBuffer buffer : buffers) {
            if (buffer != null) {
                ++cnt;
            }
        }
        return cnt;
    }

    @LuaFunction
    public final void FreeAllFrameBuffer(){
        currentBuffer = te;
        Arrays.fill(buffers, null);
    }

    @LuaFunction
    public final void SetCurrentFrameBuffer(int idx) throws LuaException {
        if(idx < 0){
            currentBuffer = te;
        }
        else if(idx < buffers.length){
            if(buffers[idx] != null){
                currentBuffer = buffers[idx];
            }
            else throw new LuaException("Buffer [" + idx + "] not exist.");
        }
        else throw new LuaException("Buffer id out of range(, " + (buffers.length - 1) + "]");
    }

    @LuaFunction
    public final Map<Object, Object> DumpFrameBuffer(){
        Map<Object, Object> map = new HashMap<>();
        map.put("w", currentBuffer.getWidth());
        map.put("h", currentBuffer.getHeight());
        map.put("pixels", ParamUtils.dumpIntArray(currentBuffer.getBuffer()));
        return map;
    }


    @LuaFunction
    public final void FreeFrameBuffer(int idx) throws LuaException {
        if(idx >= 0 && idx < buffers.length){
            if(currentBuffer == buffers[idx]) currentBuffer = te;
            buffers[idx] = null;
        }
        else throw new LuaException("Buffer id out of range[0, " + (buffers.length - 1) + "]");
    }

    @LuaFunction
    public final void Resize(int w, int h) throws LuaException {
        synchronized (SYNC_LOCK){
            if(w > Config.holo_w_mx || h > Config.holo_h_mx) throw new LuaException("Max resolution is %d x %d, out of range.".formatted(Config.holo_w_mx, Config.holo_h_mx));
            currentBuffer = currentBuffer.resize(w, h);
        }
    }

    @LuaFunction
    public final void SetClearColor(IArguments param) throws LuaException {
        currentBuffer.setInitColor(ParamUtils.convertColor(param.getInt(0)));
    }
    @LuaFunction
    public final String GetName(){
        return te.name;
    }

    @LuaFunction
    public final void Rename(String n){
        te.Rename(n);
    }

    @LuaFunction
    public final Map<Object, Object> GetAllInvalidBuffer(){
        Map<Object, Object> table = Maps.newHashMap();
        int idx = 0;
        for (int i = 0; i < buffers.length; i++) {
            if(buffers[i] != null){
                table.put((++idx)*1.0, i*1.0);
            }
        }
        return table;
    }


    @LuaFunction
    public final void BlitFrameBuffer(IArguments param) throws LuaException {
        int ax = param.getInt(0);
        int ay = param.getInt(1);
        int bufferId = param.getInt(2);
        int mode = 0;
        if(param.count() == 4){
            mode = param.getInt(3);
        }

        IFrameBuffer fb = getBuffer(bufferId);
        if(fb == null) throw new LuaException("Invalid frame buffer.");
        if(fb == currentBuffer) return;
        int w = fb.getWidth();
        int h = fb.getHeight();

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        try {
            int[] buffer = currentBuffer.getBuffer();
            int[] fbuffer = fb.getBuffer();
            int col = -1, idx;
            int edgeD = Math.min(high, ay+h);
            if(mode == 1){  //cutout
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        col = fbuffer[idx];
                        if((col & 0xFF) > 0){
                            buffer[offD + x] = col;
                        }
                    }
                }
            }
            else if(mode == 0){ //solid
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * width;
                    int edgeR = Math.min(width, ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        buffer[offD + x] = fbuffer[idx];
                    }
                }
            }
            else if(mode == 2){ //blend
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(fbuffer[idx], ParamUtils.convertColor(buffer[offD + x])));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new LuaException(e.toString());
        }

    }

    IFrameBuffer getBuffer(int idx){
        if(idx < 0) return te;
        else if(idx < buffers.length) return buffers[idx];
        else return null;
    }

    @LuaFunction
    public final void Blit(IArguments param) throws LuaException {
        if(param.count() < 5) throw new LuaException("Need more than 5 argument at, got " + param.count() + ".");
        int ax, ay, w, h;
        ax = param.getInt(0);
        ay = param.getInt(1);
        w = param.getInt(2);
        h = param.getInt(3);

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        int mode = param.count() == 6 ? param.getInt(5) : 0;
        try {
            Map<?, ?> src_raw = param.getTable(4);

            int[] buffer = currentBuffer.getBuffer();
            int col = -1, idx;
            int edgeD = Math.min(high, ay+h);
            if(mode == 1){  //cutout
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offO = (y-ay) * w;
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
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
                    int offD = y * width;
                    int edgeR = Math.min(width, ax+w);
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
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        idx = offO + x - ax;
                        if(src_raw.containsKey(idx * 1.0)){
                            col = (int) ((Double) src_raw.get(idx * 1.0)).longValue();
                        }
                        buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(col, ParamUtils.convertColor(buffer[offD + x])));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new LuaException(e.toString());
        }
    }

    // refer https://blog.51cto.com/u_15273495/2914191
    @LuaFunction
    public final void DrawTriangle(IArguments param) throws LuaException {
        int x0 = param.getInt(0);
        int y0 = param.getInt(1);
        int x1 = param.getInt(2);
        int y1 = param.getInt(3);
        int x2 = param.getInt(4);
        int y2 = param.getInt(5);

        int color0 = param.getInt(6);
        int mode = 0;
        if(param.count() > 7){
            mode = param.getInt(7);
        }

        int color1 = color0;
        int color2 = color0;
        if(param.count() > 8){
            color1 = param.getInt(7);
            color2 = param.getInt(8);
            mode = 0;
        }

        if(param.count() > 9){
            mode = param.getInt(9);
        }

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        int sx = Math.max(0, Math.min(x0, Math.min(x1, x2)));
        int ex = Math.min(width, Math.max(x0, Math.max(x1, x2)));

        int sy = Math.max(0, Math.min(y0, Math.min(y1, y2)));
        int ey = Math.min(high, Math.max(y0, Math.max(y1, y2)));

        float area = PerpDot(x0, y0,
                x1, y1,
                x2, y2);

        int col;
        int[] buffer = currentBuffer.getBuffer();
        if(mode == 2){ // blend
            for(int y =  sy; y < ey; ++y){
                int offD = y * width;
                for(int x = sx; x < ex; ++x){
                    float e0 = PerpDot(x1, y1, x2, y2, x, y)/ area;
                    float e1 = PerpDot(x2, y2, x0, y0, x, y)/ area;
                    float e2 = PerpDot(x0, y0, x1, y1, x, y)/ area;

                    if (e0 >= 0 && e1 >= 0 && e2 >= 0){ // inside
                        col = LerpCol(color0, color1, color2, e0, e1, e2);
                        buffer[offD + x] =
                                ParamUtils.convertColor(
                                        ParamUtils.blendColor(
                                                col,
                                                ParamUtils.convertColor(buffer[offD + x])
                                        )
                                );
                    }
                }
            }
        }
        else {
            for(int y =  sy; y < ey; ++y){
                int offD = y * width;
                for(int x = sx; x < ex; ++x){
                    float e0 = PerpDot(x1, y1, x2, y2, x, y)/ area;
                    float e1 = PerpDot(x2, y2, x0, y0, x, y)/ area;
                    float e2 = PerpDot(x0, y0, x1, y1, x, y)/ area;

                    if (e0 >= 0 && e1 >= 0 && e2 >= 0){ // inside
                        col = LerpCol(color0, color1, color2, e0, e1, e2);
                        buffer[offD + x] = ParamUtils.convertColor(col);
                    }
                }
            }
        }
    }

    @LuaFunction
    public final void DrawLine(IArguments param) throws LuaException {
        int r_ma_x = te.getWidth()*2;
        int r_mi_x = -te.getWidth();
        int r_ma_y = te.getHeight()*2;
        int r_mi_y = -te.getHeight();
        int x0 = ParamUtils.rangeCheck(param.getInt(0), r_mi_x, r_ma_x, 0);
        int y0 = ParamUtils.rangeCheck(param.getInt(1), r_mi_y, r_ma_y, 1);
        int x1 = ParamUtils.rangeCheck(param.getInt(2), r_mi_x, r_ma_x, 2);
        int y1 = ParamUtils.rangeCheck(param.getInt(3), r_mi_y, r_ma_y, 3);
        int color = param.getInt(4);
        int mode = 0;
        if(param.count() > 5){
            mode = param.getInt(5);
        }
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int[] buf = currentBuffer.getBuffer();
        int width = currentBuffer.getWidth();
        int height = currentBuffer.getHeight();
        int col = ParamUtils.convertColor(color);
        if(mode != 2){
            int sx = x0 < x1 ? 1 : -1;
            int sy = y0 < y1 ? 1 : -1;
            int e = 0;
            for (int i = 0; i < dx + dy; i++) {
                if(x0 >= 0 && x0 < width
                        && y0 >= 0 && y0 < height
                ){
                    buf[x0 + y0 * width] = col;
                }
                int e1 = e + dy;
                int e2 = e - dx;
                if (Math.abs(e1) < Math.abs(e2)) {
                    x0 += sx;
                    e = e1;
                } else {
                    y0 += sy;
                    e = e2;
                }
            }
        }else {
            int sgnX = x0 < x1 ? 1 : -1;
            int sgnY = y0 < y1 ? 1 : -1;
            int e = 0;
            for (int i = 0; i < dx + dy; i++) {
                if (x0 >= 0 && x0 < width
                        && y0 >= 0 && y0 < height
                ) {
                    buf[x0 + y0 * width] = ParamUtils.convertColor(ParamUtils.blendColor(
                            color, ParamUtils.convertColor(buf[x0 + y0 * width])
                    ));
                }
                int e1 = e + dy;
                int e2 = e - dx;
                if (Math.abs(e1) < Math.abs(e2)) {
                    x0 += sgnX;
                    e = e1;
                } else {
                    y0 += sgnY;
                    e = e2;
                }
            }
        }

    }

    @LuaFunction
    public final void DrawPixel(IArguments param) throws LuaException {
        int x = param.getInt(0);
        int y = param.getInt(1);

        int color = param.getInt(2);
        int mode = 0;
        if(param.count() > 3){
            mode = param.getInt(3);
        }

        int width = currentBuffer.getWidth();
        int[] buf = currentBuffer.getBuffer();
        if(x >= 0 && x < width
                && y >= 0 && y < currentBuffer.getHeight()){
            if(mode == 2){
                buf[x + y * width] = ParamUtils.convertColor(ParamUtils.blendColor(
                        color, ParamUtils.convertColor(buf[x + y * width])
                ));
            }
            else {
                buf[x + y * width] = ParamUtils.convertColor(color);
            }
        }
    }

    static void NotFinish() throws LuaException {
        throw new LuaException("This function is not finish on this version of mod.");
    }

    record Vertex(int x, int y, float u, float v, Color color){
        static Vertex dumpFrom(Map<?,?> table) throws LuaException {
            try {
                return new Vertex((int)((double)table.get(1.0)),
                        (int)((double) table.get(2.0)),
                        (float)((double) table.get(3.0)),
                        (float)((double)table.get(4.0)),
                        Color.fromRBGA32((int)((double) table.get(5.0)))
                );
            }catch (Exception e){
                throw new LuaException("Vertex format error.");
            }
        }
    }

    record Color(float r, float g, float b, float a){
        static Color fromRBGA32(int c){
            float r = ((c >> 24) & 0xFF) / 255.f;
            float g = ((c >> 16) & 0xFF) / 255.f;
            float b = ((c >> 8) & 0xFF) / 255.f;
            float a = (c & 0xFF) / 255.f;
            return new Color(r,g,b,a);
        }

        public int getRGBA32(){
            return ((((int)(r * 0xFF)) & 0xFF) << 24) |
                    ((((int)(g * 0xFF)) & 0xFF) << 16) |
                    ((((int)(b * 0xFF)) & 0xFF) << 8) |
                    ((((int)(a * 0xFF)) & 0xFF));
        }

        public Color mul(float r, float g, float b, float a){
            return new Color(this.r * r, this.g * g, this.b * b, this.a * a);
        }
    }

    @LuaFunction
    public final void DrawTriangleWithTexture(IArguments param) throws LuaException {
        IFrameBuffer texture = getBuffer(param.getInt(3));
        if(texture == null){
            throw new LuaException("Frame buffer not allocate.");
        }

        if(texture == currentBuffer){
            throw new LuaException("Can't set current frame buffer as texture to sampler.");
        }

        Vertex v0,v1,v2;
        v0 = Vertex.dumpFrom(param.getTable(0));
        v1 = Vertex.dumpFrom(param.getTable(1));
        v2 = Vertex.dumpFrom(param.getTable(2));

        int mode = 0;
        if(param.count() > 4){
            mode = param.getInt(4);
        }

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        int sx = Math.max(0, Math.min(v0.x, Math.min(v1.x, v2.x)));
        int ex = Math.min(width, Math.max(v0.x, Math.max(v1.x, v2.x)));

        int sy = Math.max(0, Math.min(v0.y, Math.min(v1.y, v2.y)));
        int ey = Math.min(high, Math.max(v0.y, Math.max(v1.y, v2.y)));

        float area = PerpDot(v0.x, v0.y,
                v1.x, v1.y,
                v2.x, v2.y);

        float u,v,r,g,b,a;

        int[] buffer = currentBuffer.getBuffer();
        if(mode == 2){ // blend
            for(int y =  sy; y < ey; ++y){
                int offD = y * width;
                for(int x = sx; x < ex; ++x){
                    float e0 = PerpDot(v1.x, v1.y, v2.x, v2.y, x, y)/ area;
                    float e1 = PerpDot(v2.x, v2.y, v0.x, v0.y, x, y)/ area;
                    float e2 = PerpDot(v0.x, v0.y, v1.x, v1.y, x, y)/ area;

                    if (e0 >= 0 && e1 >= 0 && e2 >= 0){ // inside
                        r = LerpChannel(v0.color.r, v1.color.r, v2.color.r, e0, e1, e2);
                        g = LerpChannel(v0.color.g, v1.color.g, v2.color.g, e0, e1, e2);
                        b = LerpChannel(v0.color.b, v1.color.b, v2.color.b, e0, e1, e2);
                        a = LerpChannel(v0.color.a, v1.color.a, v2.color.a, e0, e1, e2);
                        u = v0.u * e0 + v1.u * e1 + v2.u * e2;
                        v = v0.v * e0 + v1.v * e1 + v2.v * e2;
                        Color s = Color.fromRBGA32(sampler(texture, u, v)).mul(r,g,b,a);
                        buffer[offD + x] =
                                ParamUtils.convertColor(
                                        ParamUtils.blendColor(
                                                s.getRGBA32(),
                                                ParamUtils.convertColor(buffer[offD + x])
                                        )
                                );
                    }
                }
            }
        }
        else {
            for(int y =  sy; y < ey; ++y){
                int offD = y * width;
                for(int x = sx; x < ex; ++x){
                    float e0 = PerpDot(v1.x, v1.y, v2.x, v2.y, x, y)/ area;
                    float e1 = PerpDot(v2.x, v2.y, v0.x, v0.y, x, y)/ area;
                    float e2 = PerpDot(v0.x, v0.y, v1.x, v1.y, x, y)/ area;

                    if (e0 >= 0 && e1 >= 0 && e2 >= 0){ // inside
                        r = LerpChannel(v0.color.r, v1.color.r, v2.color.r, e0, e1, e2);
                        g = LerpChannel(v0.color.g, v1.color.g, v2.color.g, e0, e1, e2);
                        b = LerpChannel(v0.color.b, v1.color.b, v2.color.b, e0, e1, e2);
                        a = LerpChannel(v0.color.a, v1.color.a, v2.color.a, e0, e1, e2);
                        u = v0.u * e0 + v1.u * e1 + v2.u * e2;
                        v = v0.v * e0 + v1.v * e1 + v2.v * e2;
                        Color s = Color.fromRBGA32(sampler(texture, u, v));
                        buffer[offD + x] = ParamUtils.convertColor(s.mul(r,g,b,a).getRGBA32());
                    }
                }
            }
        }
    }



    int sampler(IFrameBuffer texture, float _u, float _v){
        int width = texture.getWidth();
        float u,v;
        u = Math.min(1, Math.max(0, _u));
        v = Math.min(1, Math.max(0, _v));

        int x = (int) ((width - 1) * u);
        int y = (int) ((texture.getHeight() - 1) * (1 - v));
        return ParamUtils.convertColor(texture.getBuffer()[y * width + x]);
    }


    int LerpCol(int c0, int c1, int c2, float e0, float e1, float e2){
        int r = (int) (((c0 >> 24) & 0xFF) * e0 + ((c1 >> 24) & 0xFF) * e1 + ((c2 >> 24) & 0xFF) * e2) & 0xFF;
        int g = (int) (((c0 >> 16) & 0xFF) * e0 + ((c1 >> 16) & 0xFF) * e1 + ((c2 >> 16) & 0xFF) * e2) & 0xFF;
        int b = (int) (((c0 >> 8) & 0xFF) * e0 + ((c1 >> 8) & 0xFF) * e1 + ((c2 >> 8) & 0xFF) * e2) & 0xFF;
        int a = (int) ((c0 & 0xFF) * e0 + (c1 & 0xFF) * e1 + (c2 & 0xFF) * e2) & 0xFF;
        return (r << 24) | (g << 16) | (b << 8) | (a);
    }

    float LerpChannel(float c0, float c1, float c2, float e0, float e1, float e2){
        return c0 * e0 + c1 * e1 + c2 * e2;
    }

    float PerpDot(float x0, float y0, float x1 ,float y1, float x2, float y2){
        return (x2 - x1) * (y0 - y1) - (y2 - y1) * (x0 - x1);
    }

    @LuaFunction
    public final void Text(IArguments param) throws LuaException {
        int ax = param.getInt(0);
        int ay = param.getInt(1);

        String text = ParamUtils.unicodeToStr(param.getString(2));

        int color = param.getInt(3);
        int mode = param.getInt(4);

        IFontLib font = DefaultFont.Instance;
        if(param.count() > 5){
            String ft_lib = param.getString(5);
            if(fontlibs.containsKey(ft_lib)){
                font = fontlibs.get(ft_lib);
            }
            else {
                throw new LuaException("Font %s is not exist.".formatted(ft_lib));
            }
        }

        int tmpx = ax;
        int[] buffer = currentBuffer.getBuffer();

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        if(mode == 0 || mode == 1){ // cutout
            color = ParamUtils.convertColor(color);
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (tmpx > width) continue;
                if (ch == '\n') {
                    ay += 16;
                    if (ay > high) break;
                    tmpx = ax;
                    continue;
                }
                Font.CharMat cm = font.get(ch);
                int edgeR = Math.min(ay + cm.bitmap.length, high);
                for (int y = Math.max(ay, 0); y < edgeR; y++) {
                    int offD = y * width;
                    int edgeD = Math.min(width, tmpx + cm.width);
                    for (int x = Math.max(tmpx, 0); x < edgeD; x++) {
                        if(cm.bitmap[y-ay][x-tmpx]){
                            buffer[offD + x] = color;
                        }
                    }
                }

                tmpx += cm.width;
            }
        }
        else if(mode == 2){ // blend
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (tmpx > width) continue;
                if (ch == '\n') {
                    ay += 16;
                    if (ay > high) break;
                    tmpx = ax;
                    continue;
                }
                Font.CharMat cm = font.get(ch);
                int edgeR = Math.min(ay + cm.bitmap.length, high);
                for (int y = Math.max(ay, 0); y < edgeR; y++) {
                    int offD = y * width;
                    int edgeD = Math.min(width, tmpx + cm.width);
                    for (int x = Math.max(tmpx, 0); x < edgeD; x++) {
                        if(cm.bitmap[y-ay][x-tmpx]){
                            buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(color, ParamUtils.convertColor(buffer[offD + x])));
                        }
                    }
                }
                tmpx += cm.width;
            }
        }

    }

    @LuaFunction
    public final void SetPixel(IArguments args) throws LuaException {
        int x = args.getInt(0);
        int y = args.getInt(1);
        int c = args.getInt(2);
        int w = currentBuffer.getWidth();
        if(x >= 0 && x < w && y >= 0 && y < currentBuffer.getHeight()){
            currentBuffer.getBuffer()[y*w + x] = ParamUtils.convertColor(c);
        }
        else throw new LuaException("Position out of range.");
    }

    @LuaFunction
    public final double GetPixel(IArguments args) throws LuaException {
        int x = args.getInt(0);
        int y = args.getInt(1);
        int w = currentBuffer.getWidth();
        if(x >= 0 && x < w && y >= 0 && y < currentBuffer.getHeight()){
            return ParamUtils.convertColor(currentBuffer.getBuffer()[y*w + x]);
        }
        else throw new LuaException("Position out of range.");
    }

    @LuaFunction
    public final Object[] GetBlockPos(){
        BlockPos bp = te.getBlockPos();
        return new Object[]{ bp.getX(), bp.getY(), bp.getZ() };
    }

    @LuaFunction
    public final void SetScale(double x, double y) throws LuaException {
        if(x > 3 || y > 3) throw new LuaException("Too large, max is 3.");
        synchronized (SYNC_LOCK) {
            te.scalex = (float) x;
            te.scaley = (float) y;
        }
        te.transformDirty.set(true);
    }

    @LuaFunction
    public final void Flush(IArguments param) throws LuaException {
        if (param.count() > 0){
            if (param.getBoolean(0)){
                te.fullSync.set(true);
                return;
            }
        }
        te.needSync.set(true);
    }

    @LuaFunction
    public final void SetRotation(double yaw, double pitch, double roll){
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


    @LuaFunction
    public final void SetTranslation(double x, double y, double z) throws LuaException {
        if(x > 16 || y > 16 || z > 16) throw new LuaException("Too far, max is 16.");
        synchronized (SYNC_LOCK) {
            te.offx = (float) x;
            te.offy = (float) y;
            te.offz = (float) z;
        }
        te.transformDirty.set(true);
    }

    @LuaFunction
    public final void Fill(IArguments param) throws LuaException {
        if(param.count() < 5) throw new LuaException("Need 5 argument, got " + param.count() + ".");
        int ax, ay, w, h;
        ax = param.getInt(0);
        ay = param.getInt(1);
        w = param.getInt(2);
        h = param.getInt(3);

        int width = currentBuffer.getWidth();
        int high = currentBuffer.getHeight();

        int mode = param.count() == 6 ? param.getInt(5) : 0;
        try {
            int color = param.getInt(4);
            int[] buffer = currentBuffer.getBuffer();
            int edgeD = Math.min(high, ay+h);
            if(mode == 0 || mode == 1){  //solid
                color = ParamUtils.convertColor(color);
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        buffer[offD + x] = color;
                    }
                }
            }
            else if (mode == 2){  //blend
                for(int y = Math.max(ay, 0); y < edgeD; ++y){
                    int offD = y * width;
                    int edgeR = Math.min(width ,ax+w);
                    for(int x = Math.max(ax, 0); x < edgeR; ++x){
                        buffer[offD + x] = ParamUtils.convertColor(ParamUtils.blendColor(color, ParamUtils.convertColor(buffer[offD + x])));
                    }
                }
            }
        } catch (LuaException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(dirty_ex+" "+dirty_ey);
        //System.out.println(ax + " " + ay + " " + (ax+w) + " " + (ay+h));
    }

    final Set<IComputerAccess> computers = Sets.newConcurrentHashSet();

    @GuardedBy("SYNC_LOCK")
    public final Object SYNC_LOCK = new Object();

    @Override
    public String getType() {
        return "hologram";
    }

    void FillBuffer(int ax, int ay, int w, int h, int color_raw){
        //int col = ParamUtils.convertColor(color);
        int[] buffer = currentBuffer.getBuffer();
        int width = currentBuffer.getWidth();
        int edgeD = Math.min(currentBuffer.getHeight(), ay + h);
        for(int y = Math.max(ay, 0); y < edgeD; ++y) {
            int offD = y * w;
            int edgeR = Math.min(width, ax + w);
            for (int x = Math.max(ax, 0); x < edgeR; ++x) {
                buffer[offD + x] = color_raw;
            }
        }
    }

    @LuaFunction
    public final void Clear(){
        Arrays.fill(currentBuffer.getBuffer(), currentBuffer.getInitColor());
    }

    @LuaFunction
    public final String CreateFont(String name) throws LuaException {
        if(name.isEmpty()){
            throw new LuaException("Empty string is not allowed.");
        }
        if(fontlibs.containsKey(name)){
            return "";
        }
        else {
            if(fontlibs.size() < Config.HologramFontCount){
                fontlibs.put(name, new FontLib());
                return name;
            }
            else {
                return null;
            }
        }
    }

    @LuaFunction
    public final void PutCharPointMat(IArguments param) throws LuaException {
        String name = param.getString(0);
        FontLib fl = null;
        if(fontlibs.containsKey(name)){
            fl = fontlibs.get(name);
        }
        else {
            throw new LuaException("Font '%s' is not registered.".formatted(name));
        }
        String orgc = param.getString(1);
        String ch = ParamUtils.unicodeToStr(orgc);
        if(ch.length() > 1){
            throw new LuaException("'%s' is not a ascii char or unicode char.".formatted(orgc));
        }

        int width = param.getInt(2);
        int height = param.getInt(3);
        Map<?, ?> mat = param.getTable(4);

        double idx;
        boolean[][] cmat = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                idx = i * width + j + 1;
                if(mat.containsKey(idx)){
                    cmat[i][j] = ((Double)mat.get(idx)) > 0.5;
                }
                else {
                    throw new LuaException("Point mat error.");
                }
            }
        }
        fl.put(ch.charAt(0), cmat);
    }

    @LuaFunction
    public final void DeleteFont(IArguments param) throws LuaException {
        if(param.count() == 0){
            fontlibs.clear();
        }
        else {
            fontlibs.remove(param.getString(0));
        }
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
        //System.out.println("A_ " + computer.getID());
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
        //System.out.println("D_ " + computer.getID());
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }
}
