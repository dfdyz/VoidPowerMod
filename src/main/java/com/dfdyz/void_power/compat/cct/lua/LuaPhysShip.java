package com.dfdyz.void_power.compat.cct.lua;


import com.dfdyz.void_power.utils.CCUtils;
import com.dfdyz.void_power.utils.VSUtils;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.util.Mth;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysInertia;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.physics_api.PoseVel;

import java.util.Map;


public class LuaPhysShip {
    private final PhysShipImpl physShip;
    public LuaPhysShip(PhysShipImpl physShip){
        this.physShip = physShip;
    }

    @LuaFunction
    public Map<String, Object> getInertia()  {
        PhysInertia inertia = physShip.getInertia();
        return Map.of(
                "momentOfInertiaTensor", CCUtils.dumpMat3(inertia.getMomentOfInertiaTensor()),
                "mass", inertia.getShipMass()
        );
    }

    @LuaFunction
    public Map<String, Object> getPoseVel(){
        PoseVel poseVel = this.physShip.getPoseVel();
        return Map.of(
                "velocity", CCUtils.dumpVec3(poseVel.getVel()),
                "omega", CCUtils.dumpVec3(poseVel.getOmega()),
                "pos", CCUtils.dumpVec3(poseVel.getPos()),
                "rot", CCUtils.dumpVec4(poseVel.getRot()),
                "yaw", getYaw(),
                "pitch", getPitch(),
                "roll", getRoll(),
                "up", CCUtils.dumpVec3(poseVel.getRot().transform(new Vector3d(0, 1, 0)))
        );
    }

    @LuaFunction
    public double getRoll() {
        double[][] rotMatrix = VSUtils.getRotationMatrixRaw(physShip);
        return Mth.atan2(rotMatrix[1][0], rotMatrix[1][1]);
    }

    double getYaw() {
        double[][] rotMatrix = VSUtils.getRotationMatrixRaw(physShip);
        return Mth.atan2(-rotMatrix[0][2], rotMatrix[2][2]);
    }

    double getPitch(){
        return Math.asin(VSUtils.getRotationMatrixRaw(physShip)[1][2]);
    }

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

    // tensor.transform(omega)
    @LuaFunction
    public Map<String, Double> transformOmega(double x, double y, double z){
        return CCUtils.dumpVec3(physShip.getInertia().getMomentOfInertiaTensor().transform(new Vector3d(x,y,z)));
    }
}
