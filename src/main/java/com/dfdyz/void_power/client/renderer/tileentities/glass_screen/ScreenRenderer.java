package com.dfdyz.void_power.client.renderer.tileentities.glass_screen;

import com.dfdyz.void_power.client.renderer.VPRenderTypes;
import com.dfdyz.void_power.mixin.IMonitorTEAccessor;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dan200.computercraft.client.FrameInfo;
import dan200.computercraft.client.integration.ShaderMod;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.monitor.MonitorRenderState;
import dan200.computercraft.client.render.text.DirectFixedWidthFontRenderer;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.client.render.vbo.DirectVertexBuffer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.peripheral.monitor.MonitorRenderer;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_HEIGHT;
import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_WIDTH;
import static dan200.computercraft.core.util.Nullability.assertNonNull;

public class ScreenRenderer extends SafeBlockEntityRenderer<GlassScreenTE> {

    private static final float MARGIN = (float) (MonitorBlockEntity.RENDER_MARGIN * 1.1);

    private static final Matrix3f IDENTITY_NORMAL = new Matrix3f().identity();

    private static @Nullable ByteBuffer backingBuffer;

    private static long lastFrame = -1;

    public ScreenRenderer(BlockEntityRendererProvider.Context context){
        super();
    }

    @Override
    protected void renderSafe(GlassScreenTE monitor, float partialTicks, PoseStack transform, MultiBufferSource bufferSource, int light, int overlay) {
        var originTerminal = monitor.getOriginClientMonitor();
        var originTE = ((IMonitorTEAccessor) monitor).Invoke_getOrigin();
        if (originTerminal == null || originTE == null) return;

        var origin = originTerminal.getOrigin();
        var renderState = originTerminal.getRenderState(MonitorRenderState::new);
        var monitorPos = monitor.getBlockPos();

        // Ensure each monitor terminal is rendered only once. We allow rendering a specific tile
        // multiple times in a single frame to ensure compatibility with shaders which may run a
        // pass multiple times.
        var renderFrame = FrameInfo.getRenderFrame();
        if (renderState.lastRenderFrame == renderFrame && !monitorPos.equals(renderState.lastRenderPos)) {
            return;
        }

        lastFrame = renderFrame;
        renderState.lastRenderFrame = renderFrame;
        renderState.lastRenderPos = monitorPos;

        var originPos = origin.getBlockPos();

        // Determine orientation
        var dir = origin.getDirection();
        var front = origin.getFront();
        var yaw = dir.toYRot();
        var pitch = DirectionUtil.toPitchAngle(front);

        // Setup initial transform
        transform.pushPose();
        transform.translate(
                originPos.getX() - monitorPos.getX() + 0.5,
                originPos.getY() - monitorPos.getY() + 0.5,
                originPos.getZ() - monitorPos.getZ() + 0.5
        );

        transform.mulPose(Axis.YN.rotationDegrees(yaw));
        transform.mulPose(Axis.XP.rotationDegrees(pitch));
        transform.translate(
                -0.5 + MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN,
                origin.getHeight() - 0.5 - (MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN) + 0,
                0
        );
        var xSize = origin.getWidth() - 2.0 * (MonitorBlockEntity.RENDER_MARGIN + MonitorBlockEntity.RENDER_BORDER);
        var ySize = origin.getHeight() - 2.0 * (MonitorBlockEntity.RENDER_MARGIN + MonitorBlockEntity.RENDER_BORDER);

        // Draw the contents
        var terminal = originTerminal.getTerminal();
        if (terminal != null && !ShaderMod.get().isRenderingShadowPass()) {
            // Draw a terminal
            int width = terminal.getWidth(), height = terminal.getHeight();
            int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;
            var xScale = xSize / pixelWidth;
            var yScale = ySize / pixelHeight;
            transform.pushPose();
            transform.scale((float) xScale, (float) -yScale, 1.0f);

            var matrix = transform.last().pose();

            if(originTE instanceof GlassScreenTE holo){
                //System.out.println("HOLO");
                renderTerminal(matrix, originTerminal, renderState, terminal, (float) (MARGIN / xScale), (float) (MARGIN / yScale), holo, holo.getTransMode(), holo.getTransparentIndex());
            }
            else {
                renderTerminal(matrix, originTerminal, renderState, terminal, (float) (MARGIN / xScale), (float) (MARGIN / yScale), null, true, 'f');
            }

            transform.popPose();
        } else {
            /*
            ScreenRenderUtils.drawEmptyTerminal(
                    FixedWidthFontRenderer.toVertexConsumer(transform, bufferSource.getBuffer(RenderTypes.TERMINAL)),
                    -MARGIN, MARGIN,
                    (float) (xSize + 2 * MARGIN), (float) -(ySize + MARGIN * 2)
            );*/
        }

        transform.popPose();

    }

