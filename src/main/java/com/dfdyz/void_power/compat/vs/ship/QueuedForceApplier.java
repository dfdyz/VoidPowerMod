package com.dfdyz.void_power.compat.vs.ship;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Queues;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.util.VSCoreUtilKt;
import org.valkyrienskies.physics_api.PoseVel;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("ClassEscapesDefinedScope")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueuedForceApplier implements ShipForcesInducer {
    //@JsonIgnore ServerShip ship;
    @JsonIgnore private final Queue<Vector3dc> invForces = Queues.newConcurrentLinkedQueue();
    @JsonIgnore private final Queue<Vector3dc> invTorques = Queues.newConcurrentLinkedQueue();
    @JsonIgnore private final Queue<Vector3dc> rotForces = Queues.newConcurrentLinkedQueue();
    @JsonIgnore private final Queue<Vector3dc> rotTorques = Queues.newConcurrentLinkedQueue();
    @JsonIgnore private final Queue<ForceAtPos> invPosForces = Queues.newConcurrentLinkedQueue();
    @JsonIgnore private final Queue<ForceAtPos> rotPosForces = Queues.newConcurrentLinkedQueue();

    @JsonIgnore private double Mass = 0;
    @JsonIgnore public boolean Enabled = false;

    //private void setShip(ServerShip ship){
        //this.ship = ship;
    //}

    public static QueuedForceApplier getOrCreate(@NotNull ServerShip ship){
        QueuedForceApplier obj = ship.getAttachment(QueuedForceApplier.class);
        if(obj == null) {
            obj = new QueuedForceApplier();
            ship.saveAttachment(QueuedForceApplier.class, obj);
        }
        return obj;
    }

    @JsonIgnore PhysShip physShip;
    @JsonIgnore Vector3d velocity = new Vector3d();
    @JsonIgnore Vector3d omega = new Vector3d();
    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        Mass = ((PhysShipImpl)physShip).getInertia().getShipMass();
        this.physShip = physShip;
        PoseVel pv = ((PhysShipImpl) physShip).getPoseVel();
        velocity.set(pv.getVel());
        omega.set(pv.getOmega());
        if(!Enabled) {
            invForces.clear();
            invTorques.clear();
            invPosForces.clear();
            rotForces.clear();
            rotTorques.clear();
            rotPosForces.clear();
            return;
        }
        pollUntilEmpty(invForces, this::_applyInvariantForce);
        pollUntilEmpty(invTorques, this::_applyInvariantTorque);
        pollUntilEmpty(invPosForces ,this::_applyInvariantForceToPos);
        pollUntilEmpty(rotForces, this::_applyRotDependentForce);
        pollUntilEmpty(rotTorques, this::_applyRotDependentTorque);
        pollUntilEmpty(rotPosForces, this::_applyRotDependentForceToPos);
    }

    double maxValue = Double.MAX_VALUE;
    public boolean _checkVelocity(Vector3dc f){
        return velocity.lengthSquared() > maxValue || !f.isFinite();
    }

    public boolean _checkOmega(Vector3dc f){
        return omega.lengthSquared() > maxValue || !f.isFinite();
    }

    public boolean _applyInvariantForce(Vector3dc f){
        if(_checkVelocity(f)){
            return true;
        }
        physShip.applyInvariantForce(f);
        return false;
    }
    public boolean _applyInvariantTorque(Vector3dc f){
        if(_checkOmega(f)){
            return true;
        }
        physShip.applyInvariantTorque(f);
        return false;
    }
    public boolean _applyInvariantForceToPos(ForceAtPos f){
        if(_checkVelocity(f.force) || !f.pos.isFinite()){
            return true;
        }
        physShip.applyInvariantForceToPos(f.force, f.pos);
        return false;
    }

    public boolean _applyRotDependentForce(Vector3dc f){
        if(_checkVelocity(f)){
            return true;
        }
        physShip.applyRotDependentForce(f);
        return false;
    }
    public boolean _applyRotDependentTorque(Vector3dc f){
        if(_checkOmega(f)){
            return true;
        }
        physShip.applyRotDependentTorque(f);
        return false;
    }
    public boolean _applyRotDependentForceToPos(ForceAtPos f){
        if(_checkVelocity(f.force) || !f.pos.isFinite()){
            return true;
        }
        physShip.applyRotDependentForceToPos(f.force, f.pos);
        return false;
    }

    private <T> void pollUntilEmpty(Queue<T> queue, Function<T, Boolean> consumer){
        T elem;
        while (!queue.isEmpty()){
            elem = queue.poll();
            if(consumer.apply(elem)) return;
        }
    }

    public double getShipMass(){
        return Mass;
    }

    public void  applyInvariantForce(Vector3dc force) {
        invForces.add(force);
    }

    public void  applyInvariantTorque(Vector3dc torque) {
        invTorques.add(torque);
    }

    public void  applyInvariantForceToPos(Vector3dc force,Vector3dc pos) {
        invPosForces.add(new ForceAtPos(force, pos));
    }

    public void  applyRotDependentForce(Vector3dc force) {
        rotForces.add(force);
    }

    public void  applyRotDependentTorque(Vector3dc torque) {
        rotTorques.add(torque);
    }

    public void  applyRotDependentForceToPos(Vector3dc force, Vector3dc pos) {
        rotPosForces.add(new ForceAtPos(force, pos));
    }

    private record ForceAtPos(Vector3dc force, Vector3dc pos){
    }
}
