package com.dfdyz.void_power.compat.cct.lua;


import com.dfdyz.void_power.utils.CCUtils;
import com.dfdyz.void_power.utils.VSUtils;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.util.Mth;
import org.joml.*;
import org.valkyrienskies.core.impl.game.ships.PhysInertia;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.physics_api.PoseVel;

import java.lang.Math;
import java.util.Map;


public class LuaPhysShip {

    private final PhysShipImpl physShip;
    private final ShipPhysStateSnapshot shipSnapshot;
    public LuaPhysShip(ShipPhysStateSnapshot physShip, PhysShipImpl ship){
        this.shipSnapshot = physShip;
        this.physShip = ship;
    }

    @LuaFunction
    public Map<String, Object> getInertia()  {
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


    @LuaFunction
    public Map<String, Object> getPoseVel(){
        return Map.of(
                "velocity", CCUtils.dumpVec3(shipSnapshot.vel),
                "omega", CCUtils.dumpVec3(shipSnapshot.omg),
                "pos", CCUtils.dumpVec3(shipSnapshot.pos),
                "rot", CCUtils.dumpVec4(shipSnapshot.rot),
                "yaw", shipSnapshot.yaw,
                "pitch", shipSnapshot.pitch,
                "roll", shipSnapshot.roll,
                "up", CCUtils.dumpVec3(shipSnapshot.rot.transform(new Vector3d(0, 1, 0)))
        );
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
                Mth.atan2(-rotMatrix[0][2], rotMatrix[2][2]),
                Math.asin(rotMatrix[1][2]),
                Mth.atan2(rotMatrix[1][0], rotMatrix[1][1]),
                new Matrix3d(inertia.getMomentOfInertiaTensor()),
                inertia.getShipMass()
        );
    }

    @LuaFunction
    double getBuoyantFactor(){
        return this.physShip.getBuoyantFactor();
    }

    @LuaFunction
    public boolean isStatic(){
        return this.physShip.isStatic();
    }

    @LuaFunction
    public boolean doFluidDrag() {
        return this.physShip.getDoFluidDrag();
    }
}
