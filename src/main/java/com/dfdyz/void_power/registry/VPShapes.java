package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.hologram_monitor.HologramScreenBlock;
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
            //HOLO_SCREEN_H = new AllShapes.Builder(HologramScreenBlock.Shape()).forHorizontal(Direction.NORTH),
            HOLO_SCREEN_O = new AllShapes.Builder(HologramScreenBlock.ShapeO()).forHorizontal(Direction.NORTH)
    ;

    public static class ScreenShaper{
        public static VoxelShape L(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0, 0,6 / 16.0,
                    1.5 / 16.0,1, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape R(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(14.5 / 16.0, 0,6 / 16.0,
                    1,1, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape U(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0, 14.5 / 16.0,6 / 16.0,
                    1, 1, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }

        public static VoxelShape D(VoxelShape shapes){
            shapes = Shapes.join(shapes, Shapes.box(0, 0,6 / 16.0,
                    1, 1.5 / 16.0, 10 / 16.0), BooleanOp.OR);
            return shapes;
        }
    }

    public static final VoxelShaper HOLO_SCREEN_L, HOLO_SCREEN_LR, HOLO_SCREEN_LRU, HOLO_SCREEN_LRUD,
            HOLO_SCREEN_LRD, HOLO_SCREEN_LUD, HOLO_SCREEN_LU, HOLO_SCREEN_LD,
            HOLO_SCREEN_R, HOLO_SCREEN_RU, HOLO_SCREEN_RD, HOLO_SCREEN_RUD, HOLO_SCREEN_D, HOLO_SCREEN_U, HOLO_SCREEN_UD, HOLO_SCREEN_N
    ;

    public static final Map<MonitorEdgeState, VoxelShaper> ScreenStateShapeMap = Maps.newHashMap();

    static {
        VoxelShape lrud = HologramScreenBlock.Shape();
        HOLO_SCREEN_LRUD = new AllShapes.Builder(lrud).forHorizontal(Direction.NORTH);

        VoxelShape lru = ScreenShaper.D(lrud);
        HOLO_SCREEN_LRU = new AllShapes.Builder(lru).forHorizontal(Direction.NORTH);

        VoxelShape lrd = ScreenShaper.U(lrud);
        HOLO_SCREEN_LRD = new AllShapes.Builder(lrd).forHorizontal(Direction.NORTH);

        VoxelShape lr = ScreenShaper.U(lru);
        HOLO_SCREEN_LR = new AllShapes.Builder(lr).forHorizontal(Direction.NORTH);

        VoxelShape ld = ScreenShaper.R(lrd);
        HOLO_SCREEN_LD = new AllShapes.Builder(ld).forHorizontal(Direction.NORTH);

        VoxelShape lu = ScreenShaper.R(lru);
        HOLO_SCREEN_LU = new AllShapes.Builder(lu).forHorizontal(Direction.NORTH);

        VoxelShape lud = ScreenShaper.R(lrud);
        HOLO_SCREEN_LUD = new AllShapes.Builder(lud).forHorizontal(Direction.NORTH);

        VoxelShape l = ScreenShaper.U(lu);
        HOLO_SCREEN_L = new AllShapes.Builder(l).forHorizontal(Direction.NORTH);

        VoxelShape rud = ScreenShaper.L(lrud);
        HOLO_SCREEN_RUD = new AllShapes.Builder(rud).forHorizontal(Direction.NORTH);

        VoxelShape ru = ScreenShaper.D(rud);
        HOLO_SCREEN_RU = new AllShapes.Builder(ru).forHorizontal(Direction.NORTH);

        VoxelShape rd = ScreenShaper.U(rud);
        HOLO_SCREEN_RD = new AllShapes.Builder(rd).forHorizontal(Direction.NORTH);

        VoxelShape r = ScreenShaper.D(rd);
        HOLO_SCREEN_R = new AllShapes.Builder(r).forHorizontal(Direction.NORTH);

        VoxelShape ud = ScreenShaper.R(rud);
        HOLO_SCREEN_UD = new AllShapes.Builder(ud).forHorizontal(Direction.NORTH);

        VoxelShape u = ScreenShaper.D(ud);
        HOLO_SCREEN_U = new AllShapes.Builder(u).forHorizontal(Direction.NORTH);

        VoxelShape d = ScreenShaper.U(ud);
        HOLO_SCREEN_D = new AllShapes.Builder(d).forHorizontal(Direction.NORTH);

        VoxelShape n = ScreenShaper.D(d);
        HOLO_SCREEN_N = new AllShapes.Builder(n).forHorizontal(Direction.NORTH);

        ScreenStateShapeMap.put(MonitorEdgeState.NONE, HOLO_SCREEN_N);
        ScreenStateShapeMap.put(MonitorEdgeState.L, HOLO_SCREEN_L);
        ScreenStateShapeMap.put(MonitorEdgeState.LU, HOLO_SCREEN_LU);
        ScreenStateShapeMap.put(MonitorEdgeState.LD, HOLO_SCREEN_LD);
        ScreenStateShapeMap.put(MonitorEdgeState.LUD, HOLO_SCREEN_LUD);
        ScreenStateShapeMap.put(MonitorEdgeState.LR, HOLO_SCREEN_LR);
        ScreenStateShapeMap.put(MonitorEdgeState.LRU, HOLO_SCREEN_LRU);
        ScreenStateShapeMap.put(MonitorEdgeState.LRD, HOLO_SCREEN_LRD);
        ScreenStateShapeMap.put(MonitorEdgeState.LRUD, HOLO_SCREEN_LRUD);
        ScreenStateShapeMap.put(MonitorEdgeState.R, HOLO_SCREEN_R);
        ScreenStateShapeMap.put(MonitorEdgeState.RU, HOLO_SCREEN_RU);
        ScreenStateShapeMap.put(MonitorEdgeState.RD, HOLO_SCREEN_RD);
        ScreenStateShapeMap.put(MonitorEdgeState.RUD, HOLO_SCREEN_RUD);
        ScreenStateShapeMap.put(MonitorEdgeState.U, HOLO_SCREEN_U);
        ScreenStateShapeMap.put(MonitorEdgeState.D, HOLO_SCREEN_D);
        ScreenStateShapeMap.put(MonitorEdgeState.UD, HOLO_SCREEN_UD);






    }



}
