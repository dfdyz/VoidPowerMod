package com.dfdyz.void_power.compat.vs.ship;

import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineController implements ShipForcesInducer {
    //@JsonIgnore
    //ServerShip ship;
    //@JsonIgnore
    //QueuedForceApplier applier;
    @JsonIgnore
    Set<EngineControllerTE> controller = Sets.newConcurrentHashSet();
    @JsonIgnore
    Set<VoidEngineTE> engine = Sets.newConcurrentHashSet();



    @JsonIgnore
    boolean canDrive = false;

    @JsonIgnore
    double massCanDrive = 0;

    @JsonIgnore
    boolean idle_mode = true;

    boolean disable_idle = false;
    //public long shipId = -1;

    //private void setShip(ServerShip ship){
        //this.ship = ship;
        //this.shipId = ship.getId();
    //}

    public static EngineController getOrCreate(@NotNull ServerShip ship){
        EngineController obj = ship.getAttachment(EngineController.class);
        if(obj == null){
            obj = new EngineController();
            ship.saveAttachment(EngineController.class, obj);
        }
        QueuedForceApplier.getOrCreate(ship);
        return obj;
    }

    public void addController(EngineControllerTE te){
        this.controller.add(te);
    }

    public void removeController(EngineControllerTE te){
        if(controller.contains(te))
            this.controller.remove(te);
    }

    public void addEngine(VoidEngineTE te){
        this.engine.add(te);
    }

    public void removeEngine(VoidEngineTE te){
        if(engine.contains(te))
            this.engine.remove(te);
    }

    @JsonIgnore
    private double mass;

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        // System.out.println("AAAAAAAA");
        this.controller.removeIf((e) -> {
            return e.isRemoved();
        });

        this.controller.forEach((e) -> {
            e.PushCCEvent((PhysShipImpl) physShip);
        });

        this.engine.removeIf((e) -> {
            return e.isRemoved();
        });

        mass = ((PhysShipImpl) physShip).getInertia().getShipMass();
        AtomicReference<Double> mass_driver = new AtomicReference<>((double) 0);

        this.engine.forEach((e) -> {
            mass_driver.updateAndGet(v -> {
                return v + e.massCanDrive();
            });
        });
        massCanDrive = mass_driver.get();
        canDrive = mass <= massCanDrive;

        if(idle_mode && canDrive && !controller.isEmpty()){
            IDLE((PhysShipImpl)physShip);
        }
    }

    public boolean isIdle(){
        return idle_mode;
    }

    public void setIdle(boolean b){
        idle_mode = b;
    }

    public void disableIdle(boolean b){
        disable_idle = b;
    }

    private void IDLE(PhysShipImpl physShip){
        Vector3d omega = physShip.getPoseVel().getOmega().negate(new Vector3d()).mul(10);

        Vector3d force = physShip.getPoseVel().getVel().negate(new Vector3d()).mul(mass*1.5).add(0,mass * 10,0);

        Vector3d torque = physShip.getInertia().getMomentOfInertiaTensor().transform(omega);

        physShip.applyInvariantForce(force);
        physShip.applyInvariantTorque(torque);
    }

    public boolean canDrive(){
        return canDrive;
    }

    public double massCanDrive(){
        return massCanDrive;
    }
}
