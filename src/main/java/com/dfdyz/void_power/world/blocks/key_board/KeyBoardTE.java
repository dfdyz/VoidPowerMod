package com.dfdyz.void_power.world.blocks.key_board;

import com.dfdyz.void_power.compat.cct.peripherals.P_KeyBoardPeripheral;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeyBoardTE extends SmartBlockEntity {

    public KeyBoardTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public P_KeyBoardPeripheral peripheral;
    protected LazyOptional<IPeripheral> peripheralCap;

    MonitorBlockEntity monitor_proxy;

    public P_KeyBoardPeripheral getPeripheral(){
        if(peripheral == null){
            peripheral = new P_KeyBoardPeripheral(this);
        }
        return peripheral;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction direction) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            if(peripheralCap == null || !peripheralCap.isPresent())
                peripheralCap =  LazyOptional.of(this::getPeripheral);
            return peripheralCap.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {

    }

    @Override
    public void tick() {
        super.tick();

        if(monitor_proxy == null || monitor_proxy.isRemoved()) {
            monitor_proxy = null;
            return;
        }

        // todo: use monitor as a hub
        //if(monitor_proxy.)

    }

    public void PushEvent(String event, Object... data){
        if (peripheral != null){
            peripheral.PushEvent(event, data);
        }
    }
}
