package com.dfdyz.void_power.compat.cct.peripherals;

import com.dfdyz.void_power.utils.ParamUtils;
import com.dfdyz.void_power.world.blocks.redstone_link.RSRouterTE;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/*
* 无限红石路由器 外设实现
*/
public class P_RSRouter implements IPeripheral {
    RSRouterTE te;

    public P_RSRouter(RSRouterTE e){
        te =e;
    }

    @LuaFunction
    public boolean openPort(int port_id, String channel, boolean mode) throws LuaException {
        if(!ParamUtils.checkChannel(channel)) return false;
        return te.openPort(port_id, new ResourceLocation(channel), mode);
    }
    @LuaFunction
    public void closePort(int port_id) throws LuaException {
        te.closePort(port_id);
    }

    @LuaFunction
    public int getPortMode(int port_id) throws LuaException {
        return te.getPortMode(port_id);
    }

    @LuaFunction
    public int getPower(int port_id) throws LuaException {
        return te.getPower(port_id);
    }

    @LuaFunction
    public boolean setPower(int port_id, int power) throws LuaException {
        return te.setPower(port_id, power);
    }

    @LuaFunction(mainThread = true)
    public void process(){
        te.process();
    }

    @Override
    public String getType() {
        return "redstone_router";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }
}
