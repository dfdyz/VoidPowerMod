package com.dfdyz.void_power.compat.cct.lua;


import com.dfdyz.void_power.utils.CCUtils;
import com.dfdyz.void_power.utils.VSUtils;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.joml.*;
import org.valkyrienskies.core.impl.game.ships.PhysInertia;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.physics_api.PoseVel;

import java.lang.Math;
import java.util.List;
import java.util.Map;


public class LuaPhysShip {

    private final PhysShipImpl physShip;
    private final ShipPhysStateSnapshot shipSnapshot;
    //private final Quaternionf rot;
    //private final double[][] ctrl_rotation;

    static final Vector3d Up = new Vector3d(0,1,0);

    public LuaPhysShip(ShipPhysStateSnapshot physShip, PhysShipImpl ship, EngineControllerTE te){
        this.shipSnapshot = physShip;
        this.physShip = ship;
        //shipSnapshot.ctrl_rot;
    }

    @LuaFunction
    public final Map<String, Object> getInertia()  {
        return Map.of(
                "momentOfInertiaTensor", CCUtils.dumpMat3(shipSnapshot.tensor),
                "mass", shipSnapshot.mass
        );
    }



    public record ShipPhysStateSnapshot(Vector3dc vel, Vector3dc omg, Vector3dc pos,
                                        Quaterniondc rot,
                                        double[][] ctrl_rot,
                                        Matrix3dc tensor, double mass
    ){
    }


    @LuaFunction()
    public final Map<String, Object> getShipPoseVel(){
        return Map.of(
                "velocity", CCUtils.dumpVec3(shipSnapshot.vel),
                "omega", CCUtils.dumpVec3(shipSnapshot.omg),
                "pos", CCUtils.dumpVec3(shipSnapshot.pos),
                "rot", CCUtils.dumpVec4(shipSnapshot.rot)
        );
    }

    @LuaFunction
    public final Map<String, Object> getControllerFacesVec() {
        return Map.of(
                "right", CCUtils.dumpVec3(shipSnapshot.ctrl_rot[0][0], shipSnapshot.ctrl_rot[1][0], shipSnapshot.ctrl_rot[2][0]),
                "up", CCUtils.dumpVec3(shipSnapshot.ctrl_rot[0][1], shipSnapshot.ctrl_rot[1][1], shipSnapshot.ctrl_rot[2][1]),
                "front", CCUtils.dumpVec3(-shipSnapshot.ctrl_rot[0][2], -shipSnapshot.ctrl_rot[1][2], -shipSnapshot.ctrl_rot[2][2])
        );
    }

    @LuaFunction
    public final Map<String, Object> getControllerEuler() {
        return Map.of(
                "yaw", Math.atan2(shipSnapshot.ctrl_rot[0][2], shipSnapshot.ctrl_rot[2][2]),
                "pitch", -Math.asin(shipSnapshot.ctrl_rot[1][2]),
                "roll", Math.atan2(shipSnapshot.ctrl_rot[1][0], shipSnapshot.ctrl_rot[1][1])
        );
    }

    @LuaFunction
    public final List<List<Double>> getControllerRotationMat(){
        return CCUtils.dumpMat3(shipSnapshot.ctrl_rot);
    }

    public static ShipPhysStateSnapshot createSnapshot(PhysShipImpl ship, EngineControllerTE te){

        PoseVel poseVel = ship.getPoseVel();
        PhysInertia inertia = ship.getInertia();

        double[][] ctrl_rotation;
        Matrix3d rm = poseVel.getRot().get(new Matrix3d());

        Direction face = te.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        if(face.equals(Direction.NORTH)){
            ctrl_rotation = new double[][]{
                    { rm.m00, rm.m10, rm.m20 },
                    { rm.m01, rm.m11, rm.m21 },
                    { rm.m02, rm.m12, rm.m22 }
            };
        }
        else if(face.equals(Direction.SOUTH)){
            ctrl_rotation = new double[][]{
                    { -rm.m00, rm.m10, -rm.m20 },
                    { -rm.m01, rm.m11, -rm.m21 },
                    { -rm.m02, rm.m12, -rm.m22 }
            };
        }
        else if(face == Direction.EAST) {
            ctrl_rotation = new double[][]{
                    { rm.m20, rm.m10, -rm.m00 },
                    { rm.m21, rm.m11, -rm.m01 },
                    { rm.m22, rm.m12, -rm.m02 }
            };
        }
        else {
            ctrl_rotation = new double[][]{
                    { -rm.m20, rm.m10, rm.m00 },
                    { -rm.m21, rm.m11, rm.m01 },
                    { -rm.m22, rm.m12, rm.m02 }
            };
        }

        return new ShipPhysStateSnapshot(
                new Vector3d(poseVel.getVel()),
                new Vector3d(poseVel.getOmega()),
                new Vector3d(poseVel.getPos()),
                new Quaterniond(poseVel.getRot()),
                ctrl_rotation,
                new Matrix3d(inertia.getMomentOfInertiaTensor()),
                inertia.getShipMass()
        );
    }

    @LuaFunction
    double getBuoyantFactor(){
        return this.physShip.getBuoyantFactor();
    }

    @LuaFunction
    public final boolean isStatic(){
        return this.physShip.isStatic();
    }

    @LuaFunction
    public final boolean doFluidDrag() {
        return this.physShip.getDoFluidDrag();
    }
}
