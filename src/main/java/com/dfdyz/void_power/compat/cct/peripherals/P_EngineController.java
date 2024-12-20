package com.dfdyz.void_power.compat.cct.peripherals;

import com.dfdyz.void_power.compat.cct.lua.LuaPhysShip;
import com.dfdyz.void_power.compat.vs.ship.QueuedForceApplier;
import com.dfdyz.void_power.utils.CCUtils;
import com.dfdyz.void_power.utils.VSUtils;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBi;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.lang.Math;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class P_EngineController implements IPeripheral {
    private final EngineControllerTE te;
    public P_EngineController(EngineControllerTE te){
        this.te = te;
    }

    private final Set<IComputerAccess> computers = Sets.newConcurrentHashSet();

    @Override
    public String getType() {
        return "EngineController";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return Objects.equals(this, iPeripheral);
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
        //System.out.println("ATTACH");
    }

    @Override
    public void detach(IComputerAccess computer) {
        //System.out.println("DETACH");
        computers.remove(computer);
        if(computers.isEmpty() && te.hasShip()){
            te.getCtrl().setIdle(true);
        }
    }

    public void PushEvent(PhysShipImpl physShip){
        LuaPhysShip.ShipPhysStateSnapshot snapshot = LuaPhysShip.createSnapshot(physShip, te);
        computers.forEach((c) -> {
            c.queueEvent("phys_tick", new Object[]{new LuaPhysShip(snapshot, physShip, te)});
        });
    }

    @LuaFunction
    public long getId(){
        if(te.hasShip()){
            return te.getShip().getId();
        }
        return -1;
    }

    @LuaFunction
    public double getMass(){
        if(te.hasShip()){
            return te.getShip().getInertiaData().getMass();
        }
        return 0;
    }

    @LuaFunction
    public List<List<Double>> getMomentOfInertiaTensor() {
        if(te.hasShip()){
            return CCUtils.dumpMat3(te.getShip().getInertiaData().getMomentOfInertiaTensor());
        }
        return null;
    }

    @LuaFunction
    public String getName(){
        if(te.hasShip()){
            return te.getShip().getSlug();
        }
        return "";
    }


    @LuaFunction
    public Map<String, Double> getOmega(){
        if(te.hasShip()){
            return CCUtils.dumpVec3(te.getShip().getOmega());
        }
        return CCUtils.dumpVec3(0,0,0);
    }

    @LuaFunction
    public Map<String, Double> getRotation() {
        if(te.hasShip()){
            Quaterniondc rot = te.getShip().getTransform().getShipToWorldRotation();
            return CCUtils.dumpVec4(rot);
        }
        return CCUtils.dumpVec4(0,0,0,0);
    }

    @LuaFunction
    public Map<String, Double> getFaceVector() {
        return CCUtils.dumpVec3(te.getFace());
    }

    @LuaFunction
    public Map<String, Double> getFaceRaw() {
        return CCUtils.dumpVec3(te.getFaceRaw());
    }


    @LuaFunction
    public double getRoll() {
        if(te.hasShip()){
            double[][] rotMatrix = VSUtils.getRotationMatrixRaw(te.getShip());
            return Mth.atan2(rotMatrix[1][0], rotMatrix[1][1]);
        }
        return 0;
    }

    @LuaFunction
    public double getYaw() {
        if(te.hasShip()){
            double[][] rotMatrix = VSUtils.getRotationMatrixRaw(te.getShip());
            return Mth.atan2(-rotMatrix[0][2], rotMatrix[2][2]);
        }
        return 0;
    }

    @LuaFunction
    public double getPitch(){
        if(te.hasShip()){
            return Math.asin(VSUtils.getRotationMatrixRaw(te.getShip())[1][2]);
        }
        return 0;
    }

    @LuaFunction
    public List<List<Double>> getRotationMatrix(){
        if(te.hasShip()){
            return getRotationMatrix(te.getShip());
        }
        return null;
    }

    @LuaFunction
    public boolean isIdle(){
        if(te.hasShip()){
            return  te.getCtrl().isIdle();
        }
        return true;
    }

    @LuaFunction
    public void setIdle(boolean idle){
        if(te.hasShip()){
            te.getCtrl().setIdle(idle);
        }
    }

    @LuaFunction
    public void forcedDisableIdle(boolean b){
        if(te.hasShip()){
            te.getCtrl().disableIdle(b);
        }
    }

    @LuaFunction
    public boolean isOnShip(){
        return te.hasShip();
    }

    @LuaFunction
    public Map<String, Double> getVelocity(){
        if(te.hasShip()){
            return CCUtils.dumpVec3(te.getShip().getVelocity());
        }
        return CCUtils.dumpVec3(0,0,0);
    }

    @LuaFunction
    public Map<String, Double> getPosition(){
        if(te.hasShip()){
            return CCUtils.dumpVec3(te.getShip().getTransform().getPositionInWorld());
        }
        return CCUtils.dumpVec3(0,0,0);
    }//▀▄

    private List<List<Double>> getRotationMatrix(ServerShip ship){
        Matrix4dc transform = ship.getTransform().getShipToWorld();
        List<List<Double>> matrix = Lists.newArrayList();

        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            matrix.add(List.of(row.x, row.y, row.z, row.w));
        }
        return matrix;
    }

    @LuaFunction
    public double getMassCanDrive(){
        if(te.hasShip()){
            return te.massCanDrive();
        }
        return 0;
    }

    /***
     * power API
     */
    @LuaFunction
    public void applyInvariantForce(double x, double y, double z) {
        QueuedForceApplier applier = te.getApplier();
        //System.out.println("APPLY_0");
        if(applier != null){
            //System.out.println("APPLY_1");
            applier.applyInvariantForce(new Vector3d(x,y,z));
        }
    }

    @LuaFunction
    public void applyInvariantTorque(double x, double y, double z) {
        QueuedForceApplier applier = te.getApplier();
        if(applier != null){
            applier.applyInvariantTorque(new Vector3d(x,y,z));
        }
    }

    @LuaFunction
    public void applyInvariantForceToPos(double px, double py, double pz, double fx, double fy, double fz) {
        QueuedForceApplier applier = te.getApplier();
        if(applier != null){
            applier.applyInvariantForceToPos(new Vector3d(fx,fy,fz), new Vector3d(px,py,pz));
        }
    }

    @LuaFunction
    public void applyRotDependentForce(double x, double y, double z) {
        QueuedForceApplier applier = te.getApplier();
        if(applier != null){
            applier.applyRotDependentForce(new Vector3d(x,y,z));
        }
    }

    @LuaFunction
    public void applyRotDependentTorque(double x, double y, double z) {
        QueuedForceApplier applier = te.getApplier();
        if(applier != null){
            applier.applyRotDependentTorque(new Vector3d(x,y,z));
        }
    }

    @LuaFunction
    public void applyRotDependentForceToPos(double px, double py, double pz, double fx, double fy, double fz) {
        QueuedForceApplier applier = te.getApplier();
        if(applier != null){
            applier.applyRotDependentForceToPos(new Vector3d(fx,fy,fz), new Vector3d(px,py,pz));
        }
    }

    @LuaFunction
    public boolean isStatic(){
        if(te.hasShip()){
            return te.getShip().isStatic();
        }
        return true;
    }

    @LuaFunction
    public Map<String, Double> getScale(){
        if(te.hasShip()){
            Vector3d s = te.getShip().getShipToWorld().getScale(new Vector3d());
            return CCUtils.dumpVec3(s);
        }
        return null;
    }

    @LuaFunction
    public Map<String, Double> getSize(){
        if(te.hasShip()){
            var aabb = te.getShip().getShipAABB();
            if(aabb == null) aabb = new AABBi(0, 0, 0, 0, 0, 0);
            return CCUtils.dumpVec3(
                    aabb.maxX() - aabb.minX(),
                    aabb.maxY() - aabb.minY(),
                    aabb.maxZ() - aabb.minZ()
            );
        }
        return null;
    }

    @LuaFunction
    public Map<String, Double> getShipCenter(){ //getShipyardPosition
        if(te.hasShip()){
            Vector3dc s = te.getShip().getTransform().getPositionInShip();
            return CCUtils.dumpVec3(s);
        }
        return null;
    }


}
