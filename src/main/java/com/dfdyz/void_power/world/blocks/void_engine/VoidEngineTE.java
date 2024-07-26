package com.dfdyz.void_power.world.blocks.void_engine;

import com.dfdyz.void_power.Config;
import com.dfdyz.void_power.compat.vs.ship.EngineController;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class VoidEngineTE extends KineticBlockEntity{

    public VoidEngineTE(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    private ServerShip ship;

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) return;
        if(ship == null){
            ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) level, getBlockPos());
        }

        if(ship != null){
            EngineController ec = EngineController.getOrCreate(ship);
            ec.addEngine(this);
        }

        calculateStressApplied();
        updateSpeed = true;
    }

    private float STRESS = 0;
    @Override
    public float calculateStressApplied() {
        STRESS = (float) Config.StressPerRPM;
        this.lastStressApplied = STRESS;
        return STRESS;
    }

    public double massCanDrive(){
        //System.out.println(STRESS * Config.MassPerStress * Mth.abs(getSpeed()));
        return STRESS * Config.MassPerStress * Mth.abs(getSpeed());
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if(level.isClientSide) return;
        if(ship != null){
            EngineController ec = EngineController.getOrCreate(ship);
            ec.removeEngine(this);
        }
    }

    @Override
    public void remove() {
        super.remove();

        if(level.isClientSide) return;
        if(ship != null){
            EngineController ec = EngineController.getOrCreate(ship);
            ec.removeEngine(this);
        }
    }
}