    private static void renderTerminal(
            Matrix4f matrix, ClientMonitor monitor, MonitorRenderState renderState, Terminal terminal, float xMargin, float yMargin,
            GlassScreenTE te, boolean enableTransparent, char noBG_color
    ) {
        //int width = terminal.getWidth(), height = terminal.getHeight();
        //int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;

        //var renderType = currentRenderer();
        var redraw = monitor.pollTerminalChanged();
        if (te != null && te.pollChange()) redraw = true;
        if (renderState.createBuffer(MonitorRenderer.VBO)) redraw = true;

        var backgroundBuffer = assertNonNull(renderState.backgroundBuffer);
        var foregroundBuffer = assertNonNull(renderState.foregroundBuffer);
        if (redraw) {
            var size = ScreenRenderUtils.getVertexCount(terminal);

            // In an ideal world we could upload these both into one buffer. However, we can't render VBOs with
            // and starting and ending offset, and so need to use two buffers instead.

            //System.out.println("te: " + te.getBlockPos());
            //System.out.println("enableTransparent: " + enableTransparent);
            // m = peripheral.wrap("left")

            renderToBuffer(backgroundBuffer, size, sink ->
                    ScreenRenderUtils.drawTerminalBackground(sink, 0, 0, terminal, yMargin, yMargin, xMargin, xMargin, enableTransparent ? noBG_color : 'z'));

            renderToBuffer(foregroundBuffer, size, sink -> {
                ScreenRenderUtils.drawTerminalForeground(sink, 0, 0, terminal);
                // If the cursor is visible, we append it to the end of our buffer. When rendering, we can either
                // render n or n+1 quads and so toggle the cursor on and off.
                ScreenRenderUtils.drawCursor(sink, 0, 0, terminal);
            });
        }

        // Our VBO doesn't transform its vertices with the provided pose stack, which means that the inverse view
        // rotation matrix gives entirely wrong numbers for fog distances. We just set it to the identity which
        // gives a good enough approximation.
        var oldInverseRotation = RenderSystem.getInverseViewRotationMatrix();
        RenderSystem.setInverseViewRotationMatrix(IDENTITY_NORMAL);
        RenderSystem.disableCull();

        RenderTypes.TERMINAL.setupRenderState();
        // Render background geometry
        backgroundBuffer.bind();
        backgroundBuffer.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), VPRenderTypes.text());

        // Render foreground geometry with glPolygonOffset enabled.
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();

        foregroundBuffer.bind();
        foregroundBuffer.drawWithShader(
                matrix, RenderSystem.getProjectionMatrix(), VPRenderTypes.text(),
                // As mentioned in the above comment, render the extra cursor quad if it is visible this frame. Each
                // // quad has an index count of 6.
                FixedWidthFontRenderer.isCursorVisible(terminal) && FrameInfo.getGlobalCursorBlink()
                        ? foregroundBuffer.getIndexCount() + 6 : foregroundBuffer.getIndexCount()
        );

        // Clear state
        RenderSystem.polygonOffset(0.0f, -0.0f);
        RenderSystem.disablePolygonOffset();
        RenderTypes.TERMINAL.clearRenderState();
        VertexBuffer.unbind();
        RenderSystem.enableCull();
        RenderSystem.setInverseViewRotationMatrix(oldInverseRotation);
    }


    /*
    @Override
    protected void renderSafe(HologramScreenTE monitor, float partialTicks, PoseStack transform, MultiBufferSource bufferSource, int light, int overlay) {
        var originTerminal = monitor.getOriginClientMonitor();
        var originTE = ((IMonitorTEAccessor) monitor).Invoke_getOrigin();
        if (originTerminal == null || originTE == null) return;

        var origin = originTerminal.getOrigin();
        var renderState = originTerminal.getRenderState(MonitorRenderState::new);
        var monitorPos = monitor.getBlockPos();

        // Ensure each monitor terminal is rendered only once. We allow rendering a specific tile
        // multiple times in a single frame to ensure compatibility with shaders which may run a
        // pass multiple times.
        var renderFrame = FrameInfo.getRenderFrame();
        if (renderState.lastRenderFrame == renderFrame && !monitorPos.equals(renderState.lastRenderPos)) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cpos = camera.getPosition();

        lastFrame = renderFrame;
        renderState.lastRenderFrame = renderFrame;
        renderState.lastRenderPos = monitorPos;

        var originPos = origin.getBlockPos();

        // Determine orientation
        var dir = origin.getDirection();
        var front = origin.getFront();
        var yaw = dir.toYRot();
        var pitch = DirectionUtil.toPitchAngle(front);

        // Setup initial transform
        System.out.println("Frame");

        transform.pushPose();
        System.out.println( + cpos);
        transform.translate(cpos.x, cpos.y, cpos.z);
        transform.translate(
                originPos.getX() - monitorPos.getX() + 0.5,
                originPos.getY() - monitorPos.getY() + 0.5,
                originPos.getZ() - monitorPos.getZ() + 0.5
        );

        transform.mulPose(Axis.YN.rotationDegrees(yaw));
        transform.mulPose(Axis.XP.rotationDegrees(pitch));
        transform.translate(
                -0.5 + MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN,
                origin.getHeight() - 0.5 - (MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN) + 0,
                0
        );
        var xSize = origin.getWidth() - 2.0 * (MonitorBlockEntity.RENDER_MARGIN + MonitorBlockEntity.RENDER_BORDER);
        var ySize = origin.getHeight() - 2.0 * (MonitorBlockEntity.RENDER_MARGIN + MonitorBlockEntity.RENDER_BORDER);

        // Draw the contents
        var terminal = originTerminal.getTerminal();
        if (terminal != null && !ShaderMod.get().isRenderingShadowPass()) {
            // Draw a terminal
            int width = terminal.getWidth(), height = terminal.getHeight();
            int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;
            var xScale = xSize / pixelWidth;
            var yScale = ySize / pixelHeight;
            transform.pushPose();
            transform.scale((float) xScale, (float) -yScale, 1.0f);
            //transform.scale(10,10,10);

            var matrix = transform.last().pose();

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

            if(originTE instanceof HologramScreenTE holo){
                //System.out.println("HOLO");
                renderTerminal(buffer, matrix, terminal, (float) (MARGIN / xScale), (float) (MARGIN / yScale), holo, holo.getTransMode(), holo.getTransparentIndex());
            }
            else {
                renderTerminal(buffer, matrix, terminal, (float) (MARGIN / xScale), (float) (MARGIN / yScale), null, true, 'f');
            }

            transform.popPose();
        } else {

            var matrix = transform.last().pose();
            MCPipelineRenderer.drawEmptyTerminal(
                    transform, bufferSource.getBuffer(VPRenderTypes.TERMINAL), matrix,
                    -MARGIN, MARGIN,
                    (float) (xSize + 2 * MARGIN), (float) -(ySize + MARGIN * 2)
            );
        }

        transform.popPose();

    }


    private static void renderTerminal(VertexConsumer buffer,
            Matrix4f matrix, ClientMonitor monitor, MonitorRenderState renderState Terminal terminal, float xMargin, float yMargin,
            HologramScreenTE te, boolean enableTransparent, char noBG_color
    ) {
        //int width = terminal.getWidth(), height = terminal.getHeight();
        //int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;

        //var renderType = currentRenderer();
        //var redraw = monitor.pollTerminalChanged();
        //if (te != null && te.pollChange()) redraw = true;
        //if (renderState.createBuffer(MonitorRenderer.VBO)) redraw = true;

        //var backgroundBuffer = assertNonNull(renderState.backgroundBuffer);
        //var foregroundBuffer = assertNonNull(renderState.foregroundBuffer);
        //if (redraw) {
        //    var size = ScreenRenderUtils.getVertexCount(terminal);

            // In an ideal world we could upload these both into one buffer. However, we can't render VBOs with
            // and starting and ending offset, and so need to use two buffers instead.

            //System.out.println("te: " + te.getBlockPos());
            //System.out.println("enableTransparent: " + enableTransparent);
            // m = peripheral.wrap("left")
        MCPipelineRenderer.SetUpUV();
            //renderToBuffer(backgroundBuffer, size, sink ->
        MCPipelineRenderer.drawTerminalBackground(buffer, matrix, 0, 0, terminal, yMargin, yMargin, xMargin, xMargin, enableTransparent ? noBG_color : 'z');
            //);
            //renderToBuffer(foregroundBuffer, size, sink -> {
        MCPipelineRenderer.drawTerminalForeground(buffer, matrix, 0, 0, terminal);
                // If the cursor is visible, we append it to the end of our buffer. When rendering, we can either
                // render n or n+1 quads and so toggle the cursor on and off.
        MCPipelineRenderer.drawCursor(buffer, matrix, 0, 0, terminal);




            //});
        //}

        // FFFFFFFFFFFFFFFFFFFFF
        // Our VBO doesn't transform its vertices with the provided pose stack, which means that the inverse view
        // rotation matrix gives entirely wrong numbers for fog distances. We just set it to the identity which
        // gives a good enough approximation.
        //var oldInverseRotation = RenderSystem.getInverseViewRotationMatrix();
        //RenderSystem.setInverseViewRotationMatrix(IDENTITY_NORMAL);
        //RenderSystem.disableCull();

        //VPRenderTypes.TERMINAL.setupRenderState();
        // Render background geometry
        //backgroundBuffer.bind();
        //backgroundBuffer.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), RenderTypes.getTerminalShader());

        // Render foreground geometry with glPolygonOffset enabled.
        //RenderSystem.polygonOffset(-1.0f, -10.0f);
        //RenderSystem.enablePolygonOffset();

        //foregroundBuffer.bind();
        //foregroundBuffer.drawWithShader(
                //matrix, RenderSystem.getProjectionMatrix(), RenderTypes.getTerminalShader(),
                // As mentioned in the above comment, render the extra cursor quad if it is visible this frame. Each
                // // quad has an index count of 6.
                //FixedWidthFontRenderer.isCursorVisible(terminal) && FrameInfo.getGlobalCursorBlink()
                        //? foregroundBuffer.getIndexCount() + 6 : foregroundBuffer.getIndexCount()
        //);

        // Clear state
        //RenderSystem.polygonOffset(0.0f, -0.0f);
        //RenderSystem.disablePolygonOffset();

        //VPRenderTypes.TERMINAL.clearRenderState();
        //VertexBuffer.unbind();
        //RenderSystem.enableCull();
        //RenderSystem.setInverseViewRotationMatrix(oldInverseRotation);
    }*/

    private static void renderToBuffer(DirectVertexBuffer vbo, int size, Consumer<DirectFixedWidthFontRenderer.QuadEmitter> draw) {
        var sink = ShaderMod.get().getQuadEmitter(size, ScreenRenderer::getBuffer);
        var buffer = sink.buffer();

        draw.accept(sink);
        buffer.flip();
        vbo.upload(buffer.limit() / sink.format().getVertexSize(), VPRenderTypes.TERMINAL.mode(), sink.format(), buffer);
    }


    private static ByteBuffer getBuffer(int capacity) {
        var buffer = backingBuffer;
        if (buffer == null || buffer.capacity() < capacity) {
            buffer = backingBuffer = buffer == null ? MemoryTracker.create(capacity) : MemoryTracker.resize(buffer, capacity);
        }
        buffer.clear();
        return buffer;
    }

    @Override
    public int getViewDistance() {
        return Config.monitorDistance;
    }


    public static boolean hasRenderedThisFrame() {
        return FrameInfo.getRenderFrame() == lastFrame;
    }



}
