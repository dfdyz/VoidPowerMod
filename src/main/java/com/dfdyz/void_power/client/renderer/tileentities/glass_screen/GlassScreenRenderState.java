package com.dfdyz.void_power.client.renderer.tileentities.glass_screen;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.mojang.blaze3d.platform.GlStateManager;
import dan200.computercraft.client.render.monitor.MonitorRenderState;
import dan200.computercraft.client.render.vbo.DirectBuffers;
import dan200.computercraft.client.render.vbo.DirectVertexBuffer;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.MonitorRenderer;
import net.minecraft.core.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GlassScreenRenderState implements ClientMonitor.RenderState{

    @GuardedBy("allMonitors")
    private static final Set<GlassScreenRenderState> allMonitors = new HashSet();

    public long lastRenderFrame = -1L;
    @Nullable
    public BlockPos lastRenderPos = null;
    public int tboBuffer;
    public int tboTexture;
    public int tboUniform;
    @Nullable
    public DirectVertexBuffer backgroundBuffer;
    @Nullable
    public DirectVertexBuffer foregroundBuffer;
    @Nullable
    public DirectVertexBuffer foregroundNegBuffer;

    public GlassScreenRenderState() {
    }

    public boolean createBuffer(MonitorRenderer renderer) {
        switch (renderer) {
            case TBO:
                if (this.tboBuffer != 0) {
                    return false;
                }

                this.deleteBuffers();
                this.tboBuffer = DirectBuffers.createBuffer();
                DirectBuffers.setEmptyBufferData(35882, this.tboBuffer, 35044);
                this.tboTexture = GlStateManager._genTexture();
                GL11.glBindTexture(35882, this.tboTexture);
                GL31.glTexBuffer(35882, 33330, this.tboBuffer);
                GL11.glBindTexture(35882, 0);
                this.tboUniform = DirectBuffers.createBuffer();
                DirectBuffers.setEmptyBufferData(35345, this.tboUniform, 35044);
                this.addMonitor();
                return true;
            case VBO:
                if (this.backgroundBuffer != null) {
                    return false;
                }

                this.deleteBuffers();
                this.backgroundBuffer = new DirectVertexBuffer();
                this.foregroundBuffer = new DirectVertexBuffer();
                this.foregroundNegBuffer = new DirectVertexBuffer();
                this.addMonitor();
                return true;
            default:
                return false;
        }
    }


    private void deleteBuffers() {
        if (this.tboBuffer != 0) {
            DirectBuffers.deleteBuffer(35882, this.tboBuffer);
            this.tboBuffer = 0;
        }

        if (this.tboTexture != 0) {
            GlStateManager._deleteTexture(this.tboTexture);
            this.tboTexture = 0;
        }

        if (this.tboUniform != 0) {
            DirectBuffers.deleteBuffer(35345, this.tboUniform);
            this.tboUniform = 0;
        }

        if (this.backgroundBuffer != null) {
            this.backgroundBuffer.close();
            this.backgroundBuffer = null;
        }

        if (this.foregroundBuffer != null) {
            this.foregroundBuffer.close();
            this.foregroundBuffer = null;
        }

        if (this.foregroundNegBuffer != null) {
            this.foregroundNegBuffer.close();
            this.foregroundNegBuffer = null;
        }
    }

    private void addMonitor() {
        synchronized(allMonitors) {
            allMonitors.add(this);
        }
    }

    @Override
    public void close() {
        if (this.tboBuffer != 0 || this.backgroundBuffer != null) {
            synchronized(allMonitors) {
                allMonitors.remove(this);
            }

            this.deleteBuffers();
        }

    }

    public static void destroyAll() {
        synchronized(allMonitors) {
            Iterator<GlassScreenRenderState> iterator = allMonitors.iterator();

            while(iterator.hasNext()) {
                GlassScreenRenderState monitor = iterator.next();
                monitor.deleteBuffers();
                iterator.remove();
            }

        }
    }
}
