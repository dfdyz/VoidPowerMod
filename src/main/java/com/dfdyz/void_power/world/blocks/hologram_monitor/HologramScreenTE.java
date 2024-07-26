package com.dfdyz.void_power.world.blocks.hologram_monitor;

import com.dfdyz.void_power.compat.cct.peripherals.HologramScreenPeripheral;
import com.dfdyz.void_power.mixin.IMonitorTEAccessor;
import com.dfdyz.void_power.patched.IPatchedNetTermAccessor;
import com.dfdyz.void_power.patched.IPatchedTermAccessor;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.peripheral.monitor.MonitorPeripheral;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HologramScreenTE extends MonitorBlockEntity{

    //private boolean EnableTransparent = true;
    //private char TransparentIndex = 'f';

    public HologramScreenTE(BlockEntityType<? extends MonitorBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, true);
    }

    @Override
    public IPeripheral peripheral() {
        ((IMonitorTEAccessor)this).Invoke_createServerTerminal();
        MonitorPeripheral peripheral = ((IMonitorTEAccessor)this).getPrivatePeripheral();
        if(peripheral == null){
            peripheral = new HologramScreenPeripheral(this);
            ((IMonitorTEAccessor)this).setPrivatePeripheral(peripheral);
        }
        ((IMonitorTEAccessor)this).Invoke_assertInvariant();
        return peripheral;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getTransMode(){
        ClientMonitor clientMonitor = getOriginClientMonitor();
        if(clientMonitor != null && clientMonitor.getTerminal() != null){
            //System.out.println("_A");
            return ((IPatchedTermAccessor) clientMonitor.getTerminal()).void_power$GetTransMode();
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public char getTransparentIndex(){
        ClientMonitor clientMonitor = getOriginClientMonitor();
        if(clientMonitor != null && clientMonitor.getTerminal() != null){
            //System.out.println("_A");
            return ((IPatchedTermAccessor) clientMonitor.getTerminal()).void_power$GetTransColor();
        }
        return 'f';
    }

    public void SetTrans(boolean t){
        if(!level.isClientSide) {
            ServerMonitor serverMonitor = ((IMonitorTEAccessor)this).Invoke_getServerMonitor();
            if(serverMonitor != null){
                IPatchedNetTermAccessor PATCH = (IPatchedNetTermAccessor) serverMonitor.getTerminal();
                if (PATCH != null) {
                    PATCH.void_power$SetTransMode2(t);
                }
            }
        }
    }

    public void SetTransCol(char c){
        if(!level.isClientSide) {
            ServerMonitor serverMonitor = ((IMonitorTEAccessor)this).Invoke_getServerMonitor();
            if(serverMonitor != null){
                //System.out.println("A");
                IPatchedNetTermAccessor PATCH = (IPatchedNetTermAccessor) serverMonitor.getTerminal();
                if (PATCH != null) {
                    //System.out.println("B");
                    PATCH.void_power$SetTransColor2(c);
                }
            }
        }
    }

    private LazyOptional<IPeripheral> peripheralCap;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL){
            if(peripheralCap == null || !peripheralCap.isPresent())
                peripheralCap =  LazyOptional.of(this::peripheral);
            return peripheralCap.cast();
        }
        return super.getCapability(cap, side);
    }

    boolean TChanged = false;

    @OnlyIn(Dist.CLIENT)
    public boolean pollChange(){
        boolean b = TChanged;
        TChanged = false;
        return b;
    }

    public void setTChanged(){
        //System.out.println("Recd: " + EnableTransparent + ", " + TransparentIndex + ", " + getBlockPos());
        TChanged = true;
    }
}
