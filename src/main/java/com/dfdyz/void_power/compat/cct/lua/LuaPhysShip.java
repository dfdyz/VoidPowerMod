package com.dfdyz.void_power.compat.cct.lua;


import com.dfdyz.void_power.utils.CCUtils;
import com.dfdyz.void_power.utils.VSUtils;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
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
    private final Matrix3d ctrl_rotation;

    static final Vector3d Up = new Vector3d(0,1,0);

    public LuaPhysShip(ShipPhysStateSnapshot physShip, PhysShipImpl ship, EngineControllerTE te){
        this.shipSnapshot = physShip;
        this.physShip = ship;
        //this.rot = te.getBlockState().getValue(EngineControllerBlock.FACING).getRotation();
        Matrix3d rm = shipSnapshot.rot.get(new Matrix3d());

        var face = te.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        if(face == Direction.NORTH){
            ctrl_rotation = rm;
        }
        else if(face == Direction.SOUTH){
            ctrl_rotation = new Matrix3d(
                    -rm.m00, rm.m01, -rm.m02,
                    -rm.m10, rm.m11, -rm.m12,
                    -rm.m20, rm.m21, -rm.m22
            );
        }
        else if(face == Direction.EAST) {
            ctrl_rotation = new Matrix3d(
                    -rm.m02, rm.m01, rm.m00,
                    -rm.m12, rm.m11, rm.m10,
                    -rm.m22, rm.m21, rm.m20
            );
        }
        else {
            ctrl_rotation = new Matrix3d(
                    rm.m02, rm.m01, -rm.m00,
                    rm.m12, rm.m11, -rm.m10,
                    rm.m22, rm.m21, -rm.m20
            );
        }
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
                                        double yaw, double pitch, double roll,
                                        Matrix3dc tensor, double mass
    ){
    }


    @LuaFunction()
    public final Map<String, Object> getShipPoseVel(){
        return Map.of(
                "velocity", CCUtils.dumpVec3(shipSnapshot.vel),
                "omega", CCUtils.dumpVec3(shipSnapshot.omg),
                "pos", CCUtils.dumpVec3(shipSnapshot.pos),
                "rot", CCUtils.dumpVec4(shipSnapshot.rot),
                "yaw", shipSnapshot.yaw,
                "pitch", shipSnapshot.pitch,
                "roll", shipSnapshot.roll
        );
    }


    @LuaFunction
    public final Map<String, Object> getControllerFacesVec(){
        return Map.of(
                "right", CCUtils.dumpVec3(ctrl_rotation.m00(), ctrl_rotation.m10(), ctrl_rotation.m20()),
                "up", CCUtils.dumpVec3(ctrl_rotation.m01(), ctrl_rotation.m11(), ctrl_rotation.m21()),
                "front", CCUtils.dumpVec3(-ctrl_rotation.m02(), -ctrl_rotation.m12(), -ctrl_rotation.m22())
        );
    }

    @LuaFunction
    public final List<List<Double>> getControllerRotationMat(){
        return CCUtils.dumpMat3(ctrl_rotation);
    }

    public static ShipPhysStateSnapshot createSnapshot(PhysShipImpl ship){
        double[][] rotMatrix = VSUtils.getRotationMatrixRaw(ship);
        PoseVel poseVel = ship.getPoseVel();
        PhysInertia inertia = ship.getInertia();
        return new ShipPhysStateSnapshot(
                new Vector3d(poseVel.getVel()),
                new Vector3d(poseVel.getOmega()),
                new Vector3d(poseVel.getPos()),
                new Quaterniond(poseVel.getRot()),
                Mth.atan2(rotMatrix[0][2], rotMatrix[2][2]),
                Mth.atan2(rotMatrix[1][0], rotMatrix[1][1]),
                Mth.atan2(rotMatrix[1][2], rotMatrix[1][1]),
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
