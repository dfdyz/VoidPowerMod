package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.client.renderer.tileentities.hologram_monitor.HologramScreenInstance;
import com.dfdyz.void_power.client.renderer.tileentities.hologram_monitor.HologramScreenRenderer;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.dfdyz.void_power.world.blocks.hologram_monitor.HologramScreenBlock;
import com.dfdyz.void_power.world.blocks.hologram_monitor.HologramScreenTE;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineBlock;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.dfdyz.void_power.client.renderer.tileentities.void_engine.VoidEngineTEInstance;
import com.dfdyz.void_power.client.renderer.tileentities.void_engine.VoidEngineTERenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.dfdyz.void_power.VoidPowerMod.REGISTRATE;

public class VPTileEntities {


    public static final BlockEntityEntry<VoidEngineTE> VOID_ENGINE_TE = REGISTRATE
            .blockEntity(VoidEngineBlock.ID, VoidEngineTE::new)
            .instance(() -> VoidEngineTEInstance::new, false)
            .validBlock(VPBlocks.VOID_ENGINE)
            .renderer(() -> VoidEngineTERenderer::new)
            .register();

    public static final BlockEntityEntry<EngineControllerTE> ENGINE_CONTROLLER_TE = REGISTRATE
            .blockEntity(EngineControllerBlock.ID, EngineControllerTE::new)
            .validBlock(VPBlocks.ENGINE_CONTROLLER_BLOCK)
            .register();

    public static final BlockEntityEntry<HologramScreenTE> HOLOGRAM_SCREEN_TE = REGISTRATE
            .blockEntity(HologramScreenBlock.ID, HologramScreenTE::new)
            .instance(() -> HologramScreenInstance::new, true)
            .validBlock(VPBlocks.HOLOGRAM_SCREEN_BLOCK)
            .renderer(() -> HologramScreenRenderer::new)
            .register();

    public static void register(){

    }




}
