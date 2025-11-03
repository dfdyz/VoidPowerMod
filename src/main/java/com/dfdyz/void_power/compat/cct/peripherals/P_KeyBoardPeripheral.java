package com.dfdyz.void_power.compat.cct.peripherals;

import com.dfdyz.void_power.world.blocks.key_board.KeyBoardTE;
import com.google.common.collect.Sets;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class P_KeyBoardPeripheral implements IPeripheral {
    final Set<IComputerAccess> computers = Sets.newConcurrentHashSet();

    final KeyBoardTE te;
    public P_KeyBoardPeripheral(KeyBoardTE te){
        this.te = te;
    }

    @LuaFunction
    @Override
    public String getType() {
        return "keyboard";
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    public void PushEvent(String event, Object... data){
        computers.forEach((computer) ->{
            computer.queueEvent(event, data);
        });
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }
}
