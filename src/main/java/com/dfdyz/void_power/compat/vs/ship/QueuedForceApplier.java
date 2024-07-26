package com.dfdyz.void_power.compat.vs.ship;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Queues;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.util.VSCoreUtilKt;

import java.util.Queue;
import java.util.function.Consumer;

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

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        Mass = ((PhysShipImpl)physShip).getInertia().getShipMass();
        if(!Enabled) {
            invForces.clear();
            invTorques.clear();
            invPosForces.clear();
            rotForces.clear();
            rotTorques.clear();
            rotPosForces.clear();
            return;
        }
        pollUntilEmpty(invForces, physShip::applyInvariantForce);
        pollUntilEmpty(invTorques, physShip::applyInvariantTorque);
        pollUntilEmpty(invPosForces , (pos) -> physShip.applyInvariantForceToPos(pos.force, pos.pos) );
        pollUntilEmpty(rotForces,physShip::applyRotDependentForce);
        pollUntilEmpty(rotTorques,physShip::applyRotDependentTorque);
        pollUntilEmpty(rotPosForces, (pos) -> physShip.applyRotDependentForceToPos(pos.force, pos.pos) );
    }

    private <T> void pollUntilEmpty(Queue<T> queue, Consumer<T> consumer){
        T elem;
        while (!queue.isEmpty()){
            elem = queue.poll();
            consumer.accept(elem);
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
