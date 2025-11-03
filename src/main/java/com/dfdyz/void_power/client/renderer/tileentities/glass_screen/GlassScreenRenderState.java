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
import java.util.Objects;
import java.util.Set;

public class GlassScreenRenderState implements ClientMonitor.RenderState{
    @GuardedBy("allMonitors")
    private static final Set<GlassScreenRenderState> allMonitors = new HashSet();

    public long lastRenderFrame = -1L;
    @Nullable
    public BlockPos lastRenderPos = null;
    @Nullable
    public DirectVertexBuffer backgroundBuffer;
    @Nullable
    public DirectVertexBuffer foregroundBuffer;
    @Nullable
    public DirectVertexBuffer foregroundNegBuffer;

    public GlassScreenRenderState() {
    }

    public boolean createBuffer(MonitorRenderer renderer) {
        if (Objects.requireNonNull(renderer) == MonitorRenderer.VBO) {
            if (this.backgroundBuffer != null) {
                return false;
            }

            this.deleteBuffers();
            this.backgroundBuffer = new DirectVertexBuffer();
            this.foregroundBuffer = new DirectVertexBuffer();
            this.foregroundNegBuffer = new DirectVertexBuffer();
            this.addMonitor();
            return true;
        }
        return false;
    }


    private void deleteBuffers() {
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
        if (this.backgroundBuffer != null) {
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
