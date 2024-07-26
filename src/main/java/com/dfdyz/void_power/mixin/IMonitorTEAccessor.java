package com.dfdyz.void_power.mixin;

import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.peripheral.monitor.MonitorPeripheral;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MonitorBlockEntity.class, remap = false)
public interface IMonitorTEAccessor {

    @Accessor("peripheral")
    MonitorPeripheral getPrivatePeripheral();

    @Accessor("peripheral")
    void setPrivatePeripheral(MonitorPeripheral value);

    @Invoker("createServerTerminal")
    void Invoke_createServerTerminal();

    @Invoker("assertInvariant")
    void Invoke_assertInvariant();

    @Invoker("getOrigin")
    MonitorBlockEntity Invoke_getOrigin();

    @Invoker("getServerMonitor")
    ServerMonitor Invoke_getServerMonitor();

}
