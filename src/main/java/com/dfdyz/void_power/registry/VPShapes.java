package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenBlock;
import com.dfdyz.void_power.world.blocks.hologram.HologramBlock;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineBlock;
import com.google.common.collect.Maps;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import dan200.computercraft.shared.peripheral.monitor.MonitorEdgeState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class VPShapes {



    public static final VoxelShaper
            VOID_ENGINE = new AllShapes.Builder(VoidEngineBlock.Shape()).forHorizontal(Direction.NORTH),
            ENGINE_CONTROLLER = new AllShapes.Builder(EngineControllerBlock.Shape()).forHorizontal(Direction.NORTH),
            HOLOGRAM = new AllShapes.Builder(HologramBlock.Shape()).forHorizontal(Direction.NORTH),
            GLASS_SCREEN_O = new AllShapes.Builder(GlassScreenBlock.ShapeO()).forHorizontal(Direction.NORTH)
    ;

    public static class ScreenShaper{
        public static VoxelShape L(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0.001, 0.001,6 / 16.0,
                    1.5 / 16.0,0.999, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape R(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(14.5 / 16.0, 0.001,6 / 16.0,
                    0.999,0.999, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape U(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0.001, 14.5 / 16.0,6 / 16.0,
                    0.999, 0.999, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape D(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0.001, 0.001,6 / 16.0,
                    0.999, 1.5 / 16.0, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }
    }

    public static final VoxelShaper GLASS_SCREEN_L, GLASS_SCREEN_LR, GLASS_SCREEN_LRU, GLASS_SCREEN_LRUD,
            GLASS_SCREEN_LRD, GLASS_SCREEN_LUD, GLASS_SCREEN_LU, GLASS_SCREEN_LD,
            GLASS_SCREEN_R, GLASS_SCREEN_RU, GLASS_SCREEN_RD, GLASS_SCREEN_RUD, GLASS_SCREEN_D, GLASS_SCREEN_U, GLASS_SCREEN_UD, GLASS_SCREEN_N
    ;

    public static final Map<MonitorEdgeState, VoxelShaper> ScreenStateShapeMap = Maps.newHashMap();

    static {
        VoxelShape lrud = GlassScreenBlock.Shape();
        GLASS_SCREEN_LRUD = new AllShapes.Builder(lrud).forHorizontal(Direction.NORTH);

        VoxelShape lru = ScreenShaper.D(lrud);
        GLASS_SCREEN_LRU = new AllShapes.Builder(lru).forHorizontal(Direction.NORTH);

        VoxelShape lrd = ScreenShaper.U(lrud);
        GLASS_SCREEN_LRD = new AllShapes.Builder(lrd).forHorizontal(Direction.NORTH);

        VoxelShape lr = ScreenShaper.U(lru);
        GLASS_SCREEN_LR = new AllShapes.Builder(lr).forHorizontal(Direction.NORTH);

        VoxelShape ld = ScreenShaper.R(lrd);
        GLASS_SCREEN_LD = new AllShapes.Builder(ld).forHorizontal(Direction.NORTH);

        VoxelShape lu = ScreenShaper.R(lru);
        GLASS_SCREEN_LU = new AllShapes.Builder(lu).forHorizontal(Direction.NORTH);

        VoxelShape lud = ScreenShaper.R(lrud);
        GLASS_SCREEN_LUD = new AllShapes.Builder(lud).forHorizontal(Direction.NORTH);

        VoxelShape l = ScreenShaper.U(lu);
        GLASS_SCREEN_L = new AllShapes.Builder(l).forHorizontal(Direction.NORTH);

        VoxelShape rud = ScreenShaper.L(lrud);
        GLASS_SCREEN_RUD = new AllShapes.Builder(rud).forHorizontal(Direction.NORTH);

        VoxelShape ru = ScreenShaper.D(rud);
        GLASS_SCREEN_RU = new AllShapes.Builder(ru).forHorizontal(Direction.NORTH);

        VoxelShape rd = ScreenShaper.U(rud);
        GLASS_SCREEN_RD = new AllShapes.Builder(rd).forHorizontal(Direction.NORTH);

        VoxelShape r = ScreenShaper.D(rd);
        GLASS_SCREEN_R = new AllShapes.Builder(r).forHorizontal(Direction.NORTH);

        VoxelShape ud = ScreenShaper.R(rud);
        GLASS_SCREEN_UD = new AllShapes.Builder(ud).forHorizontal(Direction.NORTH);

        VoxelShape u = ScreenShaper.D(ud);
        GLASS_SCREEN_U = new AllShapes.Builder(u).forHorizontal(Direction.NORTH);

        VoxelShape d = ScreenShaper.U(ud);
        GLASS_SCREEN_D = new AllShapes.Builder(d).forHorizontal(Direction.NORTH);

        VoxelShape n = ScreenShaper.D(d);
        GLASS_SCREEN_N = new AllShapes.Builder(n).forHorizontal(Direction.NORTH);

        ScreenStateShapeMap.put(MonitorEdgeState.NONE, GLASS_SCREEN_N);
        ScreenStateShapeMap.put(MonitorEdgeState.L, GLASS_SCREEN_L);
        ScreenStateShapeMap.put(MonitorEdgeState.LU, GLASS_SCREEN_LU);
        ScreenStateShapeMap.put(MonitorEdgeState.LD, GLASS_SCREEN_LD);
        ScreenStateShapeMap.put(MonitorEdgeState.LUD, GLASS_SCREEN_LUD);
        ScreenStateShapeMap.put(MonitorEdgeState.LR, GLASS_SCREEN_LR);
        ScreenStateShapeMap.put(MonitorEdgeState.LRU, GLASS_SCREEN_LRU);
        ScreenStateShapeMap.put(MonitorEdgeState.LRD, GLASS_SCREEN_LRD);
        ScreenStateShapeMap.put(MonitorEdgeState.LRUD, GLASS_SCREEN_LRUD);
        ScreenStateShapeMap.put(MonitorEdgeState.R, GLASS_SCREEN_R);
        ScreenStateShapeMap.put(MonitorEdgeState.RU, GLASS_SCREEN_RU);
        ScreenStateShapeMap.put(MonitorEdgeState.RD, GLASS_SCREEN_RD);
        ScreenStateShapeMap.put(MonitorEdgeState.RUD, GLASS_SCREEN_RUD);
        ScreenStateShapeMap.put(MonitorEdgeState.U, GLASS_SCREEN_U);
        ScreenStateShapeMap.put(MonitorEdgeState.D, GLASS_SCREEN_D);
        ScreenStateShapeMap.put(MonitorEdgeState.UD, GLASS_SCREEN_UD);






    }



}
